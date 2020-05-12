/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package help_window;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Kei Wang 19126089
 */
public class HelpWindowController implements Initializable
{

    @FXML
    private Label getStartPara, tipsPara, iconInfo, cellInfo, toolInfo, futureRelease;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        getStartPara.setText("Getting started with JSheets is very simple. No set up required, Just plug and play!");
        getStartPara.setWrapText(true);
        tipsPara.setText("• To type data into spreadsheet, double click on a cell and begin typing\n" + 
                         "   whatever and hit enter key when you finish typing your desired input.\n" + 
                         "\n• To select multiple cells, you can drag on the spreadsheet or type a\n" + 
                         "   range of cells into the Enter Cells... text box on the bottom left.\n" + 
                         "\n• Most buttons can be found on the right pane, only one or two clicks\n" + 
                         "   to perform a task!\n" + 
                         "\n• If you ever forget what something does, just keep your mouse\n" + 
                         "   hovered on the thing and a tooltip will explain what it is to you.");
        tipsPara.setWrapText(true);
        
        iconInfo.setText("Icons are buttons that provide visual representation of a task.\n" + 
                         "For example, Saving a file is usually represented as floppy disk.\n" +
                         "These buttons can be found on right pane, commonly grouped in orange trays.\n" + 
                         "The buttons should easily be spotted and only require one click.");
        iconInfo.setWrapText(true);
        
        cellInfo.setText("Steps for typing data into the spreadsheet.\n" + 
                         "1. Double click on any cell on of your choice.\n" + 
                         "2. A text field should pop up allowing you to type,\n    you can type what ever you desire.\n" + 
                         "3. After you are done typing, press enter key to confirm cell input\n" + 
                         "Repeat these steps as much as you like!");
        cellInfo.setWrapText(true);
        
        toolInfo.setText("Specific functionality that is not used by the average users can be found in the tools menu on the right pane." + 
                         "\nAll menus in the tray can be toggled on or off so that you don't always need to click to navigate through menus!\n" + 
                         "\nCharts menu: will provide functionality for creating and viewing charts of your data in a separate window.\n" + 
                          "\nMath menu: upon selecting a button, a mini window will be opened allowing you to perform math on selected cells" + 
                          " and outputting result on a cell you specify from this mini window.\n" + 
                          "\nSorts menu will enable you to sort selected cell data directly with a button click, Time will enable you to add time data such as current date with just one click!");
        toolInfo.setWrapText(true);
        
        futureRelease.setText("1. Option to move right pane to left side of interface for users who prefer it on the left\n   side.\n" + 
                              "2. Enter data into cells without pressing Enter to confirm edit.\n" + 
                              "3. More functionality and integration with XSpread.\n" + 
                              "4. TODO: add more functional windows.");
        futureRelease.setWrapText(true);
        
    }    
    
}
