/**
 * パッケージ名：geotheme.geofuse_admin.windows
 * ファイル名  ：BaseLayerWin.java
 * 
 * @author mbasa
 * @since May 23, 2017
 */
package geotheme.geofuse_admin.windows;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 説明：
 *
 */
public class TableEditWin extends Window {

    private static final long serialVersionUID = 1L;

    /**
     * コンストラクタ
     *
     */
    public TableEditWin() {
    }

    /**
     * コンストラクタ
     *
     * @param caption
     */
    public TableEditWin(String caption) {
        super(caption);
    }

    /**
     * コンストラクタ
     *
     * @param caption
     * @param content
     */
    public TableEditWin(String caption, Component content) {
        super(caption, content);
    }

    public void init( Item item, final Table baseTable, 
            String cols[][], String mTitle ) {
     
        FormLayout form   = new FormLayout();
        form.setWidth("90%");
        form.setHeightUndefined();
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        
        final ArrayList<TextField> components = new ArrayList<TextField>();
        
        for( int i=0; i < cols.length; i++ ) {
            if( cols[i][1].equalsIgnoreCase("boolean") ) {
                HorizontalLayout dispLayout = new HorizontalLayout();
                dispLayout.setCaption( cols[i][0] );
                
                CheckBox cb = new CheckBox();
                cb.setPropertyDataSource( 
                        item.getItemProperty( cols[i][0].toLowerCase()) );
                
                dispLayout.addComponent(cb);
                dispLayout.setWidth("100%");
                
                form.addComponent( dispLayout );
                components.add(null);
            }
            else if( cols[i][1].equalsIgnoreCase("combo") ) {
                ComboBox cb  = new ComboBox( cols[i][0] );
                String its[] = cols[i][3].split(",");
                
                if( its != null ) {
                    for(String s : its ) {
                        cb.addItem(s);
                    }
                    
                    cb.setTextInputAllowed(false);
                    cb.setNewItemsAllowed(false);
                    cb.setNullSelectionAllowed(false);                                

                    cb.setPropertyDataSource( 
                            item.getItemProperty( cols[i][0].toLowerCase() ) );
                    
                    if( cb.getValue() == null || 
                        cb.getValue().toString().length() == 0) {
                        cb.setValue( its[0] );
                    }                    
                    form.addComponent( cb );
                }
                components.add(null);
            }
            else {
                TextField text = new TextField( cols[i][0] );
                text.setPropertyDataSource( 
                        item.getItemProperty( cols[i][0].toLowerCase() ) );
                text.setNullRepresentation("");
                text.setWidth("100%");
                text.addStyleName(ValoTheme.TEXTFIELD_SMALL);
                
                /**
                 * Setting Validators
                 */
                if( cols[i][2] != null ) {
                    if( cols[i][2].equalsIgnoreCase("NotNull") ) {
                        text.addValidator( new StringLengthValidator(
                                "input is empty or too short", 1, 1000000, false));
                        text.setValidationVisible(false);
                    }
                    if( cols[i][2].equalsIgnoreCase("Integer") ) {
                        text.addValidator( new NullValidator(
                                "can not be empty",false) );

                        text.addValidator( new IntegerRangeValidator(
                                "value should be 0-999", 0,999) );
                        text.setValidationVisible(false);
                    }
                }
                
                form.addComponent(text);        
                components.add(text); 
            }
        }
        
        Label title = new Label( mTitle );
        title.addStyleName( ValoTheme.LABEL_COLORED );
        title.addStyleName( ValoTheme.LABEL_NO_MARGIN );
        title.addStyleName( ValoTheme.LABEL_H3 );
        title.setSizeUndefined();
        
        final Label error = new Label( "Error: check input data." );
        error.addStyleName(ValoTheme.LABEL_TINY);
        error.addStyleName(ValoTheme.LABEL_FAILURE);
        error.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        error.setSizeUndefined();
        error.setVisible(false);
        
        Button saveBtn   = new Button( "Save" );
        Button cancelBtn = new Button( "Cancel" );
        
        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setSpacing(true);
        btnLayout.addComponents(saveBtn,cancelBtn);
        
        VerticalLayout vlay = new VerticalLayout();
        //vlay.setSizeFull();
        vlay.setMargin(new MarginInfo(true,true,false,true));
        vlay.setSpacing(true);        
        
        vlay.addComponents(title,form,btnLayout,error);
        vlay.setComponentAlignment(form, Alignment.TOP_CENTER);
        vlay.setComponentAlignment(btnLayout, Alignment.MIDDLE_CENTER);
        vlay.setComponentAlignment(error, Alignment.BOTTOM_CENTER);
        vlay.setExpandRatio(btnLayout, 1.0f);
                
        this.setContent( vlay );
        
        saveBtn.addClickListener( new ClickListener() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                
                try {
                    for( TextField c : components ) {
                        if( c != null && c.getValidators() != null )  {
                            c.setValidationVisible(true);
                            c.validate();
                        }
                    }
                }
                catch(Exception e) {
                    return;
                }
                
                SQLContainer sqlc = 
                        (SQLContainer)baseTable.getContainerDataSource();
                try {
                    sqlc.commit();
                } 
                catch(SQLIntegrityConstraintViolationException e) {
                    e.printStackTrace();
                    error.setValue("Duplicate Primary Key");
                    error.setVisible(true);
                    return;
                }
                catch (UnsupportedOperationException e) {
                    e.printStackTrace();
                    error.setVisible(true);
                    return;
                } 
                catch (SQLException e) {
                    e.printStackTrace();
                    error.setVisible(true);
                    return;
                }
                sqlc.refresh();
                closeWindow();
            }
        });

        cancelBtn.addClickListener( new ClickListener() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                SQLContainer sqlc = 
                        (SQLContainer)baseTable.getContainerDataSource();
                try {
                    sqlc.rollback();
                } 
                catch (UnsupportedOperationException e) {
                    e.printStackTrace();
                } 
                catch (SQLException e) {
                    e.printStackTrace();
                }
                sqlc.refresh();
                closeWindow();
            }
        });
    }
    
    public void closeWindow() {
        this.close();
    }
}
