/**
 * パッケージ名：geotheme.geofuse_admin.sub_views
 * ファイル名  ：MarkerUploadView.java
 * 
 * @author mbasa
 * @since May 19, 2017
 */
package geotheme.geofuse_admin.sub_views;

import geotheme.geofuse_admin.db.ConnectionPoolHolder;
import geotheme.geofuse_admin.db.GeofuseCsvCtl;
import geotheme.geofuse_admin.windows.ConfirmWin;

import java.util.ResourceBundle;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.themes.ValoTheme;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 説明：
 *
 */
public class MarkerUploadView extends VerticalLayout implements View {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public  static final String NAME = "MarkerUploadView";
    
    private Logger LOGGER = LogManager.getLogger();

    private Table markerTable = new Table();
    private ResourceBundle rb = ResourceBundle.getBundle( 
            "properties/lang/MarkerUploadView",
            UI.getCurrent().getSession().getLocale()
          );
    

    /**
     * コンストラクタ
     *
     * @param children
     */
    public MarkerUploadView(Component... children) {
        super(children);
    }

    /**
     * コンストラクタ
     *
     */
    public MarkerUploadView() {
        ThemeResource tr = new ThemeResource("graphics/Favicon16x16.png");
        Image logo  = new Image(null,tr);

        Label header = new Label( rb.getString("TITLE") );
        header.addStyleName(ValoTheme.LABEL_H1);
        header.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.setSizeUndefined();

        HorizontalLayout tlayout = new HorizontalLayout();
        tlayout.addComponents(logo,header);
        tlayout.setComponentAlignment(logo  , Alignment.TOP_LEFT);
        tlayout.setComponentAlignment(header, Alignment.MIDDLE_CENTER);

        Responsive.makeResponsive(this);

        this.setSpacing(true);
        this.setMargin(new MarginInfo(true,true,false,true));
        this.addComponents(tlayout,this.setMarkerAdminTable(),
                this.setCsvUpload() );
    }

    private VerticalLayout setMarkerAdminTable() {
        VerticalLayout layout = new VerticalLayout();
        
        Label title = new Label(rb.getString("MARKER_TABLE"));
        title.addStyleName(ValoTheme.LABEL_H2);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        
        TableQuery tq = new TableQuery(null,"geofuse","markerlayer",
                ConnectionPoolHolder.getConnectionPool(), 
                new DefaultSQLGenerator() ); 

        //markerTable.setEditable(true);
        markerTable.setSelectable(true);
        markerTable.setNullSelectionAllowed(false);
        markerTable.setWidth("100%");
        markerTable.setHeight("150px");
        markerTable.setRowHeaderMode(RowHeaderMode.INDEX);
        markerTable.addStyleName(ValoTheme.TABLE_SMALL);
        
        try {
            SQLContainer sqlContainer = new SQLContainer(tq);
            sqlContainer.sort(new Object[] {"id"}, new boolean[] {false} );

            markerTable.setContainerDataSource(sqlContainer);
        }
        catch(Exception e) {
            LOGGER.error(e);
        }
                
        final Button delBtn = new Button(rb.getString("LAYER_DEL_BTN"));
        delBtn.setEnabled(false);
        
        markerTable.addItemClickListener(new ItemClickListener() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void itemClick(ItemClickEvent event) {
                delBtn.setEnabled(true);
            }
        });
        markerTable.setVisibleColumns("layername");
        markerTable.setColumnHeader("layername", 
                rb.getString("LAYER_COL_NAME") );
        
        delBtn.addClickListener(new ClickListener() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                if( markerTable.getValue() == null ) {
                    return;
                }
                
                final Item item = markerTable.getItem(markerTable.getValue());      
                LOGGER.debug(item.getItemProperty("layername").getValue());
                final ConfirmWin cw = new ConfirmWin();

                String msg = rb.getString("DELETE_LAYER_MSG")+": "+ 
                        item.getItemProperty("layername").getValue();
                
                cw.setModal(true);
                cw.setClosable(true);
                cw.setResizable(false);
                cw.setWidth("400px");
                cw.setHeight("230px");               
                cw.init( msg,false);  
                cw.ok.addClickListener(new ClickListener() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        
                        int layerId = 
                            (int)item.getItemProperty("id").getValue();
                        
                        String tableName = 
                            (String)item.getItemProperty("tablename").getValue();
                        
                        GeofuseCsvCtl.deleteMarkerLayer(layerId, tableName);
                        
                        ((SQLContainer)
                                markerTable.getContainerDataSource()).refresh();

                        cw.closeWindow();
                    }
                });
                getUI().addWindow(cw);
            }            
        });
        layout.addComponents(title,markerTable,delBtn);
        layout.setComponentAlignment(delBtn, Alignment.MIDDLE_LEFT);
        layout.setSpacing(true);
        layout.setMargin(new MarginInfo(false,true));
        
        return layout;
                
    }

    private VerticalLayout setCsvUpload() {
        VerticalLayout layout = new VerticalLayout();
        
        Label title = new Label( rb.getString("MARKER_UPLOAD"));
        title.addStyleName(ValoTheme.LABEL_H2);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        
        final TextField titleIn = new TextField(
                rb.getString("LAYERNAME_TXTBOX"));
        
        final TextArea csvIn    = new TextArea(rb.getString("CSV_TXTBOX"));
        
        titleIn.setWidth("40%");
        titleIn.setInputPrompt("ex: Municipal Hospitals");
        csvIn.setWidth("100%");
        csvIn.setInputPrompt("ex: Hospital,NumBeds,Lon,Lat\nHeisei Hospital,300,135.5,35,5");        
        
        titleIn.addValidator(new StringLengthValidator(
                rb.getString("ERR_SHORT_INPUT"),1,null,false) );
        titleIn.setValidationVisible(false);
        
        csvIn.addValidator(new StringLengthValidator(
                rb.getString("ERR_SHORT_INPUT"),10,null,false) );
        csvIn.setValidationVisible(false);
        
        Button uploadBtn  = new Button(rb.getString("UPLOAD_BTN"));
        
        layout.addComponents( title,titleIn,csvIn,uploadBtn );
        layout.setComponentAlignment(uploadBtn, Alignment.MIDDLE_LEFT);
        
        layout.setSpacing(true);
        layout.setMargin(new MarginInfo(false,true,true,true));
        
        uploadBtn.addClickListener(new ClickListener() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    titleIn.setValidationVisible(true);
                    csvIn.setValidationVisible(true);
                    
                    titleIn.validate();
                    csvIn.validate();
                }
                catch(Exception e) {
                    LOGGER.debug(e);
                    return;
                }

                titleIn.setValidationVisible(false);
                csvIn.setValidationVisible(false);
                                
                String tabName = GeofuseCsvCtl.processMarkerCsv(
                        csvIn.getValue());
                LOGGER.debug("ProcessCsv: {}",tabName);
                
                if( !tabName.startsWith("Error") ) {
                    GeofuseCsvCtl.update(
                            "insert into geofuse.markerlayer values "
                            + "(default,?,?)", titleIn.getValue(),tabName);
                    ((SQLContainer)
                            markerTable.getContainerDataSource()).refresh();
                    
                    titleIn.setValue("");
                    csvIn.setValue("");                    
                }
                else {
                    ConfirmWin cw = new ConfirmWin();

                    String msg = new String();
                    
                    if( tabName.equalsIgnoreCase("Error: Parsing CSV")) {
                        msg = rb.getString("ERR_CSV_PARSE");
                    }
                    else if( tabName.equalsIgnoreCase("Error: No Lon/Lat columns")) {
                        msg = rb.getString("ERR_NO_LATLON");
                    }
                    
                    cw.setModal(true);
                    cw.setClosable(true);
                    cw.setResizable(false);
                    cw.setWidth("400px");
                    cw.setHeight("230px");               
                    cw.init( msg,false,false );
                    
                    getUI().addWindow(cw);
                }                
            }
        });
        return layout;
    }

    /* (非 Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
        // TODO 自動生成されたメソッド・スタブ

    }

}
