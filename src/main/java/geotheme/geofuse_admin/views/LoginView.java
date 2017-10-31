/**
 * パッケージ名：geotheme.geofuse_admin.views
 * ファイル名  ：LoginView.java
 * 
 * @author mbasa
 * @since May 17, 2017
 */
package geotheme.geofuse_admin.views;

import java.util.ResourceBundle;

import geotheme.geofuse_admin.db.DBTools;

import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 説明：
 *
 */
public class LoginView extends VerticalLayout {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = ResourceBundle.getBundle( 
            "properties/lang/LoginView",
            UI.getCurrent().getSession().getLocale()
          );

    /**
     * コンストラクタ
     *
     */
    public LoginView() {
        
        VerticalLayout pLayout = new VerticalLayout();
        FormLayout form = new FormLayout();
        
        ThemeResource tr = new ThemeResource("graphics/Faviicon32x32.png");
        Image logo  = new Image(null,tr);
        
        Label title = new Label( rb.getString( "TITLE" ));
        title.setStyleName(ValoTheme.LABEL_H2);
        
        HorizontalLayout tlayout = new HorizontalLayout();
        tlayout.addComponents(logo,title);
        tlayout.setComponentAlignment(logo , Alignment.TOP_LEFT);
        tlayout.setComponentAlignment(title, Alignment.MIDDLE_CENTER);
        
        final TextField user = new TextField( rb.getString("USERNAME") );
        final PasswordField pass = new PasswordField( rb.getString("PASSWORD") );
        
        user.setWidth("80%");
        pass.setWidth("80%");
        user.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        pass.addStyleName(ValoTheme.TEXTFIELD_SMALL);        
        
        Button submitBtn = new Button( rb.getString("BTN.SUBMIT") );
        submitBtn.addStyleName(ValoTheme.BUTTON_SMALL);
              
        form.addComponents(user,pass);
        form.setSizeFull();
        
        final Label errLabel = new Label("Username or Password is Invalid");
        errLabel.setVisible(false);
        errLabel.setWidth("50%");
        errLabel.addStyleName(ValoTheme.LABEL_TINY);

        pLayout.addComponents(form,submitBtn,errLabel);
        pLayout.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
        pLayout.setComponentAlignment(submitBtn, Alignment.BOTTOM_RIGHT);
        pLayout.setComponentAlignment(errLabel, Alignment.BOTTOM_CENTER);
        pLayout.setSpacing(true);
        pLayout.setMargin(true);
        
        Panel panel = new Panel("Login");        
        panel.setWidth("50%");
        panel.setContent(pLayout);
        panel.addStyleName("color3");
                
        this.setMargin(true);
        //this.setSpacing(true);
        //this.setSizeFull();
        this.addComponents( tlayout,panel );
        this.setComponentAlignment(tlayout, Alignment.TOP_CENTER);
        this.setComponentAlignment(panel  , Alignment.MIDDLE_CENTER);
                        
        submitBtn.addClickListener(new ClickListener() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                String sql = "select username from geofuse.userinf where "
                        + "username = ? and password = ? and role = 'admin' ";
                
                if( DBTools.getColumnValue(sql, "username", 
                        user.getValue(), pass.getValue()) != null ) {
                    getSession().setAttribute("user", user.getValue());
                    Page.getCurrent().reload();
                }
                else {
                    errLabel.setVisible(true);
                }
            }
        });
    }

    /**
     * コンストラクタ
     *
     * @param children
     */
    public LoginView(Component... children) {
        super(children);
    }

}
