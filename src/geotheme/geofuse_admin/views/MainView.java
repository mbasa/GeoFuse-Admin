/**
 * パッケージ名：geotheme.geofuse_admin.views
 * ファイル名  ：MainView.java
 * 
 * @author mbasa
 * @since May 17, 2017
 */
package geotheme.geofuse_admin.views;

import geotheme.geofuse_admin.sub_views.BaseMapView;
import geotheme.geofuse_admin.sub_views.MapLinkerDataView;
import geotheme.geofuse_admin.sub_views.MapLinkerView;
import geotheme.geofuse_admin.sub_views.MarkerUploadView;
import geotheme.geofuse_admin.sub_views.OverlayLayerView;
import geotheme.geofuse_admin.sub_views.UserInfView;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 説明：
 *
 */
public class MainView extends HorizontalLayout {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Logger LOGGER       = LogManager.getLogger();
    private Navigator navigator = null;

    /**
     * コンストラクタ
     *
     * @param children
     */
    public MainView(Component... children) {
        super(children);
    }

    /**
     * コンストラクタ
     *
     */
    public MainView() {
        LOGGER.debug("In MainView" );
        
        final CssLayout menuLayout = new CssLayout();
        final CssLayout contentLayout = new CssLayout();
        
        menuLayout.addStyleName(ValoTheme.MENU_ROOT);
        menuLayout.addStyleName(ValoTheme.MENU_PART);
        
        contentLayout.addStyleName("valo-content");
        contentLayout.addStyleName("v-scrollable");
        contentLayout.setSizeFull();
        
        this.setSizeFull();
        this.addComponents(menuLayout,contentLayout);
        this.setExpandRatio(contentLayout, 1.0f);
        this.addStyleName(ValoTheme.UI_WITH_MENU);
        
        Responsive.makeResponsive(this);
        
        menuLayout.addComponent( buildMenu( null ) );
        
        ComponentContainer viewDisplay = contentLayout;
        navigator = new Navigator(UI.getCurrent(),viewDisplay);
        
        navigator.addView(MarkerUploadView.NAME ,MarkerUploadView.class);
        navigator.addView(BaseMapView.NAME      ,BaseMapView.class);
        navigator.addView(OverlayLayerView.NAME ,OverlayLayerView.class);
        navigator.addView(MapLinkerView.NAME    ,MapLinkerView.class);
        navigator.addView(UserInfView.NAME      ,UserInfView.class);
        navigator.addView(MapLinkerDataView.NAME,MapLinkerDataView.class);
        
        navigator.navigateTo(MarkerUploadView.NAME);
        navigator.setErrorView(MarkerUploadView.class);
    }

    private CssLayout buildMenu( final ResourceBundle rb ) {
        LOGGER.debug("In buidlMenu()");
        
        final CssLayout menu = new CssLayout();
        final CssLayout menuItemsLayout = new CssLayout();
        
        final LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();
        
        Button showMenu = new Button("Menu", new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (menu.getStyleName().contains("valo-menu-visible")) {
                    menu.removeStyleName("valo-menu-visible");
                } else {
                    menu.addStyleName("valo-menu-visible");
                }
            }
        });
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName("valo-menu-toggle");
        showMenu.setIcon(FontAwesome.LIST);
        menu.addComponent(showMenu);

        // Add items
        /*
        menuItems.put(UserSettingsView.NAME     , rb.getString("MENU.SETTINGS"));
        */
        menuItems.put( MarkerUploadView.NAME , "MarkersUpload" );
        menuItems.put( BaseMapView.NAME      , "BaseMap" );
        menuItems.put( OverlayLayerView.NAME , "OverlayLayer");
        menuItems.put( MapLinkerView.NAME    , "MapLinkerLayer");
        menuItems.put( MapLinkerDataView.NAME, "MapLinkerData");
        menuItems.put( UserInfView.NAME      , "UserAdmin");
        
        final Button buttons[] = new Button[menuItems.size()];

        final HorizontalLayout top = new HorizontalLayout();
        top.setWidth("100%");
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName(ValoTheme.MENU_TITLE);
        menu.addComponent(top);
        
        final Label title = new Label("Geofuse Admin");
        title.addStyleName(ValoTheme.LABEL_H4);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.setSizeUndefined();
        
        top.addComponent(title);
        top.setExpandRatio(title, 1);

        final MenuBar settings = new MenuBar();
        settings.addStyleName("user-menu");
        
        String user = (String)UI.getCurrent().getSession().getAttribute("user");
        
        final MenuItem settingsItem = settings.addItem(
                user.toUpperCase(),
                new ThemeResource("graphics/profile-pic-300px.jpg"),
                null);
        
        MenuBar.Command command = new MenuBar.Command() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void menuSelected(MenuItem selectedItem) {
                //LOGGER.debug( selectedItem.getText() );  
                /**
                 * Logging Out
                 */
                if( selectedItem.getText().equalsIgnoreCase( "Logout" ) ) {
                        //rb.getString("LOGOUT_BTN") ) ){
                    getSession().close();
                    Page.getCurrent().reload();
                }
            }
        };

        settingsItem.addItem( "Logout", null,command);
        menu.addComponent(settings);

        menuItemsLayout.setPrimaryStyleName("valo-menuitems");
        menu.addComponent(menuItemsLayout);

        int count = 0;
        int icon  = 85;

        List<FontAwesome> ICONS = Collections.unmodifiableList(Arrays
                .asList(FontAwesome.values()));
              
        for (final Entry<String, String> item : menuItems.entrySet()) {
            
            buttons[count] = new Button(item.getValue(), new ClickListener() {
                
                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(final ClickEvent event) {

                    navigator.navigateTo(item.getKey());
                    LOGGER.debug("Navigating to: {}", item.getKey() );
                    
                    for(int i=0; i<buttons.length;i++) {
                        buttons[i].removeStyleName("selected");
                        event.getButton().addStyleName("selected");
                    }
                    menu.removeStyleName("valo-menu-visible");
                }                
            });
            
            int selected = 0;

            if( count == selected ) {
                buttons[count].addStyleName("selected");
            }

            buttons[count].setHtmlContentAllowed(true);
            buttons[count].setPrimaryStyleName("valo-menu-item");
            buttons[count].setIcon( ICONS.get(icon++) );
            menuItemsLayout.addComponent(buttons[count]);
            count++;
        }

        return menu;
    }

}
