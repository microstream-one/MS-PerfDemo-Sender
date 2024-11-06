
package one.microstream.microstream.config.ui;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

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
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
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

import one.microstream.microstream.config.core.SenderScheduler;
import one.microstream.microstream.config.core.threads.TaskBookCreateMS;
import one.microstream.microstream.config.core.threads.TaskBookQuery;
import one.microstream.microstream.config.domain.Action;


@RedirectView
@Route("main")
public class MainLayout extends VerticalLayout
{
	private SenderScheduler sender;

	public MainLayout()
	{
		super();
		this.initUI();

		this.radioButtonGroup.setItems("Books by Title", "Create Data EclipseStore", "Create Data PostgreSQL",
			"Clear EclipseStore", "Clear PostgreSQL");
		this.radioButtonGroup.setValue("Books by Title");
		
		// if(MainLayout.tasks.size() != 0)
		// {
		// this.btnStop.setEnabled(true);
		// this.btnTask.setEnabled(false);
		// }

		// try(final PsqlBooks clientPG = new PsqlBooks())
		// {
		// final Book[] books2 = clientPG.getAllBooks();
		// }
		// catch(final Exception e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	// @Override
	// public void configurePage(final InitialPageSettings settings)
	// {
	// settings.addLink("shortcut icon", "frontend/images/favicon.ico");
	// settings.addFavIcon("icon", "frontend/images/favicon256.png", "256x256");
	// }

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

		if(this.radioButtonGroup.getValue().equals("Books by Title"))
		{
			this.sender = new SenderScheduler(
				this.rangeAmountThreads.getValue().intValue(),
				this.rangeSendDelay.getValue().intValue(),
				this.rangeRampUp.getValue().intValue());
			this.sender.setTask(new TaskBookQuery());
			this.sender.start();
		}
		else if(this.radioButtonGroup.getValue().equals("Create Data EclipseStore"))
		{
			this.sender = new SenderScheduler(
				this.rangeAmountThreads.getValue().intValue(),
				this.rangeSendDelay.getValue().intValue(),
				this.rangeRampUp.getValue().intValue());
			this.sender.setTask(new TaskBookCreateMS());
			this.sender.start();
		}
		else if(this.radioButtonGroup.getValue().equals("Create Data PostgreSQL"))
		{
			this.sender = new SenderScheduler(
				this.rangeAmountThreads.getValue().intValue(),
				this.rangeSendDelay.getValue().intValue(),
				this.rangeRampUp.getValue().intValue());
			// this.sender.setTask(new TaskBookCreatePostgreSQL());
			this.sender.start();
		}

	}

	/**
	 * Event handler delegate method for the {@link Button} {@link #btnStop}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnStop_onClick(final ClickEvent<Button> event)
	{
		this.sender.stop();
		
		this.btnTask.setEnabled(true);
		this.btnStop.setEnabled(false);
	}

	/**
	 * Event handler delegate method for the {@link RadioButtonGroup} {@link #radioButtonGroup}.
	 *
	 * @see HasValue.ValueChangeListener#valueChanged(HasValue.ValueChangeEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void radioButtonGroup_valueChanged(final ComponentValueChangeEvent<RadioButtonGroup<String>, String> event)
	{
		if(this.radioButtonGroup.getValue().equals("Books by Title"))
		{
			
		}
		else if(this.radioButtonGroup.getValue().equals("Create Data EclipseStore"))
		{
			
		}
		else if(this.radioButtonGroup.getValue().equals("Create Data PostgreSQL"))
		{
			
		}
	}

	/* WARNING: Do NOT edit!<br>The content of this method is always regenerated by the UI designer. */
	// <generated-code name="initUI">
	private void initUI()
	{
		this.div                = new Div();
		this.horizontalLayout   = new HorizontalLayout();
		this.verticalLayout     = new VerticalLayout();
		this.btnTask            = new Button();
		this.btnStop            = new Button();
		this.hr                 = new Hr();
		this.radioButtonGroup   = new RadioButtonGroup<>();
		this.hr2                = new Hr();
		this.nativeLabel4       = new NativeLabel();
		this.rangeAmountThreads = new NumberField();
		this.nativeLabel2       = new NativeLabel();
		this.rangeRampUp        = new NumberField();
		this.nativeLabel3       = new NativeLabel();
		this.rangeSendDelay     = new NumberField();
		this.hr3                = new Hr();
		this.label              = new Label();
		this.grid               = new Grid<>(Action.class, false);
		
		this.verticalLayout.setSpacing(false);
		this.verticalLayout.setPadding(false);
		this.btnTask.setText("Tasks");
		this.btnTask.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		this.btnTask.setIcon(VaadinIcon.PLAY.create());
		this.btnStop.setEnabled(false);
		this.btnStop.setText("Stop");
		this.btnStop.addThemeVariants(ButtonVariant.LUMO_ERROR);
		this.btnStop.setIcon(VaadinIcon.STOP.create());
		this.radioButtonGroup
			.setRenderer(new TextRenderer<>(ItemLabelGeneratorFactory.NonNull(CaptionUtils::resolveCaption)));
		this.radioButtonGroup.addThemeName("vertical");
		this.nativeLabel4.setText("Amount Threads");
		this.rangeAmountThreads.setValue(1.0);
		this.nativeLabel2.setText("Ramp up delay (ms)");
		this.rangeRampUp.setValue(10000.0);
		this.nativeLabel3.setText("Send delay (ms)");
		this.rangeSendDelay.setValue(0.0);
		this.label.setText("No Tasks running");
		this.grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.grid
			.addColumn(new LocalDateTimeRenderer<>(Action::getDate,
				() -> DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT), ""))
			.setKey("date").setHeader("Time").setSortable(true).setAutoWidth(true).setFlexGrow(0);
		this.grid.addColumn(Action::getDescription).setKey("description").setHeader("Action").setSortable(true)
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
		this.rangeRampUp.setWidthFull();
		this.rangeRampUp.setHeight(null);
		this.nativeLabel3.setSizeUndefined();
		this.rangeSendDelay.setWidthFull();
		this.rangeSendDelay.setHeight(null);
		this.hr3.setSizeUndefined();
		this.label.setSizeUndefined();
		this.verticalLayout.add(this.btnTask, this.btnStop, this.hr, this.radioButtonGroup, this.hr2, this.nativeLabel4,
			this.rangeAmountThreads, this.nativeLabel2, this.rangeRampUp, this.nativeLabel3, this.rangeSendDelay,
			this.hr3,
			this.label);
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
		this.radioButtonGroup.addValueChangeListener(this::radioButtonGroup_valueChanged);
	} // </generated-code>

	// <generated-code name="variables">
	private NativeLabel              nativeLabel4, nativeLabel2, nativeLabel3;
	private Button                   btnTask, btnStop;
	private Grid<Action>             grid;
	private NumberField              rangeAmountThreads, rangeRampUp, rangeSendDelay;
	private HorizontalLayout         horizontalLayout;
	private VerticalLayout           verticalLayout;
	private Div                      div;
	private Label                    label;
	private Hr                       hr, hr2, hr3;
	private RadioButtonGroup<String> radioButtonGroup;
	// </generated-code>

}
