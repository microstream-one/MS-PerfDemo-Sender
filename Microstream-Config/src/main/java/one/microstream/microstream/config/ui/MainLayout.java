
package one.microstream.microstream.config.ui;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidclipse.framework.server.resources.CaptionUtils;
import com.rapidclipse.framework.server.security.authentication.RedirectView;
import com.rapidclipse.framework.server.ui.ItemLabelGeneratorFactory;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;

import one.microstream.microstream.config.core.BookstoreHttpClient;
import one.microstream.microstream.config.core.HttpTask;
import one.microstream.microstream.config.core.MockupData;
import one.microstream.microstream.config.core.TaskRunner;
import one.microstream.microstream.config.domain.Action;
import one.microstream.microstream.config.dto.DTOAuthor;

@RedirectView
@Route("/")
public class MainLayout extends VerticalLayout
{
	private static final Logger LOG = LoggerFactory.getLogger(MainLayout.class);

	private static final String BOOKS_BY_TITLE = "Books by Title";
	private static final String BOOKS_BY_AUTHOR = "Books by Author";
	private static final String CREATE_ES_DATA = "Generate EclipseStore Data";
	private static final String CREATE_PG_DATA = "Generate Postgres Data";
	private static final String CLEAR_DATA = "Clear Data";

	private final AtomicInteger finishCount = new AtomicInteger(0);
	private final Random random = new Random();
	private final MockupData mockup = new MockupData();

	private final BookstoreHttpClient pgHttpClient = new BookstoreHttpClient("http://localhost:8080");
	private final BookstoreHttpClient esHttpClient = new BookstoreHttpClient("http://localhost:8081");

	private TaskRunner pgTaskRunner;
	private TaskRunner esTaskRunner;

	private UI ui;

	public MainLayout()
	{
		super();
		this.initUI();

		this.radioButtonGroup.setItems(CREATE_ES_DATA, CREATE_PG_DATA, BOOKS_BY_TITLE, BOOKS_BY_AUTHOR, CLEAR_DATA);
		this.radioButtonGroup.setValue(CREATE_ES_DATA);
	}

	/**
	 * Event handler delegate method for the {@link Button} {@link #btnTask}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnTask_onClick(final ClickEvent<Button> event)
	{
		this.btnTask.setEnabled(false);
		this.btnStop.setEnabled(true);

		this.ui = UI.getCurrent();

		switch (this.radioButtonGroup.getValue())
		{
		case BOOKS_BY_TITLE:
		{
			final HttpTask task = httpClient -> mockup.generateBooks(1)
				.forEach(b -> httpClient.searchByTitle("a"));
			executePostgres(task);
			executeEclipsestore(task);
			break;
		}

		case BOOKS_BY_AUTHOR:
		{
			final List<String> pgAuthors;
			try
			{
				pgAuthors = pgHttpClient.listAuthors(10).stream().map(d ->
				{
					System.out.println("Was ist d?");
					return d.mail();
				}
					).toList();
			}
			catch (final Exception e)
			{
				LOG.error("Failed to receive author list", e);
				this.btnStop_onClick(null);
				break;
			}
			LOG.info("AUTHORS: {}", pgAuthors);
			final HttpTask pgTask = httpClient ->
			{
				final var randomAuthor = pgAuthors.get(random.nextInt(pgAuthors.size()));
				LOG.info("Searching author {}", randomAuthor);
				httpClient.searchByAuthor(randomAuthor);
			};
			executePostgres(pgTask);
			break;
		}

		case CREATE_PG_DATA:
		{
			final HttpTask task = httpClient -> mockup.generateBooks(1).forEach(httpClient::createBook);
			executePostgres(task);
			break;
		}

		case CREATE_ES_DATA:
		{
			final HttpTask task = httpClient -> mockup.generateBooks(1).forEach(httpClient::createBook);
			executeEclipsestore(task);
			break;
		}

		default:
			throw new RuntimeException("Encountered unexpected task name");
		}
	}

	private void executeEclipsestore(HttpTask task)
	{
		final int threadCount = this.rangeAmountThreads.getValue().intValue();
		final int delayMs = this.rangeSendDelay.getValue().intValue();
		final int runCount = this.rangeRunCount.getValue().intValue();

		this.esTaskRunner = new TaskRunner(threadCount, runCount, delayMs, task, esHttpClient, this::onTasksFinished);
		this.esTaskRunner.start();
	}

	private void executePostgres(HttpTask task)
	{
		final int threadCount = this.rangeAmountThreads.getValue().intValue();
		final int delayMs = this.rangeSendDelay.getValue().intValue();
		final int runCount = this.rangeRunCount.getValue().intValue();

		this.pgTaskRunner = new TaskRunner(threadCount, runCount, delayMs, task, pgHttpClient, this::onTasksFinished);
		this.pgTaskRunner.start();
	}

	private void onTasksFinished()
	{
		int runningExecutorsCount = 0;
		if (this.pgTaskRunner != null)
		{
			++runningExecutorsCount;
		}
		if (this.esTaskRunner != null)
		{
			++runningExecutorsCount;
		}

		if (this.finishCount.incrementAndGet() == runningExecutorsCount)
		{
			// Needs to be a new thread as this is called inside a task thread
			new Thread(() ->
			{
				try
				{
					this.ui.access(() ->
					{
						this.btnStop_onClick(null);
					});
				}
				catch (final Exception e)
				{
					LOG.error("Failed to update ui", e);
				}
			}).start();
		}
	}

	/**
	 * Event handler delegate method for the {@link Button} {@link #btnStop}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnStop_onClick(final ClickEvent<Button> unused)
	{
		if (this.esTaskRunner != null)
		{
			this.esTaskRunner.stop();
			this.esTaskRunner.waitForTasks();
		}

		if (this.pgTaskRunner != null)
		{
			this.pgTaskRunner.stop();
			this.pgTaskRunner.waitForTasks();
		}

		this.esTaskRunner = null;
		this.pgTaskRunner = null;

		this.finishCount.set(0);

		this.btnTask.setEnabled(true);
		this.btnStop.setEnabled(false);
	}

	/*
	 * WARNING: Do NOT edit!<br>The content of this method is always regenerated by
	 * the UI designer.
	 */
	// <generated-code name="initUI">
	private void initUI()
	{
		this.div = new Div();
		this.horizontalLayout = new HorizontalLayout();
		this.verticalLayout = new VerticalLayout();
		this.btnTask = new Button();
		this.btnStop = new Button();
		this.hr = new Hr();
		this.radioButtonGroup = new RadioButtonGroup<>();
		this.hr2 = new Hr();
		this.nativeLabel4 = new NativeLabel();
		this.rangeAmountThreads = new NumberField();
		this.nativeLabel2 = new NativeLabel();
		this.rangeRunCount = new NumberField();
		this.nativeLabel3 = new NativeLabel();
		this.rangeSendDelay = new NumberField();
		this.hr3 = new Hr();
		this.grid = new Grid<>(Action.class, false);

		this.verticalLayout.setSpacing(false);
		this.verticalLayout.setPadding(false);
		this.btnTask.setText("Tasks");
		this.btnTask.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		this.btnTask.setIcon(VaadinIcon.PLAY.create());
		this.btnStop.setEnabled(false);
		this.btnStop.setText("Stop");
		this.btnStop.addThemeVariants(ButtonVariant.LUMO_ERROR);
		this.btnStop.setIcon(VaadinIcon.STOP.create());
		this.radioButtonGroup.setRenderer(
			new TextRenderer<>(ItemLabelGeneratorFactory.NonNull(CaptionUtils::resolveCaption))
		);
		this.radioButtonGroup.addThemeName("vertical");
		this.nativeLabel4.setText("Amount Threads");
		this.rangeAmountThreads.setValue(1.0);
		this.nativeLabel2.setText("Run Count");
		this.rangeRunCount.setValue(1.0);
		this.nativeLabel3.setText("Send delay (ms)");
		this.rangeSendDelay.setValue(0.0);
		this.grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.grid.addColumn(
			new LocalDateTimeRenderer<>(
				Action::getDate,
				() -> DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT),
				""
			)
		).setKey("date").setHeader("Time").setSortable(true).setAutoWidth(true).setFlexGrow(0);
		this.grid.addColumn(Action::getDescription)
			.setKey("description")
			.setHeader("Action")
			.setSortable(true)
			.setAutoWidth(true);
		this.grid.setSelectionMode(Grid.SelectionMode.SINGLE);

		this.btnTask.setWidthFull();
		this.btnTask.setHeight(null);
		this.btnStop.setWidthFull();
		this.btnStop.setHeight(null);
		this.hr.setSizeUndefined();
		this.radioButtonGroup.setSizeUndefined();
		this.hr2.setSizeUndefined();
		this.nativeLabel4.setSizeUndefined();
		this.rangeAmountThreads.setWidthFull();
		this.rangeAmountThreads.setHeight(null);
		this.nativeLabel2.setSizeUndefined();
		this.rangeRunCount.setWidthFull();
		this.rangeRunCount.setHeight(null);
		this.nativeLabel3.setSizeUndefined();
		this.rangeSendDelay.setWidthFull();
		this.rangeSendDelay.setHeight(null);
		this.hr3.setSizeUndefined();
		this.verticalLayout.add(
			this.btnTask,
			this.btnStop,
			this.hr,
			this.radioButtonGroup,
			this.hr2,
			this.nativeLabel4,
			this.rangeAmountThreads,
			this.nativeLabel2,
			this.rangeRunCount,
			this.nativeLabel3,
			this.rangeSendDelay,
			this.hr3
		);
		this.verticalLayout.setWidth("300px");
		this.verticalLayout.setHeightFull();
		this.grid.setSizeFull();
		this.horizontalLayout.add(this.verticalLayout, this.grid);
		this.horizontalLayout.setFlexGrow(1.0, this.grid);
		this.horizontalLayout.setSizeFull();
		this.div.add(this.horizontalLayout);
		this.div.setWidth("90%");
		this.div.setHeight("80%");
		this.add(this.div);
		this.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, this.div);
		this.setSizeFull();

		this.btnTask.addClickListener(this::btnTask_onClick);
		this.btnStop.addClickListener(this::btnStop_onClick);
	} // </generated-code>

	// <generated-code name="variables">
	private NativeLabel nativeLabel4, nativeLabel2, nativeLabel3;
	private Button btnTask, btnStop;
	private Grid<Action> grid;
	private NumberField rangeAmountThreads, rangeRunCount, rangeSendDelay;
	private HorizontalLayout horizontalLayout;
	private VerticalLayout verticalLayout;
	private Div div;
	private Hr hr, hr2, hr3;
	private RadioButtonGroup<String> radioButtonGroup;
	// </generated-code>

}
