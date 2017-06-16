/**
 * パッケージ名：geotheme.geofuse_admin.sub_views
 * ファイル名  ：BaseLayerView.java
 * 
 * @author mbasa
 * @since May 22, 2017
 */
package geotheme.geofuse_admin.sub_views;


import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import geotheme.geofuse_admin.db.ConnectionPoolHolder;
import geotheme.geofuse_admin.db.DBTools;
import geotheme.geofuse_admin.windows.ConfirmWin;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 説明：
 *
 */
public class MetaDataView extends VerticalLayout implements View {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final String NAME = "MetaDataView";
    
    private Logger LOGGER     = LogManager.getLogger();
    private ResourceBundle rb = ResourceBundle.getBundle( 
            "properties/lang/MetaDataView",
            UI.getCurrent().getSession().getLocale()
          );
    /**
     * コンストラクタ
     *
     * @param children
     */
    public MetaDataView(Component... children) {
        super(children);
    }

    /**
     * コンストラクタ
     *
     */
    public MetaDataView() {
        
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
                
        Label tab_title = new Label( rb.getString("TABLE") );
        tab_title.addStyleName(ValoTheme.LABEL_H2);
        tab_title.addStyleName(ValoTheme.LABEL_COLORED);
        tab_title.setSizeUndefined();
        
        VerticalLayout descLayout = new VerticalLayout();
        descLayout.addComponents( tab_title );
        
        Responsive.makeResponsive(this);

        this.setSpacing(true);
        this.setMargin( new MarginInfo(true,true,false,true));
        this.setSizeFull();
        this.addComponents(tlayout,descLayout);
        this.setupTable();
        
    }

    private final String geofuseURL = "/geofuse/ui/showtheme?layer=";
    
    private final Button delBtn = new Button( rb.getString("BTN.DEL") );
    private final Button vueBtn = new Button( rb.getString("BTN.VIEW") );
    
    private void setupTable() {
        
        final Table baseTable = new Table();
        final TableQuery tq   = new TableQuery(null,"geofuse","metadata",
                ConnectionPoolHolder.getConnectionPool(), 
                new DefaultSQLGenerator() );

        try{
            SQLContainer sqlContainer = new SQLContainer( tq );
            sqlContainer.sort(new Object[] {"ddate"}, new boolean[] {false} );

            baseTable.setContainerDataSource(sqlContainer);
        }
        catch( Exception e ) {
            LOGGER.error( e );
        }
        final BrowserWindowOpener bOpener = new BrowserWindowOpener("");
        bOpener.setWindowName("_geotab");
        bOpener.extend(vueBtn);
        
        baseTable.setSelectable(true);
        baseTable.setNullSelectionAllowed(false);
        baseTable.setSizeFull();
       
        baseTable.setRowHeaderMode(RowHeaderMode.INDEX);
        baseTable.addStyleName(ValoTheme.TABLE_SMALL);
        baseTable.setVisibleColumns("layername","maptable","linkcolumn","ddate",
                "layertype","colnames");
                
        baseTable.addItemClickListener( new ItemClickListener() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void itemClick(ItemClickEvent event) {
                delBtn.setEnabled(true);
                vueBtn.setEnabled(true);
                
                bOpener.setUrl( geofuseURL + 
                        event.getItem().getItemProperty("tabid").getValue() ); 
            }
        });
        
        delBtn.setEnabled(false);
        vueBtn.setEnabled(false);

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setSpacing(true);
        btnLayout.setMargin( new MarginInfo(false,false,true,false) );
        btnLayout.addComponents(vueBtn,delBtn);
        btnLayout.setSizeUndefined();
        
        this.addComponents(baseTable,btnLayout);
        this.setDeleteEvent(baseTable); 
        
        this.setExpandRatio(baseTable, 2.0f);
        this.setExpandRatio(btnLayout, 1.0f);
    }

    private void setDeleteEvent(final Table baseTable) {
        delBtn.addClickListener(new ClickListener() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {

                if( baseTable.getValue() == null ) {
                    return;
                }
                
                final ConfirmWin cw = new ConfirmWin();
                
                cw.setModal(true);
                cw.setClosable(true);
                cw.setResizable(false);
                cw.setWidth("400px");
                cw.setHeight("230px");               
                cw.init( "Delete Selected Record?",false);
                
                cw.ok.addClickListener(new ClickListener() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        
                        try {
                            LOGGER.debug( "Delete: {}",baseTable.getValue() );
                            
                            /**
                             * dropping CSV table
                             */
                            String sql = "drop table "+ baseTable.getValue();
                            DBTools.update(sql);
                            
                            /**
                             * deleting from MetaData table
                             */
                            baseTable.removeItem( baseTable.getValue() );
                            SQLContainer sqlc = 
                                    (SQLContainer)baseTable.getContainerDataSource();
                            sqlc.commit();
                            sqlc.refresh();                    
                        }
                        catch( Exception e ) {
                            LOGGER.error( e );
                        }                
                        finally {
                            cw.closeWindow();
                            delBtn.setEnabled(false);
                        }
                    }
                });
                getUI().addWindow(cw);                
            }
        });   
    }
    
    /* (非 Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {

    }

}
