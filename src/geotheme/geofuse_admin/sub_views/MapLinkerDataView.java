/**
 * パッケージ名：geotheme.geofuse_admin.sub_views
 * ファイル名  ：BaseLayerView.java
 * 
 * @author mbasa
 * @since May 22, 2017
 */
package geotheme.geofuse_admin.sub_views;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import geotheme.geofuse_admin.db.ConnectionPoolHolder;
import geotheme.geofuse_admin.db.DBTools;
import geotheme.geofuse_admin.db_beans.LinkColBean;

import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Responsive;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.themes.ValoTheme;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 * 説明：
 *
 */
public class MapLinkerDataView extends VerticalLayout implements View {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final String NAME = "MapLinkerDataView";
    
    private Logger LOGGER     = LogManager.getLogger();
    private ResourceBundle rb = ResourceBundle.getBundle( 
            "properties/lang/MapLinkerDataView",
            UI.getCurrent().getSession().getLocale()
          );


    /**
     * コンストラクタ
     *
     * @param children
     */
    public MapLinkerDataView(Component... children) {
        super(children);
    }

    /**
     * コンストラクタ
     *
     */
    public MapLinkerDataView() {
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

        Label desc_title = new Label( rb.getString("TABLE") );
        desc_title.addStyleName(ValoTheme.LABEL_H2);
        desc_title.addStyleName(ValoTheme.LABEL_COLORED);
        desc_title.setSizeUndefined();
        
        Responsive.makeResponsive(this);

        this.setSpacing(true);
        this.setMargin( new MarginInfo(true,true,false,true));
        this.setSizeFull();
        
        this.addComponents(tlayout,desc_title);
        this.setupTable();        
    }

    private final Table baseTable = new Table();
    
    private void setupTable() {
        
        final TableQuery tq   = new TableQuery(null,"geofuse","maplinker",
                ConnectionPoolHolder.getConnectionPool(), 
                new DefaultSQLGenerator() );

        try{
            SQLContainer sqlContainer = new SQLContainer( tq );
            sqlContainer.sort(new Object[] {"mapname"}, new boolean[] {true} );
            /**
             * filtering out the latlon x,y point linker
             */
            sqlContainer.addContainerFilter(new Not(
                    new Compare.Equal("colname","latlon")));

            baseTable.setContainerDataSource(sqlContainer);
        }
        catch( Exception e ) {
            LOGGER.error( e );
        }
        
        baseTable.setSelectable(true);
        baseTable.setNullSelectionAllowed(false);
        baseTable.setSizeFull();
        
        baseTable.setRowHeaderMode(RowHeaderMode.INDEX);
        baseTable.addStyleName(ValoTheme.TABLE_SMALL);
        baseTable.setVisibleColumns("mapname","colname");
        baseTable.setColumnHeader("mapname", "Link MapTable");
        baseTable.setColumnHeader("colname", "Column Name");
        
        if( baseTable.getItemIds() != null && !baseTable.getItemIds().isEmpty() ) {
            baseTable.select( baseTable.getItemIds().iterator().next()  );
        }
        
        Button exportBtn = new Button( rb.getString("BTN.EXPORT") );
        
        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setSpacing(true);
        btnLayout.setMargin( new MarginInfo(false,false,true,false) );
        btnLayout.addComponents( exportBtn );
        btnLayout.setSizeUndefined();
        
        this.addComponents(baseTable,btnLayout);
        FileDownloader filed = new FileDownloader( getExcelResource( ) );
        filed.extend( exportBtn );
        
        this.setExpandRatio(baseTable, 2.0f);
        this.setExpandRatio(btnLayout, 1.0f);
    }
        
    private StreamResource getExcelResource( ) {
        LOGGER.debug("In getExcelResource");
        
        final int limit = 20000;
        
        if( baseTable.getValue() == null ) {
            return null;
        }
        
        StreamSource streamSource = new StreamSource() {

            private static final long serialVersionUID = 1L;

            @Override
            public InputStream getStream() {
                LOGGER.debug("Creating excel file");
                try {
                    Item item = baseTable.getItem( baseTable.getValue() );
                    
                    String colname = (String) item.getItemProperty("colname").getValue();
                    String mapname = (String) item.getItemProperty("mapname").getValue();
                    String sql = "select "+ colname +" as colname from "+ mapname +
                            " where "+ colname +" is not null group by "+ colname +
                            " order by "+ colname +" limit "+ limit;
                    
                    LOGGER.debug("Excel SQL: {}",sql);
                    
                    List<LinkColBean> res = DBTools.getRecords(sql, LinkColBean.class);

                    if( res == null ) {
                        LOGGER.debug("No MapLinker Data found");
                        return null;
                    }

                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet sheet = workbook.createSheet("MapLink");
                    
                    int rowNum = 0;
                    Row row = sheet.createRow(rowNum++);
                    Cell cell = row.createCell(0);
                    cell.setCellValue( colname );
                    
                    for (LinkColBean datatype : res ) {
                        row  = sheet.createRow(rowNum++);
                        cell = row.createCell(0);
                        cell.setCellValue( datatype.getColname() );
                    }

                    if( rowNum > limit ) {
                        row  = sheet.createRow(rowNum++);
                        cell = row.createCell(0);
                        cell.setCellValue( "*** Record Limit has been reached."
                                + "Some data may not be included ***" );                    
                    }
                    
                    ByteArrayOutputStream arrayOutputStream = 
                            new ByteArrayOutputStream();

                    workbook.write( arrayOutputStream );
                    workbook.close();

                    return new ByteArrayInputStream(
                            arrayOutputStream.toByteArray());
                }
                catch(Exception e) {
                    LOGGER.error( e,e );
                }
                return null;
            }
            
        };
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String filen = "table_"+sdf.format( new Date() )+".xlsx";
        
        StreamResource sr = new StreamResource(streamSource, filen );
        sr.setMIMEType("application/vnd.ms-excel");

        return sr;
    }
            
    /* (非 Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {

    }

}
