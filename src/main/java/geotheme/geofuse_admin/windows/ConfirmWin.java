/**
 * パッケージ名：geotheme.geofuse_admin.windows
 * ファイル名  ：ConfirmWin.java
 * 
 * @author mbasa
 * @since May 19, 2017
 */
package geotheme.geofuse_admin.windows;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 説明：
 *
 */
public class ConfirmWin extends Window {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * コンストラクタ
     *
     */
    public ConfirmWin() {
    }

    /**
     * コンストラクタ
     *
     * @param caption
     */
    public ConfirmWin(String caption) {
        super(caption);
    }

    /**
     * コンストラクタ
     *
     * @param caption
     * @param content
     */
    public ConfirmWin(String caption, Component content) {
        super(caption, content);
    }

    public Button ok         = new Button();
    public Button cancel     = new Button();
    public TextArea question = new TextArea();
    
    /**
     * Creates Message Window with Cancel Button
     * 
     * 
     * @param message
     * @param withQuestion
     */
    public void init(String message, boolean withQuestion) {
        this.init(message, withQuestion,true);
    }
    
    /**
     * Creates Message Window
     * 
     * 
     * @param message
     * @param withQuestion
     * @param withCancelBtn
     */
    @SuppressWarnings("serial")
    public void init(String message, boolean withQuestion,
            boolean withCancelBtn) {
        
        VerticalLayout   vlayout  = new VerticalLayout();
        VerticalLayout h1Layout = new VerticalLayout();
        HorizontalLayout h2Layout = new HorizontalLayout();
        
        Label label = new Label(message);
        label.addStyleName("h2");
        h1Layout.addComponent(label);
        h1Layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        h1Layout.setHeight("100%");
        h1Layout.setSpacing(true);
        h1Layout.setMargin(true);
                
        if( withQuestion ) {
            question.setWidth("100%");
            question.setRows(1);
            h1Layout.addComponent(question);
        }
        
        ok.setCaption("OK");
        //ok.addStyleName(ValoTheme.BUTTON_DANGER);
        if( !withCancelBtn ) {
            ok.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    closeWindow();                
                }

            });            
        }
        h2Layout.addComponents(ok);
        
        if( withCancelBtn ) {
            cancel.setCaption("Cancel");
            cancel.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    closeWindow();                
                }

            });
            h2Layout.addComponents(cancel);
        }
        h2Layout.addComponents(ok);
        h2Layout.setMargin(new MarginInfo(false,true,true,false));
        h2Layout.setSpacing(true);
        h2Layout.setHeight("100%");
        
        vlayout.addComponents(h1Layout,h2Layout);
        vlayout.setExpandRatio(h1Layout, 1.0f);
        vlayout.setComponentAlignment(h1Layout, Alignment.MIDDLE_CENTER);
        vlayout.setComponentAlignment(h2Layout,Alignment.BOTTOM_RIGHT);
        this.setContent(vlayout);
        
    }
    
    public void closeWindow() {
        this.close();
    }
}
