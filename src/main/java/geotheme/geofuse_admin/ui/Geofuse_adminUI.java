package geotheme.geofuse_admin.ui;

import geotheme.geofuse_admin.views.LoginView;
import geotheme.geofuse_admin.views.MainView;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("geofuse_admin")
public class Geofuse_adminUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(
	        productionMode = false, 
	        ui = Geofuse_adminUI.class,
	        widgetset = "geotheme.geofuse_admin.ui.widgetset.Geofuse_adminWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
	    
	    if( getSession().getAttribute("user") == null ) {
	        setContent( new LoginView() );
	    }
	    else {
	        setContent( new MainView() );
	    }
	}

}