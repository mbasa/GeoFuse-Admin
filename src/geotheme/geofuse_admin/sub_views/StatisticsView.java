/**
 * パッケージ名：geotheme.geofuse_admin.sub_views
 * ファイル名  ：StatisticsView.java
 * 
 * @author mbasa
 * @since Jul 12, 2017
 */
package geotheme.geofuse_admin.sub_views;
import geotheme.geofuse_admin.db.DBTools;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dussan.vaadin.dcharts.DCharts;
import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.base.renderers.LabelRenderer;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.data.Ticks;
import org.dussan.vaadin.dcharts.metadata.TooltipAxes;
import org.dussan.vaadin.dcharts.metadata.XYaxes;
import org.dussan.vaadin.dcharts.metadata.locations.TooltipLocations;
import org.dussan.vaadin.dcharts.metadata.renderers.AxisRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.LabelRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.Axes;
import org.dussan.vaadin.dcharts.options.AxesDefaults;
import org.dussan.vaadin.dcharts.options.Highlighter;
import org.dussan.vaadin.dcharts.options.Legend;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.SeriesDefaults;
import org.dussan.vaadin.dcharts.renderers.series.PieRenderer;
import org.dussan.vaadin.dcharts.renderers.tick.AxisTickRenderer;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 説明：
 *
 */
public class StatisticsView extends VerticalLayout implements View {

    private static final long serialVersionUID = 1L;
    public  static final String NAME = "StatisticsView";
    
    private final Logger LOGGER     = LogManager.getLogger();
    private final ResourceBundle rb = ResourceBundle.getBundle( 
            "properties/lang/StatisticsView",
            UI.getCurrent().getSession().getLocale()
          );

    /**
     * コンストラクタ
     *
     * @param children
     */
    public StatisticsView(Component... children) {
        super(children);
    }


    /**
     * コンストラクタ
     *
     */
    public StatisticsView() {
        LOGGER.debug("In Statistics View");
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
        
        Label desc_title = new Label( rb.getString("GRAPH.TITLE") );
        desc_title.addStyleName(ValoTheme.LABEL_H2);
        desc_title.addStyleName(ValoTheme.LABEL_COLORED);
        desc_title.setSizeUndefined();
        
        Label pie_title = new Label( rb.getString("PIE.TITLE") );
        pie_title.addStyleName(ValoTheme.LABEL_H2);
        pie_title.addStyleName(ValoTheme.LABEL_COLORED);
        pie_title.setSizeUndefined();
        
        Responsive.makeResponsive(this);

        this.setSpacing(true);
        this.setMargin( new MarginInfo(true,true,false,true));

        DCharts pieChart = this.makeTypeGraph();
        pieChart.setSizeFull();
        pieChart.show();

        DCharts chart = this.makeUsageGraph();
        chart.setSizeFull();
        chart.show();
                
        this.addComponents( tlayout,pie_title,pieChart,desc_title,chart );
    }

    private DCharts makeUsageGraph() {
        String sql = "select "
                + "(select count(*) from geofuse.metadata where ddate < now() and ddate > now() - '1 month'::interval) as c1,"
                + "(select count(*) from geofuse.metadata where ddate < now() - '1 month'::interval and ddate > now()  - '2 months'::interval) as c2,"
                + "(select count(*) from geofuse.metadata where ddate < now() - '2 months'::interval and ddate > now() - '3 months'::interval) as c3,"
                + "(select count(*) from geofuse.metadata where ddate < now() - '3 months'::interval and ddate > now() - '4 months'::interval) as c4,"
                + "(select count(*) from geofuse.metadata where ddate < now() - '4 months'::interval and ddate > now() - '5 months'::interval) as c5,"
                + "(select count(*) from geofuse.metadata where ddate < now() - '5 months'::interval and ddate > now() - '6 months'::interval) as c6,"
                + "(select count(*) from geofuse.metadata where ddate < now() - '6 months'::interval and ddate > now() - '7 months'::interval) as c7,"
                + "(select count(*) from geofuse.metadata where ddate < now() - '7 months'::interval and ddate > now() - '8 months'::interval) as c8,"
                + "(select count(*) from geofuse.metadata where ddate < now() - '8 months'::interval and ddate > now() - '9 months'::interval) as c9,"
                + "(select count(*) from geofuse.metadata where ddate < now() - '9 months'::interval and ddate > now() - '10 months'::interval) as c10,"
                + "(select count(*) from geofuse.metadata where ddate < now() - '10 months'::interval and ddate > now() - '11 months'::interval) as c11,"
                + "(select count(*) from geofuse.metadata where ddate < now() - '11 months'::interval and ddate > now() - '12 months'::interval) as c12";
        
        Object dataDB[] = DBTools.getSingleRecord( sql );
        
        /*
        Title title = new Title( rb.getString("GRAPH.TITLE") );
        title.setFontSize("14px");
        title.setTextColor("CadetBlue");
        */
        AxesDefaults axesDef = new AxesDefaults();
        axesDef.setAutoscale(true);
        axesDef.setLabelRenderer(LabelRenderers.CANVAS);
        
        Axes axes = new Axes();
        
        XYaxis xAxis = new XYaxis( XYaxes.X );
        xAxis.setLabel( rb.getString("GRAPH.X-AXIS") );
        xAxis.setLabelRenderer(LabelRenderers.CANVAS);
        xAxis.setRenderer(AxisRenderers.CATEGORY);
        xAxis.setTicks(new Ticks().add("1","2","3","4",
                "5","6","7","8","9","10","11","12") );
        xAxis.setLabelOptions(
                new LabelRenderer<Object>(true,"","","15px","black"));


        XYaxis yAxis = new XYaxis( XYaxes.Y );
        yAxis.setLabel( rb.getString("GRAPH.Y-AXIS") );
        yAxis.setLabelRenderer(LabelRenderers.CANVAS);
        yAxis.setTickOptions(
                new AxisTickRenderer().setFormatString("%d "));
        yAxis.setLabelOptions(
                new LabelRenderer<Object>(true,"","","15px","black"));
        
        axes.addAxis( xAxis );
        axes.addAxis( yAxis );
        
        Highlighter highlighter = new Highlighter();
        highlighter.setKeepTooltipInsideChart(true);
        highlighter.setShow(true);
        highlighter.setShowTooltip(true);
        highlighter.setTooltipLocation(TooltipLocations.NORTH);
        highlighter.setTooltipAxes(TooltipAxes.Y);
        highlighter.setTooltipFormatString("%s "+
                rb.getString("GRAPH.TOOLTIP") );
        highlighter.setUseAxesFormatters(false);
        
        Options options = new Options();
        options.setAxes(axes);
        options.setAxesDefaults(axesDef);
        options.setHighlighter(highlighter);
        //options.setTitle(title);
        
        DataSeries dataSeries = new DataSeries();
        
        dataSeries.add(
                dataDB[0],dataDB[1],dataDB[2],
                dataDB[3],dataDB[4],dataDB[5],
                dataDB[6],dataDB[7],dataDB[8],
                dataDB[9],dataDB[10],dataDB[11] );
        
        DCharts dChart = new DCharts();
        
        dChart.setDataSeries(dataSeries);
        dChart.setOptions(options);
               
        return dChart;
    }
    
    private DCharts makeTypeGraph() {
        DataSeries dataSeries = new DataSeries();
        dataSeries.newSeries().add(rb.getString("PIE.POLYGON"),95);
        dataSeries.newSeries().add(rb.getString("PIE.LINE"),29);
        dataSeries.newSeries().add(rb.getString("PIE.POINT"),15);
        
        PieRenderer pieRenderer = new PieRenderer();
        pieRenderer.setShowDataLabels(true);
        pieRenderer.setFill( true );
        pieRenderer.setSliceMargin(10);
        pieRenderer.setLineWidth(1);
        pieRenderer.setShadowDepth(8);
        
        SeriesDefaults seriesDefaults = new SeriesDefaults();
        seriesDefaults.setRenderer(SeriesRenderers.PIE);
        seriesDefaults.setRendererOptions( pieRenderer );
        
        Legend legend = new Legend();
        legend.setShow(true);
        
        Highlighter hLighter = new Highlighter();
        hLighter.setShow(true);
        hLighter.setShowTooltip(true);
        hLighter.setTooltipAlwaysVisible(true);
        hLighter.setKeepTooltipInsideChart(true);
        
        Options options = new Options();
        options.setSeriesDefaults(seriesDefaults);
        options.setLegend(legend);
        options.setHighlighter(hLighter);
        
        DCharts dChart = new DCharts();
        dChart.setDataSeries(dataSeries);
        dChart.setOptions(options);
        
        return dChart;
    }
    
    /* (非 Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
        
    }

}
