/**
 * パッケージ名：geotheme.geofuse_admin.sub_views
 * ファイル名  ：StatisticsView.java
 * 
 * @author mbasa
 * @since Jul 12, 2017
 */
package geotheme.geofuse_admin.sub_views;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
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
        

    }

    /* (非 Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
        
    }

}
