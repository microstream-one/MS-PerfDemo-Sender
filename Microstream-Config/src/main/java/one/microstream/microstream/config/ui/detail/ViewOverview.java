package one.microstream.microstream.config.ui.detail;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.rapidclipse.framework.server.resources.CaptionUtils;
import com.rapidclipse.framework.server.ui.UIUtils;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import one.microstream.microstream.config.core.BookstoreHttpClient;
import one.microstream.microstream.config.dto.DTOBook;
import one.microstream.microstream.config.ui.MainLayout;
import one.microstream.microstream.config.ui.model.Book;

public class ViewOverview extends HorizontalLayout
{
	private final BookstoreHttpClient pgHttpClient = new BookstoreHttpClient(
		Optional.ofNullable(System.getenv("POSTGRES_URL")).orElse("http://localhost:8081")
	);
	private final BookstoreHttpClient esHttpClient = new BookstoreHttpClient(
		Optional.ofNullable(System.getenv("ECLIPSESTORE_URL")).orElse("http://localhost:8082")
	);

	/**
	 * 
	 */
	public ViewOverview()
	{
		super();
		this.initUI();
		
		btnLoadESData.setEnabled(!txtTitleFilter.isEmpty());
		btnLoadPostData.setEnabled(!txtTitleFilterPost.isEmpty());
		
		long countESBooks = esHttpClient.countBooks();
		long countPGBooks = pgHttpClient.countBooks();
		
		txtESBookCount.setValue(String.valueOf(countESBooks));
		txtESBookInsertCount.setValue(String.valueOf(esHttpClient.countBooksInsertPM()));
		txtPostBookCount.setValue(String.valueOf(countPGBooks));
		txtPostBookInsertCount.setValue(String.valueOf(pgHttpClient.countBooksInsertPM()));
	}

	
	/**
	 * Event handler delegate method for the {@link Button}
	 * {@link #btnRefreshESCount}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnRefreshESCount_onClick(final ClickEvent<Button> event)
	{
		txtESBookCount.setValue(String.valueOf(esHttpClient.countBooks()));
		txtESBookInsertCount.setValue(String.valueOf(esHttpClient.countBooksInsertPM()));
	}

	/**
	 * Event handler delegate method for the {@link Button}
	 * {@link #btnRefreshPostCount}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnRefreshPostCount_onClick(final ClickEvent<Button> event)
	{
		txtPostBookCount.setValue(String.valueOf(pgHttpClient.countBooks()));
		txtPostBookInsertCount.setValue(String.valueOf(pgHttpClient.countBooksInsertPM()));
	}

	/**
	 * Event handler delegate method for the {@link Button} {@link #btnLoadESData}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnLoadESData_onClick(final ClickEvent<Button> event)
	{
		List<DTOBook> searchByTitle = esHttpClient.searchByTitle(txtTitleFilter.getValue());
		List<Book> collect = searchByTitle.stream().map(b -> new Book(b)).collect(Collectors.toList());
		gridESData.setItems(collect);
	}

	/**
	 * Event handler delegate method for the {@link Button} {@link #btnLoadPostData}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void btnLoadPostData_onClick(final ClickEvent<Button> event)
	{
		List<DTOBook> searchByTitle = pgHttpClient.searchByTitle(txtTitleFilterPost.getValue());
		List<Book> collect = searchByTitle.stream().map(b -> new Book(b)).collect(Collectors.toList());
		gridPostData.setItems(collect);
	}

	/**
	 * Event handler delegate method for the {@link TextField} {@link #txtTitleFilter}.
	 *
	 * @see HasValue.ValueChangeListener#valueChanged(HasValue.ValueChangeEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void txtTitleFilter_valueChanged(final ComponentValueChangeEvent<TextField, String> event)
	{
		btnLoadESData.setEnabled(!txtTitleFilter.isEmpty());
	}

	/**
	 * Event handler delegate method for the {@link TextField} {@link #txtTitleFilterPost}.
	 *
	 * @see HasValue.ValueChangeListener#valueChanged(HasValue.ValueChangeEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void txtTitleFilterPost_valueChanged(final ComponentValueChangeEvent<TextField, String> event)
	{
		btnLoadPostData.setEnabled(!txtTitleFilterPost.isEmpty());
	}

	/*
	 * WARNING: Do NOT edit!<br>The content of this method is always regenerated by
	 * the UI designer.
	 */
	// <generated-code name="initUI">
	private void initUI()
	{
		this.splitLayout = new SplitLayout();
		this.horizontalLayout = new VerticalLayout();
		this.h2 = new H2();
		this.btnRefreshESCount = new Button();
		this.txtESBookCount = new TextField();
		this.txtESBookInsertCount = new TextField();
		this.horizontalLayout3 = new HorizontalLayout();
		this.txtTitleFilter = new TextField();
		this.btnLoadESData = new Button();
		this.gridESData = new Grid<>(Book.class, false);
		this.horizontalLayout5 = new VerticalLayout();
		this.h222 = new H2();
		this.btnRefreshPostCount = new Button();
		this.txtPostBookCount = new TextField();
		this.txtPostBookInsertCount = new TextField();
		this.horizontalLayout4 = new HorizontalLayout();
		this.txtTitleFilterPost = new TextField();
		this.btnLoadPostData = new Button();
		this.gridPostData = new Grid<>(Book.class, false);
	
		this.horizontalLayout.setSpacing(false);
		this.horizontalLayout.setPadding(false);
		this.horizontalLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
		this.horizontalLayout.getStyle().set("margin-right", "10px");
		this.h2.setText("EclipseStore DB");
		this.btnRefreshESCount.setText("Refresh count values");
		this.btnRefreshESCount.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		this.btnRefreshESCount.setIcon(VaadinIcon.REFRESH.create());
		this.txtESBookCount.setReadOnly(true);
		this.txtESBookCount.setLabel("Books Count");
		this.txtESBookInsertCount.setReadOnly(true);
		this.txtESBookInsertCount.setLabel("Books Insert Count");
		this.horizontalLayout3.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
		this.horizontalLayout3.getStyle().set("margin-bottom", "10px");
		this.txtTitleFilter.setValueChangeMode(ValueChangeMode.LAZY);
		this.txtTitleFilter.setLabel("Find by Title");
		this.btnLoadESData.setText("Search");
		this.btnLoadESData.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		this.btnLoadESData.setIcon(VaadinIcon.SEARCH.create());
		this.gridESData.setPageSize(60);
		this.gridESData.setAllRowsVisible(true);
		this.gridESData.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridESData.addColumn(Book::getIsbn)
			.setKey("isbn")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "isbn"))
			.setSortable(true);
		this.gridESData.addColumn(Book::getTitle)
			.setKey("title")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "title"))
			.setSortable(true);
		this.gridESData.addColumn(Book::getAuthorID)
			.setKey("authorID")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "authorID"))
			.setSortable(true);
		this.gridESData.addColumn(Book::getEdition)
			.setKey("edition")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "edition"))
			.setSortable(true);
		this.gridESData.addColumn(Book::getPublicationDate)
			.setKey("publicationDate")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "publicationDate"))
			.setSortable(true);
		this.gridESData.addColumn(Book::getAvailableQuantity)
			.setKey("availableQuantity")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "availableQuantity"))
			.setSortable(true);
		this.gridESData.addColumn(Book::getPrice)
			.setKey("price")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "price"))
			.setSortable(true);
		this.gridESData.addColumn(Book::getPublisherID)
			.setKey("publisherID")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "publisherID"))
			.setSortable(true);
		this.gridESData.setSelectionMode(Grid.SelectionMode.SINGLE);
		this.horizontalLayout5.setSpacing(false);
		this.horizontalLayout5.setPadding(false);
		this.horizontalLayout5.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
		this.horizontalLayout5.getStyle().set("margin-left", "10px");
		this.h222.setText("PostgreSQL DB");
		this.btnRefreshPostCount.setText("Refresh count values");
		this.btnRefreshPostCount.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		this.btnRefreshPostCount.setIcon(VaadinIcon.REFRESH.create());
		this.txtPostBookCount.setReadOnly(true);
		this.txtPostBookCount.setLabel("Books Count");
		this.txtPostBookInsertCount.setReadOnly(true);
		this.txtPostBookInsertCount.setLabel("Books Insert Count");
		this.horizontalLayout4.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
		this.horizontalLayout4.getStyle().set("margin-bottom", "10px");
		this.txtTitleFilterPost.setValueChangeMode(ValueChangeMode.LAZY);
		this.txtTitleFilterPost.setLabel("Find by Title");
		this.btnLoadPostData.setText("Search");
		this.btnLoadPostData.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		this.btnLoadPostData.setIcon(VaadinIcon.SEARCH.create());
		this.gridPostData.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridPostData.addColumn(Book::getIsbn)
			.setKey("isbn")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "isbn"))
			.setSortable(true);
		this.gridPostData.addColumn(Book::getTitle)
			.setKey("title")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "title"))
			.setSortable(true);
		this.gridPostData.addColumn(Book::getAuthorID)
			.setKey("authorID")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "authorID"))
			.setSortable(true);
		this.gridPostData.addColumn(Book::getEdition)
			.setKey("edition")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "edition"))
			.setSortable(true);
		this.gridPostData.addColumn(Book::getPublicationDate)
			.setKey("publicationDate")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "publicationDate"))
			.setSortable(true);
		this.gridPostData.addColumn(Book::getAvailableQuantity)
			.setKey("availableQuantity")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "availableQuantity"))
			.setSortable(true);
		this.gridPostData.addColumn(Book::getPrice)
			.setKey("price")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "price"))
			.setSortable(true);
		this.gridPostData.addColumn(Book::getPublisherID)
			.setKey("publisherID")
			.setHeader(CaptionUtils.resolveCaption(Book.class, "publisherID"))
			.setSortable(true);
		this.gridPostData.setSelectionMode(Grid.SelectionMode.SINGLE);
	
		this.txtTitleFilter.setWidthFull();
		this.txtTitleFilter.setHeight(null);
		this.btnLoadESData.setSizeUndefined();
		this.horizontalLayout3.add(this.txtTitleFilter, this.btnLoadESData);
		this.h2.setWidthFull();
		this.h2.setHeight(null);
		this.btnRefreshESCount.setSizeUndefined();
		this.txtESBookCount.setWidthFull();
		this.txtESBookCount.setHeight(null);
		this.txtESBookInsertCount.setWidthFull();
		this.txtESBookInsertCount.setHeight(null);
		this.horizontalLayout3.setSizeUndefined();
		this.gridESData.setWidthFull();
		this.gridESData.setHeight("659px");
		this.horizontalLayout.add(
			this.h2,
			this.btnRefreshESCount,
			this.txtESBookCount,
			this.txtESBookInsertCount,
			this.horizontalLayout3,
			this.gridESData
		);
		this.horizontalLayout.setFlexGrow(1.0, this.gridESData);
		this.txtTitleFilterPost.setWidthFull();
		this.txtTitleFilterPost.setHeight(null);
		this.btnLoadPostData.setSizeUndefined();
		this.horizontalLayout4.add(this.txtTitleFilterPost, this.btnLoadPostData);
		this.h222.setWidthFull();
		this.h222.setHeight(null);
		this.btnRefreshPostCount.setSizeUndefined();
		this.txtPostBookCount.setWidthFull();
		this.txtPostBookCount.setHeight(null);
		this.txtPostBookInsertCount.setWidthFull();
		this.txtPostBookInsertCount.setHeight(null);
		this.horizontalLayout4.setSizeUndefined();
		this.gridPostData.setSizeFull();
		this.horizontalLayout5.add(
			this.h222,
			this.btnRefreshPostCount,
			this.txtPostBookCount,
			this.txtPostBookInsertCount,
			this.horizontalLayout4,
			this.gridPostData
		);
		this.horizontalLayout5.setFlexGrow(1.0, this.gridPostData);
		this.splitLayout.addToPrimary(this.horizontalLayout);
		this.splitLayout.addToSecondary(this.horizontalLayout5);
		this.splitLayout.setSplitterPosition(50.0);
		this.splitLayout.setSizeFull();
		this.add(this.splitLayout);
		this.setFlexGrow(1.0, this.splitLayout);
		this.setSizeFull();
	
		this.btnRefreshESCount.addClickListener(this::btnRefreshESCount_onClick);
		this.txtTitleFilter.addValueChangeListener(this::txtTitleFilter_valueChanged);
		this.btnLoadESData.addClickListener(this::btnLoadESData_onClick);
		this.btnRefreshPostCount.addClickListener(this::btnRefreshPostCount_onClick);
		this.txtTitleFilterPost.addValueChangeListener(this::txtTitleFilterPost_valueChanged);
		this.btnLoadPostData.addClickListener(this::btnLoadPostData_onClick);
	} // </generated-code>

	// <generated-code name="variables">
	private Button btnRefreshESCount, btnLoadESData, btnRefreshPostCount, btnLoadPostData;
	private SplitLayout splitLayout;
	private VerticalLayout horizontalLayout, horizontalLayout5;
	private HorizontalLayout horizontalLayout3, horizontalLayout4;
	private Grid<Book> gridESData, gridPostData;
	private H2 h2, h222;
	private TextField txtESBookCount, txtESBookInsertCount, txtTitleFilter, txtPostBookCount, txtPostBookInsertCount,
		txtTitleFilterPost;
	// </generated-code>

}
