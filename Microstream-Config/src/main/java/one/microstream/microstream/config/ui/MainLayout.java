
package one.microstream.microstream.config.ui;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.rapidclipse.framework.server.Rap;
import com.rapidclipse.framework.server.resources.CaptionUtils;
import com.rapidclipse.framework.server.security.authentication.RedirectView;
import com.rapidclipse.framework.server.ui.ItemLabelGeneratorFactory;
import com.rapidclipse.framework.server.ui.UIUtils;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.SelectedChangeEvent;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;

import one.microstream.microstream.config.core.BookstoreHttpClient;
import one.microstream.microstream.config.core.Executable;
import one.microstream.microstream.config.core.MockupData;
import one.microstream.microstream.config.core.TaskRunner;
import one.microstream.microstream.config.core.task.AdminClearBooksTask;
import one.microstream.microstream.config.core.task.AdminCreateBooksTask;
import one.microstream.microstream.config.core.task.BookByIsbnTask;
import one.microstream.microstream.config.core.task.BooksByTitleTask;
import one.microstream.microstream.config.core.task.CreateBookTask;
import one.microstream.microstream.config.core.task.ResetInsertBookPMTask;
import one.microstream.microstream.config.dto.DTOBook;
import one.microstream.microstream.config.ui.detail.ViewOverview;

@RedirectView
@Route("/")
public class MainLayout extends HorizontalLayout implements AfterNavigationObserver
{
	private static final Logger LOG = LoggerFactory.getLogger(MainLayout.class);

	private static final String BOOKS_BY_TITLE = "Books by Random Title Search";
	private static final String BOOKS_BY_ISBN = "Books by Random ISBNs";
	private static final String INSERT_BOOKS = "Insert books";
	private static final String CREATE_DATA = "Generate Data";
	private static final String CLEAR_DATA = "Clear Data";
	private static final String ECLIPSESTORE = "EclipseStore";
	private static final String POSTGRES = "Postgres";

	private static List<DTOBook> books;

	private final BookstoreHttpClient pgHttpClient = new BookstoreHttpClient(
		Optional.ofNullable(System.getenv("POSTGRES_URL")).orElse("http://localhost:8081")
	);
	private final BookstoreHttpClient esHttpClient = new BookstoreHttpClient(
		Optional.ofNullable(System.getenv("ECLIPSESTORE_URL")).orElse("http://localhost:8082")
	);

	private TaskRunner taskRunner;

	public MainLayout()
	{
		super();
		this.initUI();

		this.radioButtonGroup.setItems(BOOKS_BY_TITLE, BOOKS_BY_ISBN, INSERT_BOOKS);
		this.radioButtonGroup.setValue(BOOKS_BY_TITLE);
		this.cmbTargets.setItems(ECLIPSESTORE, POSTGRES);
		this.cmbTargets.select(ECLIPSESTORE, POSTGRES);

		tabs.setSelectedIndex(0);

		containerProgress.setVisible(false);
	}

	/**
	 * Event handler delegate method for the {@link Button} {@link #btnStart}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnStart_onClick(final ClickEvent<Button> event)
	{
		btnStart.setEnabled(false);
		btnStop.setEnabled(true);
		UI.getCurrent().push();
		
		final Set<String> targets = this.cmbTargets.getSelectedItems();
		Supplier<Executable> generator = switch (targets.size())
		{
		case 1 ->
		{
			final var task = createTask(targets.contains(ECLIPSESTORE) ? esHttpClient : pgHttpClient);
			yield () -> task;
		}
		case 2 -> new Supplier<Executable>()
		{
			private final Executable pgTask = createTask(pgHttpClient);
			private final Executable esTask = createTask(esHttpClient);

			// Toggle between using the postgres and eclipsestore http client
			private boolean toggle = false;

			@Override
			public Executable get()
			{
				toggle = !toggle;
				return toggle ? pgTask : esTask;
			}
		};
		default -> throw new RuntimeException("Unexpected amount of targets");
		};

		executeTask(generator);
	}

	private Executable createTask(BookstoreHttpClient client)
	{
		final long seed = this.rangeRandomSeed.getValue().longValue();

		// Calling tasks alternate calling from postgres and eclipsestore
		return switch (this.radioButtonGroup.getValue())
		{
		case BOOKS_BY_TITLE -> new BooksByTitleTask(seed, client);
		case BOOKS_BY_ISBN -> new BookByIsbnTask(seed, client, books);
		case INSERT_BOOKS -> new CreateBookTask(seed, client);
		// TODO: Specification missing for what dataset this function should modify
		//		case CREATE_DATA -> Arrays.asList(
		//			new CreateBookTask(seed, esHttpClient),
		//			new CreateBookTask(seed, pgHttpClient)
		//		);
		// TODO: Specification missing for what dataset this function should modify
		//		case CLEAR_DATA -> () ->
		//		{
		//			final var httpClient = nextHttpClient();
		//			return httpClient::clearBooks;
		//		};
		//		case CREATE_ADMIN_DATA -> new Supplier<>()
		//		{
		//			private final int threadCount = rangeAmountThreads.getValue().intValue() / cmbTargets.getSelectedItems()
		//				.size();
		//			private final List<List<DTOBook>> batchedBooks = Lists.partition(BOOKS, 1_000);
		//
		//			private int threadTotal = 0;
		//
		//			@Override
		//			public Executable get()
		//			{
		//				final BookstoreHttpClient httpClient = nextHttpClient();
		//				final int threadNumber = threadTotal++;
		//				return () ->
		//				{
		//					for (int i = 0; i < batchedBooks.size(); i++)
		//					{
		//						if (Thread.interrupted())
		//						{
		//							throw new InterruptedException();
		//						}
		//
		//						if (i % threadCount != threadNumber)
		//						{
		//							continue;
		//						}
		//
		//						httpClient.createBookBatched(batchedBooks.get(i));
		//					}
		//				};
		//			}
		//		};
		default -> throw new RuntimeException("Encountered unexpected task name");
		};
	}

	private void executeTask(Supplier<Executable> taskSupplier)
	{
		final Set<String> targets = this.cmbTargets.getSelectedItems();

		final int threadCount = this.rangeAmountThreads.getValue().intValue();
		final long runCount = ckRunInfinite.getValue() ? Long.MAX_VALUE
			: this.rangeRunCount.getValue().longValue() / threadCount;
		final int delayMs = this.rangeRampUpSeconds.getValue().intValue();

		executeTask(taskSupplier, targets, threadCount, runCount, delayMs);
	}

	private void executeTask(
		Supplier<Executable> taskSupplier,
		Set<String> targets,
		int threadCount,
		long runCount,
		int delayMs
	)
	{
		this.taskRunner = new TaskRunner(
			threadCount * targets.size(),
			runCount,
			delayMs,
			taskSupplier,
			this::onTasksFinished
		);
		this.taskRunner.start();
	}

	private void onTasksFinished()
	{
		// Needs to be a new thread as this is called inside a task thread. Will lock up on shutdown waiting otherwise
		new Thread(() ->
		{
			try
			{
				this.getUI().ifPresent(ui -> ui.access(() ->
				{
					this.btnStop_onClick(null);
					this.btnAdminGenerateData.setEnabled(true);
					this.containerProgress.setVisible(false);
				}));

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

		refreshStartButton();
		this.btnStop.setEnabled(false);
	}

	private void refreshStartButton()
	{
		this.btnStart.setEnabled(!this.cmbTargets.getSelectedItems().isEmpty() && books != null);
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
		refreshStartButton();
	}

	/**
	 * Event handler delegate method for the {@link Button} {@link #btnClearData}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnClearData_onClick(final ClickEvent<Button> event)
	{
		Supplier<Executable> taskSupplier = new Supplier<>()
		{
			private boolean toggle = false;

			@Override
			public Executable get()
			{
				toggle = !toggle;
				var client = toggle ? esHttpClient : pgHttpClient;
				return new AdminClearBooksTask(client);
			}
		};
		this.executeTask(taskSupplier, Set.of(ECLIPSESTORE, POSTGRES), 1, 1, 0);
	}

	/**
	 * Event handler delegate method for the {@link Button}
	 * {@link #btnAdminGenerateData}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnAdminGenerateData_onClick(final ClickEvent<Button> event)
	{
		UI currentUI = UI.getCurrent();

		this.containerProgress.setVisible(true);
		this.txtProgressStatus.setText("Example data creation...");
		this.btnAdminGenerateData.setEnabled(false);
		currentUI.push();

		Rap.getExecutorService().execute(() ->
		{
			int bookCount = rangeAdminDataAmount.getValue();
			books = MockupData.generateData(bookCount, 1_000, 500);

			currentUI.access(() ->
			{
				txtProgressStatus.setText("Created Data storing to DB´s...");
			});

			Supplier<Executable> taskSupplier = new Supplier<>()
			{
				private final List<List<DTOBook>> booksBatched = Lists.partition(books, 1_000);
				private boolean toggle = false;

				@Override
				public Executable get()
				{
					toggle = !toggle;
					var client = toggle ? esHttpClient : pgHttpClient;

					return new AdminCreateBooksTask(client, booksBatched);
				}

			};
			this.executeTask(taskSupplier, Set.of(ECLIPSESTORE, POSTGRES), 1, 1, 0);
		});

	}

	/**
	 * Event handler delegate method for the {@link Checkbox}
	 * {@link #ckRunInfinite}.
	 *
	 * @see HasValue.ValueChangeListener#valueChanged(HasValue.ValueChangeEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void ckRunInfinite_valueChanged(final ComponentValueChangeEvent<Checkbox, Boolean> event)
	{
		boolean runInfinite = event.getValue();
		this.rangeRunCount.setEnabled(!runInfinite);
	}

	/**
	 * Event handler delegate method for the {@link Button} {@link #btnClearBooks}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnClearBooks_onClick(final ClickEvent<Button> event)
	{
		Supplier<Executable> taskSupplier = new Supplier<>()
		{
			private boolean toggle = false;

			@Override
			public Executable get()
			{
				toggle = !toggle;
				var client = toggle ? esHttpClient : pgHttpClient;
				return new ResetInsertBookPMTask(client);
			}
		};
		this.executeTask(taskSupplier, Set.of(ECLIPSESTORE, POSTGRES), 1, 1, 0);
	}

	/**
	 * Event handler delegate method for the {@link Tabs} {@link #tabs}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void tabs_onSelectedChange(final SelectedChangeEvent event)
	{
		if (event.getSelectedTab().equals(tabOverview))
		{
			divContent.removeAll();
			divContent.add(new ViewOverview());
		}
	}

	/*
	 * WARNING: Do NOT edit!<br>The content of this method is always regenerated by
	 * the UI designer.
	 */
	// <generated-code name="initUI">
	private void initUI()
	{
		this.verticalLayout = new VerticalLayout();
		this.btnStart = new Button();
		this.btnStop = new Button();
		this.btnClearBooks = new Button();
		this.hr = new Hr();
		this.radioButtonGroup = new RadioButtonGroup<>();
		this.cmbTargets = new MultiSelectComboBox<>();
		this.hr2 = new Hr();
		this.rangeAmountThreads = new NumberField();
		this.rangeRunCount = new NumberField();
		this.ckRunInfinite = new Checkbox();
		this.rangeRampUpSeconds = new NumberField();
		this.hr3 = new Hr();
		this.rangeRandomSeed = new NumberField();
		this.rangeAdminDataAmount = new IntegerField();
		this.btnAdminGenerateData = new Button();
		this.btnClearData = new Button();
		this.verticalLayout2 = new VerticalLayout();
		this.containerProgress = new VerticalLayout();
		this.txtProgressStatus = new NativeLabel();
		this.progressBar = new ProgressBar();
		this.tabs = new Tabs();
		this.tabOverview = new Tab();
		this.divContent = new Div();
	
		this.verticalLayout.setSpacing(false);
		this.verticalLayout.getStyle().set("overflow-x", "hidden");
		this.verticalLayout.getStyle().set("overflow-y", "auto");
		this.verticalLayout.getStyle().set("border-right", "solid gray 1px");
		this.btnStart.setEnabled(false);
		this.btnStart.setText("Start");
		this.btnStart.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		this.btnStart.setIcon(VaadinIcon.PLAY.create());
		this.btnStop.setEnabled(false);
		this.btnStop.setText("Stop");
		this.btnStop.addThemeVariants(ButtonVariant.LUMO_ERROR);
		this.btnStop.setIcon(VaadinIcon.STOP.create());
		this.btnClearBooks.setText("Reset Insert Test");
		this.btnClearBooks.setMaxWidth("");
		this.btnClearBooks.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.btnClearBooks.setTooltipText("Clears all inserted books from insert performance test.");
		this.btnClearBooks.setIcon(VaadinIcon.TRASH.create());
		this.radioButtonGroup.setRenderer(
			new TextRenderer<>(ItemLabelGeneratorFactory.NonNull(CaptionUtils::resolveCaption))
		);
		this.radioButtonGroup.addThemeName("vertical");
		this.cmbTargets.setAutoExpand(MultiSelectComboBox.AutoExpandMode.BOTH);
		this.cmbTargets.setLabel("Task Targets");
		this.cmbTargets.setItemLabelGenerator(ItemLabelGeneratorFactory.NonNull(CaptionUtils::resolveCaption));
		this.rangeAmountThreads.setValue(1.0);
		this.rangeAmountThreads.setLabel("Amount Threads (per Server)");
		this.rangeRunCount.setValue(1.0);
		this.rangeRunCount.setLabel("Run Count (per Server)");
		this.ckRunInfinite.setLabel("Run Forever");
		this.rangeRampUpSeconds.setValue(0.0);
		this.rangeRampUpSeconds.setLabel("Ramp Up Delay (seconds). 0 = no delay");
		this.rangeRandomSeed.setValue(123456.0);
		this.rangeRandomSeed.setLabel("Random Seed");
		this.rangeAdminDataAmount.setValue(10000);
		this.rangeAdminDataAmount.setLabel("Data Amount (Books)");
		this.btnAdminGenerateData.setText("Generate Data");
		this.btnAdminGenerateData.setTooltipText("Generates random data for later performance tests.");
		this.btnClearData.setText("Clear Data");
		this.btnClearData.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.btnClearData.setTooltipText("Clear the whole database on both servers.");
		this.verticalLayout2.setSpacing(false);
		this.verticalLayout2.setPadding(false);
		this.verticalLayout2.getStyle().set("padding-right", "10px");
		this.containerProgress.setSpacing(false);
		this.containerProgress.setPadding(false);
		this.containerProgress.getStyle().set("margin-top", "10px");
		this.txtProgressStatus.setText("H4");
		this.progressBar.setIndeterminate(true);
		this.progressBar.setValue(0.5);
		this.tabOverview.setLabel("Data Information");
	
		this.btnStart.setWidthFull();
		this.btnStart.setHeight(null);
		this.btnStop.setWidthFull();
		this.btnStop.setHeight(null);
		this.btnClearBooks.setWidthFull();
		this.btnClearBooks.setHeight(null);
		this.hr.setSizeUndefined();
		this.radioButtonGroup.setSizeUndefined();
		this.cmbTargets.setWidthFull();
		this.cmbTargets.setHeight(null);
		this.hr2.setSizeUndefined();
		this.rangeAmountThreads.setWidthFull();
		this.rangeAmountThreads.setHeight(null);
		this.rangeRunCount.setWidthFull();
		this.rangeRunCount.setHeight(null);
		this.ckRunInfinite.setSizeUndefined();
		this.rangeRampUpSeconds.setWidthFull();
		this.rangeRampUpSeconds.setHeight(null);
		this.hr3.setSizeUndefined();
		this.rangeRandomSeed.setWidthFull();
		this.rangeRandomSeed.setHeight(null);
		this.rangeAdminDataAmount.setWidthFull();
		this.rangeAdminDataAmount.setHeight(null);
		this.btnAdminGenerateData.setWidthFull();
		this.btnAdminGenerateData.setHeight(null);
		this.btnClearData.setWidthFull();
		this.btnClearData.setHeight(null);
		this.verticalLayout.add(
			this.btnStart,
			this.btnStop,
			this.btnClearBooks,
			this.hr,
			this.radioButtonGroup,
			this.cmbTargets,
			this.hr2,
			this.rangeAmountThreads,
			this.rangeRunCount,
			this.ckRunInfinite,
			this.rangeRampUpSeconds,
			this.hr3,
			this.rangeRandomSeed,
			this.rangeAdminDataAmount,
			this.btnAdminGenerateData,
			this.btnClearData
		);
		this.txtProgressStatus.setSizeUndefined();
		this.progressBar.setWidthFull();
		this.progressBar.setHeight("15px");
		this.containerProgress.add(this.txtProgressStatus, this.progressBar);
		this.tabs.add(this.tabOverview);
		this.containerProgress.setWidthFull();
		this.containerProgress.setHeight(null);
		this.tabs.setWidthFull();
		this.tabs.setHeight(null);
		this.divContent.setSizeFull();
		this.verticalLayout2.add(this.containerProgress, this.tabs, this.divContent);
		this.verticalLayout2.setFlexGrow(1.0, this.tabs);
		this.verticalLayout.setWidth("400px");
		this.verticalLayout.setHeightFull();
		this.verticalLayout2.setSizeFull();
		this.add(this.verticalLayout, this.verticalLayout2);
		this.setFlexGrow(1.0, this.verticalLayout2);
		this.setSizeFull();
	
		this.tabs.setSelectedIndex(-1);
	
		this.btnStart.addClickListener(this::btnStart_onClick);
		this.btnStop.addClickListener(this::btnStop_onClick);
		this.btnClearBooks.addClickListener(this::btnClearBooks_onClick);
		this.cmbTargets.addValueChangeListener(this::cmbTargets_valueChanged);
		this.ckRunInfinite.addValueChangeListener(this::ckRunInfinite_valueChanged);
		this.btnAdminGenerateData.addClickListener(this::btnAdminGenerateData_onClick);
		this.btnClearData.addClickListener(this::btnClearData_onClick);
		this.tabs.addSelectedChangeListener(this::tabs_onSelectedChange);
	} // </generated-code>

	// <generated-code name="variables">
	private ProgressBar progressBar;
	private Tab tabOverview;
	private NumberField rangeAmountThreads, rangeRunCount, rangeRampUpSeconds, rangeRandomSeed;
	private VerticalLayout verticalLayout, verticalLayout2, containerProgress;
	private Div divContent;
	private Tabs tabs;
	private Hr hr, hr2, hr3;
	private RadioButtonGroup<String> radioButtonGroup;
	private NativeLabel txtProgressStatus;
	private Checkbox ckRunInfinite;
	private Button btnStart, btnStop, btnClearBooks, btnAdminGenerateData, btnClearData;
	private IntegerField rangeAdminDataAmount;
	private MultiSelectComboBox<String> cmbTargets;
	// </generated-code>

	@Override
	public void afterNavigation(AfterNavigationEvent event)
	{
		long countESBooks = esHttpClient.countBooks();
		long countPGBooks = pgHttpClient.countBooks();
		
		if (countPGBooks > 0 && countESBooks > 0)
		{
			btnStart.setEnabled(true);		
		}
	}

	
}
