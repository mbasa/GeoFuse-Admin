/**
 * パッケージ名：geotheme.geofuse_admin.sub_views
 * ファイル名  ：InsallationView.java
 * 
 * @author mbasa
 * @since Jun 13, 2017
 */
package geotheme.geofuse_admin.sub_views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 説明：
 *
 */
public class InstallationView extends VerticalLayout implements View {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final String NAME = "InstallationView";
    
    /**
     * コンストラクタ
     *
     */
    public InstallationView() {

        ThemeResource tr = new ThemeResource("graphics/Favicon16x16.png");
        Image logo  = new Image(null,tr);
        
        Label header = new Label( "GeoFuse Installation" );
        header.addStyleName(ValoTheme.LABEL_H1);
        header.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.setSizeUndefined();

        HorizontalLayout tlayout = new HorizontalLayout();
        tlayout.addComponents(logo,header);
        tlayout.setComponentAlignment(logo  , Alignment.TOP_LEFT);
        tlayout.setComponentAlignment(header, Alignment.MIDDLE_CENTER);
        
        CustomLayout html = new CustomLayout( "geofuse_installation" );
        html.setWidth("100%");
        
        this.setSpacing(true);
        this.setMargin(true);
        this.addComponents( tlayout,html );
    }

    /**
     * コンストラクタ
     *
     * @param children
     */
    public InstallationView(Component... children) {
        super(children);
    }

    /* (非 Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
    }

}
