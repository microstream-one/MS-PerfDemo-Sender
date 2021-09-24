
package one.microstream.microstream.config.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.flowingcode.vaadin.addons.ironicons.AvIcons;
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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;

import one.microstream.microstream.config.domain.Action;
import one.microstream.microstream.config.entities.Book;
import one.microstream.microstream.config.entities.BookInShop;
import one.microstream.microstream.config.rest.MSAvgPrice;
import one.microstream.microstream.config.rest.MSBooks;
import one.microstream.microstream.config.rest.MSLazyBooks;
import one.microstream.microstream.config.rest.MSShops;
import one.microstream.microstream.config.rest.PsqlAvgPrice;
import one.microstream.microstream.config.rest.PsqlBooks;
import one.microstream.microstream.config.rest.PsqlLazyBooks;
import one.microstream.microstream.config.rest.PsqlShops;


@RedirectView
@Route("main")
public class MainLayout extends VerticalLayout implements PageConfigurator
{
	
	private String input;
	
	private static final List<Timer>  tasks   = new ArrayList<>();
	private static final List<Action> actions = new ArrayList<>();

	private long msDelay = 250L;
	private long psDelay = 250L;

	public MainLayout()
	{
		super();
		this.initUI();

		this.radioButtonGroup.setItems("Books", "Lazy Books", "avg", "join");
		this.radioButtonGroup.setValue("Books");
		
		if(MainLayout.tasks.size() != 0)
		{
			this.btnStop.setEnabled(true);
			this.btnTask.setEnabled(false);
		}

		try(final PsqlBooks clientPG = new PsqlBooks())
		{
			final Book[] books2 = clientPG.getAllBooks();
		}
		catch(final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void configurePage(final InitialPageSettings settings)
	{
		settings.addLink("shortcut icon", "frontend/images/favicon.ico");
		settings.addFavIcon("icon", "frontend/images/favicon256.png", "256x256");
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
		final TimerTask taskMS1 = new TimerTask()
		{

			@Override
			public void run()
			{
				if(MainLayout.this.input.equals("books"))
				{
					try(MSBooks clientMS = new MSBooks())
					{

						final Book[] books1 = clientMS.getAllBooks();
					}
					catch(final Exception e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call BOOKS MICROSTREAM [1]");
				}
				else if(MainLayout.this.input.equals("avg"))
				{
					try(MSAvgPrice avgclient = new MSAvgPrice())
					{
						final Double avg = avgclient.getAvgBookPrice();
					}
					catch(final Exception e2)
					{
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call AVG MICROSTREAM [1]");
				}
				else if(MainLayout.this.input.equals("join"))
				{
					try(MSShops shopClient = new MSShops())
					{
						final BookInShop[] result = shopClient.getBookInShop();
					}
					catch(final Exception e2)
					{
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call JOIN MICROSTREAM [1]");
				}
				else if(MainLayout.this.input.equals("lazybooks"))
				{
					try(MSLazyBooks clientlazyMS = new MSLazyBooks())
					{
						final Book[] books1 = clientlazyMS.getAllBooks();
					}
					catch(final Exception e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call LAZY BOOKS MICROSTREAM[1]");
				}
				else
				{
					System.out.println("wrong input, terminate app");
					System.exit(0);
				}
				
			}
		};

		final TimerTask taskPG1 = new TimerTask()
		{
			@Override
			public void run()
			{
				if(MainLayout.this.input.equals("books"))
				{

					try(final PsqlBooks clientPG = new PsqlBooks())
					{
						final Book[] books2 = clientPG.getAllBooks();
					}
					catch(final Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call BOOKS POSTGRESQL[1]");
				}
				else if(MainLayout.this.input.equals("avg"))
				{
					try(PsqlAvgPrice avgclient = new PsqlAvgPrice())
					{
						final Double avg = avgclient.getAvgBookPrice();
					}
					catch(final Exception e2)
					{
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call AVG POSTGRESQL[1]");
				}
				else if(MainLayout.this.input.equals("join"))
				{
					try(PsqlShops shopClient = new PsqlShops())
					{
						final BookInShop[] result = shopClient.getBookInShop();
					}
					catch(final Exception e2)
					{
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call JOIN POSTGRESQL[1]");
					
				}
				else if(MainLayout.this.input.equals("lazybooks"))
				{
					try(final PsqlLazyBooks clientlazyPG = new PsqlLazyBooks())
					{
						final Book[] books2 = clientlazyPG.getAllBooks();
					}
					catch(final Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call LAZY BOOKS POSTGRESQL[1]");
				}
				else
				{
					System.out.println("wrong input, terminate app");
					System.exit(0);
				}
				
			}
		};
		
		final TimerTask taskMS2 = new TimerTask()
		{
			@Override
			public void run()
			{
				if(MainLayout.this.input.equals("books"))
				{
					try(MSBooks clientMS = new MSBooks())
					{
						
						final Book[] books1 = clientMS.getAllBooks();
					}
					catch(final Exception e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call BOOKS MICROSTREAM[2]");
				}
				else if(MainLayout.this.input.equals("avg"))
				{
					try(MSAvgPrice avgclient = new MSAvgPrice())
					{
						final Double avg = avgclient.getAvgBookPrice();
					}
					catch(final Exception e2)
					{
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call AVG MICROSTREAM [1]");
				}
				else if(MainLayout.this.input.equals("join"))
				{
					try(MSShops shopClient = new MSShops())
					{
						final BookInShop[] result = shopClient.getBookInShop();
					}
					catch(final Exception e2)
					{
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call JOIN MICROSTREAM [1]");
				}
				else if(MainLayout.this.input.equals("lazybooks"))
				{
					try(MSLazyBooks clientlazyMS = new MSLazyBooks())
					{
						final Book[] books1 = clientlazyMS.getAllBooks();
					}
					catch(final Exception e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call LAZY BOOKS MICROSTREAM[2]");
				}
				else
				{
					System.out.println("wrong input, terminate app");
					System.exit(0);
				}
				
			}
		};

		final TimerTask taskPG2 = new TimerTask()
		{
			@Override
			public void run()
			{
				if(MainLayout.this.input.equals("books"))
				{
					try(final PsqlBooks clientPG = new PsqlBooks())
					{
						final Book[] books2 = clientPG.getAllBooks();
					}
					catch(final Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call BOOKS POSTGRESQL[2]");
				}
				else if(MainLayout.this.input.equals("avg"))
				{
					try(PsqlAvgPrice avgclient = new PsqlAvgPrice())
					{
						final Double avg = avgclient.getAvgBookPrice();
					}
					catch(final Exception e2)
					{
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call AVG POSTGRESQL[1]");
				}
				else if(MainLayout.this.input.equals("join"))
				{
					try(PsqlShops shopClient = new PsqlShops())
					{
						final BookInShop[] result = shopClient.getBookInShop();
					}
					catch(final Exception e2)
					{
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call JOIN POSTGRESQL[1]");
					
				}
				else if(MainLayout.this.input.equals("lazybooks"))
				{
					try(final PsqlLazyBooks clientlazyPG = new PsqlLazyBooks())
					{
						final Book[] books2 = clientlazyPG.getAllBooks();
					}
					catch(final Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// System.out.println(
					// LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"))
					// + " --> Call LAZY BOOKS POSTGRESQL[2]");
				}
				else
				{
					System.out.println("wrong input, terminate app");
					System.exit(0);
				}
				
			}
		};

		if(MainLayout.this.input.equals("books"))
		{
			this.msDelay = 100L;
			this.psDelay = 100L;
		}
		else if(MainLayout.this.input.equals("avg"))
		{
			this.msDelay = 100L;
			this.psDelay = 100L;
		}
		else if(MainLayout.this.input.equals("join"))
		{
			this.msDelay = 250L;
			this.psDelay = 250L;
		}
		else if(MainLayout.this.input.equals("lazybooks"))
		{
			this.msDelay = 150L;
			this.psDelay = 250L;
		}

		final Timer runner1 = new Timer("task");
		runner1.scheduleAtFixedRate(taskMS1, 0, this.msDelay);

		final Timer runner2 = new Timer("task2");
		runner2.scheduleAtFixedRate(taskMS2, 0, this.msDelay);
		
		final Timer runner3 = new Timer("task3");
		runner3.scheduleAtFixedRate(taskPG1, 0, this.psDelay);

		final Timer runner4 = new Timer("task3");
		runner4.scheduleAtFixedRate(taskPG2, 0, this.psDelay);
		
		MainLayout.tasks.add(runner1);
		MainLayout.tasks.add(runner2);
		MainLayout.tasks.add(runner3);
		MainLayout.tasks.add(runner4);
		
		System.out.println("-> Start Task");
		this.newAction(new Action(LocalDateTime.now(), "Start Task - " + this.input));
		
	}

	/**
	 * Event handler delegate method for the {@link Button} {@link #btnStop}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnStop_onClick(final ClickEvent<Button> event)
	{
		MainLayout.tasks.forEach(t -> {
			t.cancel();
			System.out.println("-> Cancel Task");
		});

		MainLayout.tasks.clear();
		this.newAction(new Action(LocalDateTime.now(), "Cancel Task - " + this.input));
		this.btnTask.setEnabled(true);
		this.btnStop.setEnabled(false);
	}

	private void newAction(final Action a)
	{
		MainLayout.actions.add(a);
		this.grid.setItems(MainLayout.actions);
	}
	
	/**
	 * Event handler delegate method for the {@link RadioButtonGroup} {@link #radioButtonGroup}.
	 *
	 * @see HasValue.ValueChangeListener#valueChanged(HasValue.ValueChangeEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void radioButtonGroup_valueChanged(final ComponentValueChangeEvent<RadioButtonGroup<String>, String> event)
	{
		if(event.getValue().equals("Books"))
		{
			this.input = "books";
			this.label.setText("Book Rest call - 1500 Books with title, value, autor and ISBN. Standart Rest call.");
		}
		else if(event.getValue().equals("Lazy Books"))
		{
			this.input = "lazybooks";
			this.label.setText(
				"LazyBook Rest call - 1500 Books with title, value, autor and ISBN. Microstream is using 'lazy' mode - direct read from system storage, PostgreSQL has deactived Cache function.");
		}
		else if(event.getValue().equals("avg"))
		{
			this.input = "avg";
			this.label.setText(
				"AVG Rest call - Aggregate function for average cost of 1500 books. AVG function in PostgreSQL, Microstream is using java stream");
		}
		else if(event.getValue().equals("join"))
		{
			this.input = "join";
			this.label.setText(
				"Join Rest call - 1500 Books with title, value, autor and ISBN sorted to 50 Stores with Addresses, Shopnames and more. Inner Join on PostgreSQL, Microstream is using multi javastream to find fitting references.");
		}
	}

	/* WARNING: Do NOT edit!<br>The content of this method is always regenerated by the UI designer. */
	// <generated-code name="initUI">
	private void initUI()
	{
		this.div              = new Div();
		this.horizontalLayout = new HorizontalLayout();
		this.verticalLayout   = new VerticalLayout();
		this.btnTask          = new Button();
		this.btnStop          = new Button();
		this.radioButtonGroup = new RadioButtonGroup<>();
		this.label            = new Label();
		this.grid             = new Grid<>(Action.class, false);
		
		this.verticalLayout.setSpacing(false);
		this.verticalLayout.setPadding(false);
		this.btnTask.setText("Tasks");
		this.btnTask.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		this.btnTask.setIcon(AvIcons.PLAY_ARROW.create());
		this.btnStop.setEnabled(false);
		this.btnStop.setText("Stop");
		this.btnStop.addThemeVariants(ButtonVariant.LUMO_ERROR);
		this.btnStop.setIcon(AvIcons.STOP.create());
		this.radioButtonGroup
			.setRenderer(new TextRenderer<>(ItemLabelGeneratorFactory.NonNull(CaptionUtils::resolveCaption)));
		this.radioButtonGroup.addThemeName("vertical");
		this.label.setText("No Tasks running");
		this.grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.grid
			.addColumn(new LocalDateTimeRenderer<>(Action::getDate,
				DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM), ""))
			.setKey("date").setHeader("Time").setSortable(true).setAutoWidth(true).setFlexGrow(0);
		this.grid.addColumn(Action::getDescription).setKey("description").setHeader("Action").setSortable(true)
			.setAutoWidth(true);
		this.grid.setSelectionMode(Grid.SelectionMode.SINGLE);
		
		this.btnTask.setWidthFull();
		this.btnTask.setHeight(null);
		this.btnStop.setWidthFull();
		this.btnStop.setHeight(null);
		this.label.setSizeUndefined();
		this.verticalLayout.add(this.btnTask, this.btnStop, this.radioButtonGroup, this.label);
		this.verticalLayout.setWidth("200px");
		this.verticalLayout.setHeightFull();
		this.grid.setSizeFull();
		this.horizontalLayout.add(this.verticalLayout, this.grid);
		this.horizontalLayout.setFlexGrow(1.0, this.grid);
		this.horizontalLayout.setSizeFull();
		this.div.add(this.horizontalLayout);
		this.div.setWidth("80%");
		this.div.setHeight("80%");
		this.add(this.div);
		this.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, this.div);
		this.setSizeFull();
		
		this.btnTask.addClickListener(this::btnTask_onClick);
		this.btnStop.addClickListener(this::btnStop_onClick);
		this.radioButtonGroup.addValueChangeListener(this::radioButtonGroup_valueChanged);
	} // </generated-code>

	// <generated-code name="variables">
	private Button                   btnTask, btnStop;
	private Grid<Action>             grid;
	private HorizontalLayout         horizontalLayout;
	private VerticalLayout           verticalLayout;
	private Div                      div;
	private Label                    label;
	private RadioButtonGroup<String> radioButtonGroup;
	// </generated-code>
	
}
