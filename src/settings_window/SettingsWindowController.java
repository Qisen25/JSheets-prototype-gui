/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings_window;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kei Wang 19126089
 */
public class SettingsWindowController implements Initializable
{

    @FXML
    void closeHandler(ActionEvent event) {
        Button but = (Button) event.getSource();
        Stage stage = (Stage) but.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private MenuButton menuPos, macroMenu, encryptMenu, themeMenu, cellHiMenu;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
        
        loadSettings();
    }    
    
    //stub for loading settings
    private void loadSettings()
    {
        //if settings prefers right position for menu then set button text to right
        if(true)
        {
            menuPos.setText("Right");
        }
        else
        {
            menuPos.setText("Right");
        }
        
        macroMenu.setText("Enabled");
        encryptMenu.setText("Enabled");
        
        macroMenu.setTooltip(new Tooltip("Macros are a set of stored functions that can be used to\nautomate processes that are repeated often.\n" + 
                                        "Enabling this will allow you to provide a file that contains instructrions for automated tasks."));
        encryptMenu.setTooltip(new Tooltip("Allows you to protect spreadsheet using encryption methods"));
        menuPos.setTooltip(new Tooltip("Moves side menu to right or left side of JSheets interface."));
        
        themeMenu.setTooltip(new Tooltip("Changes current color theme of JSheets."));
        cellHiMenu.setTooltip(new Tooltip("Changes cell selection highlight indicator."));
    }
    
}
