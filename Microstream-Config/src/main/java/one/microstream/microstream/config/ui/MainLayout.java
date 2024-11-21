
package one.microstream.microstream.config.ui;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.rapidclipse.framework.server.resources.CaptionUtils;
import com.rapidclipse.framework.server.security.authentication.RedirectView;
import com.rapidclipse.framework.server.ui.ItemLabelGeneratorFactory;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
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
import one.microstream.microstream.config.core.Executable;
import one.microstream.microstream.config.core.MockupData;
import one.microstream.microstream.config.core.TaskRunner;
import one.microstream.microstream.config.domain.Action;
import one.microstream.microstream.config.dto.DTOAuthor;
import one.microstream.microstream.config.dto.DTOBook;

@RedirectView
@Route("/")
public class MainLayout extends VerticalLayout
{
	private static final Logger LOG = LoggerFactory.getLogger(MainLayout.class);

	private static final String BOOKS_BY_TITLE = "Books by Random Title Search";
	private static final String BOOKS_BY_ISBN = "Books by Random ISBNs";
	private static final String CREATE_DATA = "Generate Data";
	private static final String CLEAR_DATA = "Clear Data";
	private static final String ECLIPSESTORE = "EclipseStore";
	private static final String POSTGRES = "Postgres";

	private final Random random = new Random();
	private final List<DTOBook> books;

	private final BookstoreHttpClient pgHttpClient = new BookstoreHttpClient("http://localhost:8081");
	private final BookstoreHttpClient esHttpClient = new BookstoreHttpClient("http://localhost:8082");

	private TaskRunner taskRunner;
	/**
	 * Used to toggle between using the postgres and eclipsestore http client
	 */
	private boolean httpClientToggle;

	public MainLayout() throws IOException
	{
		super();
		this.initUI();
		this.radioButtonGroup.setItems(CREATE_DATA, BOOKS_BY_TITLE, BOOKS_BY_ISBN, CLEAR_DATA);
		this.radioButtonGroup.setValue(CREATE_DATA);
		this.cmbTargets.setItems(ECLIPSESTORE, POSTGRES);
		this.cmbTargets.select(ECLIPSESTORE, POSTGRES);

		this.books = MockupData.loadData();
	}

	private BookstoreHttpClient nextHttpClient()
	{
		final Set<String> targets = this.cmbTargets.getSelectedItems();
		if (targets.size() == 2)
		{
			// If both are selected, switch between both on each call
			this.httpClientToggle = !this.httpClientToggle;
			if (this.httpClientToggle)
			{
				return this.pgHttpClient;
			}
			else
			{
				return this.esHttpClient;
			}
		}

		return targets.stream().findFirst().map(target ->
		{
			switch (target)
			{
			case ECLIPSESTORE:
				return this.esHttpClient;
			case POSTGRES:
				return this.pgHttpClient;
			default:
				throw new IllegalArgumentException("Unknown target type: " + target);
			}
		}).orElseThrow(() -> new RuntimeException("Target type is empty"));
	}

	/**
	 * Event handler delegate method for the {@link Button} {@link #btnStart}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnStart_onClick(final ClickEvent<Button> event)
	{
		this.btnStart.setEnabled(false);
		this.btnStop.setEnabled(true);

		// Calling tasks alternate calling from postgres and eclipsestore
		final Supplier<Executable> taskGenerator = switch (this.radioButtonGroup.getValue())
		{
		case BOOKS_BY_TITLE -> new Supplier<>()
		{
			private final Random seededRandom = new Random(rangeRandomSeed.getValue().longValue());

			@Override
			public Executable get()
			{
				final String search = Character.toString((char)seededRandom.nextInt('a', 'z' + 1));
				final var httpClient = nextHttpClient();
				return () -> httpClient.searchByTitle(search);
			}
		};
		case BOOKS_BY_ISBN -> new Supplier<>()
		{
			private final List<String> pgAuthors = pgHttpClient.listAuthors(10).stream().map(DTOAuthor::mail).toList();
			private final List<String> esAuthors = esHttpClient.listAuthors(10).stream().map(DTOAuthor::mail).toList();

			{
				LOG.info("Creating the generator");
			}

			@Override
			public Executable get()
			{
				LOG.info("Calling get on generator");
				boolean callEs;

				final Set<String> targets = cmbTargets.getValue();
				if (targets.size() == 2)
				{
					httpClientToggle = !httpClientToggle;
					callEs = httpClientToggle;
				}
				else
				{
					callEs = targets.contains(ECLIPSESTORE);
				}

				return () ->
				{
					LOG.info("Calling run on task");
					final BookstoreHttpClient client;
					final List<String> authors;

					if (callEs)
					{
						client = esHttpClient;
						authors = esAuthors;
					}
					else
					{
						client = pgHttpClient;
						authors = pgAuthors;
					}

					final var randomAuthor = authors.get(random.nextInt(pgAuthors.size()));
					LOG.info("Searching author {}", randomAuthor);
					client.searchByAuthor(randomAuthor);
				};
			}
		};
		case CREATE_DATA -> () ->
		{
			final var httpClient = nextHttpClient();
			return () ->
			{
				for (List<DTOBook> batch : Lists.partition(books, 1_000))
				{
					httpClient.createBookBatched(batch);

					if (Thread.interrupted())
					{
						throw new InterruptedException();
					}
				}
			};
		};
		case CLEAR_DATA -> () ->
		{
			final var httpClient = nextHttpClient();
			return httpClient::clearBooks;
		};
		default -> throw new RuntimeException("Encountered unexpected task name");
		};

		executeTask(taskGenerator);
	}

	private void executeTask(Supplier<Executable> taskGenerator)
	{
		final int threadCount = this.rangeAmountThreads.getValue().intValue();
		final int delayMs = this.rangeSendDelay.getValue().intValue();
		final int runCount = this.rangeRunCount.getValue().intValue();

		this.taskRunner = new TaskRunner(threadCount, runCount, delayMs, taskGenerator, this::onTasksFinished);
		this.taskRunner.start();
	}

	private void onTasksFinished()
	{
		// Needs to be a new thread as this is called inside a task thread
		new Thread(() ->
		{
			try
			{
				this.getUI().ifPresent(ui -> ui.access(() -> this.btnStop_onClick(null)));
			}
			catch (final Exception e)
			{
				LOG.error("Failed to update ui", e);
			}
		}).start();
	}

	/**
	 * Event handler delegate method for the {@link Button} {@link #btnStop}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnStop_onClick(final ClickEvent<Button> unused)
	{
		if (this.taskRunner != null)
		{
			this.taskRunner.stop();
			this.taskRunner.waitForTasks();
		}

		this.taskRunner = null;

		this.btnStart.setEnabled(!this.cmbTargets.getValue().isEmpty());
		this.btnStop.setEnabled(false);
	}

	/**
	 * Event handler delegate method for the {@link MultiSelectComboBox}
	 * {@link #cmbTargets}.
	 *
	 * @see HasValue.ValueChangeListener#valueChanged(HasValue.ValueChangeEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void cmbTargets_valueChanged(
		final ComponentValueChangeEvent<MultiSelectComboBox<String>, Set<String>> event
	)
	{
		this.btnStart.setEnabled(!event.getValue().isEmpty());
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
		this.btnStart = new Button();
		this.btnStop = new Button();
		this.hr = new Hr();
		this.radioButtonGroup = new RadioButtonGroup<>();
		this.cmbTargets = new MultiSelectComboBox<>();
		this.hr2 = new Hr();
		this.rangeAmountThreads = new NumberField();
		this.rangeRunCount = new NumberField();
		this.rangeSendDelay = new NumberField();
		this.hr3 = new Hr();
		this.rangeRandomSeed = new NumberField();
		this.grid = new Grid<>(Action.class, false);

		this.verticalLayout.setSpacing(false);
		this.verticalLayout.setPadding(false);
		this.btnStart.setText("Start");
		this.btnStart.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		this.btnStart.setIcon(VaadinIcon.PLAY.create());
		this.btnStop.setEnabled(false);
		this.btnStop.setText("Stop");
		this.btnStop.addThemeVariants(ButtonVariant.LUMO_ERROR);
		this.btnStop.setIcon(VaadinIcon.STOP.create());
		this.radioButtonGroup.setRenderer(
			new TextRenderer<>(ItemLabelGeneratorFactory.NonNull(CaptionUtils::resolveCaption))
		);
		this.radioButtonGroup.addThemeName("vertical");
		this.cmbTargets.setAutoExpand(MultiSelectComboBox.AutoExpandMode.BOTH);
		this.cmbTargets.setLabel("Task Targets");
		this.cmbTargets.setItemLabelGenerator(ItemLabelGeneratorFactory.NonNull(CaptionUtils::resolveCaption));
		this.rangeAmountThreads.setValue(1.0);
		this.rangeAmountThreads.setLabel("Amount Threads");
		this.rangeRunCount.setValue(1.0);
		this.rangeRunCount.setLabel("Run Count");
		this.rangeSendDelay.setValue(0.0);
		this.rangeSendDelay.setLabel("Send Delay (ms)");
		this.rangeRandomSeed.setValue(123456.0);
		this.rangeRandomSeed.setLabel("Random Seed");
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

		this.btnStart.setWidthFull();
		this.btnStart.setHeight(null);
		this.btnStop.setWidthFull();
		this.btnStop.setHeight(null);
		this.hr.setSizeUndefined();
		this.radioButtonGroup.setSizeUndefined();
		this.cmbTargets.setWidthFull();
		this.cmbTargets.setHeight(null);
		this.hr2.setSizeUndefined();
		this.rangeAmountThreads.setWidthFull();
		this.rangeAmountThreads.setHeight(null);
		this.rangeRunCount.setWidthFull();
		this.rangeRunCount.setHeight(null);
		this.rangeSendDelay.setWidthFull();
		this.rangeSendDelay.setHeight(null);
		this.hr3.setSizeUndefined();
		this.rangeRandomSeed.setWidthFull();
		this.rangeRandomSeed.setHeight(null);
		this.verticalLayout.add(
			this.btnStart,
			this.btnStop,
			this.hr,
			this.radioButtonGroup,
			this.cmbTargets,
			this.hr2,
			this.rangeAmountThreads,
			this.rangeRunCount,
			this.rangeSendDelay,
			this.hr3,
			this.rangeRandomSeed
		);
		this.verticalLayout.setWidth("300px");
		this.verticalLayout.setHeightFull();
		this.grid.setWidth(null);
		this.grid.setHeightFull();
		this.horizontalLayout.add(this.verticalLayout, this.grid);
		this.horizontalLayout.setSizeFull();
		this.div.add(this.horizontalLayout);
		this.div.setWidth("90%");
		this.div.setHeight("80%");
		this.add(this.div);
		this.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, this.div);
		this.setSizeFull();

		this.btnStart.addClickListener(this::btnStart_onClick);
		this.btnStop.addClickListener(this::btnStop_onClick);
		this.cmbTargets.addValueChangeListener(this::cmbTargets_valueChanged);
	} // </generated-code>

	// <generated-code name="variables">
	private Button btnStart, btnStop;
	private Grid<Action> grid;
	private NumberField rangeAmountThreads, rangeRunCount, rangeSendDelay, rangeRandomSeed;
	private HorizontalLayout horizontalLayout;
	private VerticalLayout verticalLayout;
	private Div div;
	private Hr hr, hr2, hr3;
	private RadioButtonGroup<String> radioButtonGroup;
	private MultiSelectComboBox<String> cmbTargets;
	// </generated-code>

}
