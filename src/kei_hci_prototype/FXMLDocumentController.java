/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kei_hci_prototype;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;

/*
 * Class handling creation of spreadsheet views
 * REFERENCES: stackoverflow and javafx docs were used to help set up listeners and views.
 *             for drag cell selection https://community.oracle.com/message/11334815#11334815
 *@author   Kei Wang 19126089
 */
public class FXMLDocumentController implements Initializable
{

    @FXML
    private AnchorPane anchor, spreadPane, bPane;
    @FXML
    private TextField cellText;
    @FXML
    private TableView<Row> table;
    @FXML
    private Pane filePane, editPane;
    @FXML
    private ListView<Row> indexList;
    @FXML
    private Label fileTitle, editTitle, cellSelected, toolsTitle, Logo;
    @FXML
    private Button saveButton, openButton, newButton, exportButton, printButton,
            undoButton, redoButton, copyButton, cutButton, pasteButton, feedbackButton, settingsButton,
            helpButton, enterCellButton, saveAsButton;
    //fields
    private Map<String, TableColumn<Row, String>> cols;
    private TableColumn prevCol = null;
    private Row prevRow = null;
    private ObservableList<Row> rowz;
    private ObservableList<TablePosition> selectedCells;
    private  ScrollBar s1, verti, horiz;
    private double sc = 0.0;
    private File currFile = null;
    
    //open file chooser
    @FXML
    void openFile(ActionEvent event) {
        Optional<ButtonType> res = null;
        if(this.currFile != null)
        {
            res = this.changeFilePrompt("Save before opening a new file? Or Do you want to stay on current sheet?");
        }
        
        if(res != null && res.get().getText().equals("Save"))
        {
            saveFile(event);
        }
        
        if(res == null || !res.get().getText().equals("Stay Here"))
        {
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File(System.getProperty("user.home")));
            fc.setTitle("Open Sheet");
            fc.getExtensionFilters().addAll(new ExtensionFilter("CSV files", "*.csv"), 
                                            new ExtensionFilter("SC files", "*.sc"),
                                            new ExtensionFilter("All files", "*"));
            File choseFile = fc.showOpenDialog(null);

            if(choseFile != null)
            {
                readFile(choseFile);
            }
            else
            {
                System.out.println("no file selected when open");
            }
        }

    }
    
    @FXML
    void saveFile(ActionEvent event) {
        if(this.currFile != null)
        {
            this.writeToFile(this.currFile, ".csv");
        }
        else
        {
            saveAs(event);
        }
    }
    
    @FXML
    void saveAs(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.setTitle("Save Sheet As...");
        fc.setInitialFileName("name.csv");
        fc.getExtensionFilters().addAll(new ExtensionFilter("CSV files", "*.csv"), 
                                        new ExtensionFilter("SC files", "*.sc"));
        File choseFile = fc.showSaveDialog(null);
        
        if(choseFile != null)
        {
            if(fc.getSelectedExtensionFilter().getDescription().equals("CSV files"))
            {
                this.writeToFile(choseFile, ".csv");
            }
        }
        else
        {
            System.out.println("file not selected for save");
        }
    }
    
    @FXML
    void newFile(ActionEvent event) {

        Optional<ButtonType> res = this.changeFilePrompt("Save before creating a new file? Or Do you want to stay on current sheet?");
        //save if user press yes
        if(res.get().getText().equals("Save"))
        {
            saveFile(event);
        }
        System.out.println(res.get());
        if(!res.get().getText().equals("Stay Here"))//if user does not press Stay Here
        {
            resetRows();
            this.setTitleOfScreen("New Sheet");
            this.currFile = null;
            table.getItems().clear();
            ObservableList<Row> list = FXCollections.observableArrayList(this.resetRows());
            table.setItems(list);
        }
    }
    
    //button to create pie chart
    @FXML
    void openPieWIndow(ActionEvent event) {
        this.createPie();
    }
    
    //create sum when sum button pressed
    @FXML
    void sumButtonResult(ActionEvent event) {
        this.sumCalcWindow();
    }
    
    //fmxml feedback button handler
    @FXML
    void startFeedback(ActionEvent event) {
        this.openWindow("/feedback_window/FeedbackWindow.fxml", 
                        "/CSS/feedback/feedbackStyle.css", "Feedback", false);
    }
    
    @FXML
    void startHelp(ActionEvent event) {
        this.openWindow("/help_window/HelpWindow.fxml", 
                        "/CSS/help/helpStyle.css", "Help", false);
    }
    
    @FXML
    void startSettings(ActionEvent event) {
        this.openWindow("/settings_window/SettingsWindow.fxml", 
                        "/CSS/settings/settingsStyle.css", "Settings", true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        this.setupSideMenuStyle();
        this.setupTooltips();

        cols = new HashMap<>();//Stores tablecolumn for easy access

        table.setEditable(true);
        //set columns headers for table
        for (char i = 'A'; i <= 'K'; i++)
        {
            TableColumn<Row, String> inCol = new TableColumn<>(String.valueOf(i));
            inCol.setId(String.valueOf(i)); 
            PropertyValueFactory<Row, String> inProp = new PropertyValueFactory<>(String.valueOf(i));
            Callback<TableColumn<Row, String>, TableCell<Row, String>> cellFactory = new DragSelectionCellFactory(TextFieldTableCell.forTableColumn());
            inCol.setCellFactory(cellFactory);
            inCol.setCellValueFactory(inProp);
            inCol.setSortable(false);
            setupColForEdit(inCol);
            table.getColumns().add(inCol);//add columns to table            
            inCol.setMinWidth(100.0);//set cell size to atleast 100
            cols.put(String.valueOf(i), inCol);
        }

        //initialise index list view
        for (int i = 0; i <= 30; i++)
        {
            Row r = new Row(String.valueOf(i));
            table.getItems().add(r);
            indexList.getItems().add(r);//String.valueOf(i));
            //table.getSelectionModel().select(r);
        }

        //set up table listeners
        selectedCells = table.getSelectionModel().getSelectedCells();
        rowz = table.getSelectionModel().getSelectedItems();

        table.getSelectionModel().setCellSelectionEnabled(true);//use cell selection model

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        

        //listen for change in cell selection
        selectedCells.addListener(new ListChangeListener<TablePosition>()
        {
            @Override
            public void onChanged(Change<? extends TablePosition> change)
            {
                //clear previous selected column header styles
                while (change.next())
                {
                    for (TablePosition tab : change.getRemoved())
                    {
                        tab.getTableColumn().getStyleClass().clear();
                    }
                }

                //go through current positions selected
                for (TablePosition t : change.getList())
                {
                    prevCol = t.getTableColumn();
                    //iterated values
                    int lastRow = Integer.valueOf(t.getRow());
                    String lastCol = t.getTableColumn().getText();
                    //get beginning cell
                    int startRow = Integer.valueOf(change.getList().get(0).getRow());
                    String startCol = change.getList().get(0).getTableColumn().getText();

                    System.out.println(t.getTableColumn().getCellData(lastRow));
                    
                    prevCol.getStyleClass().add("table-row-cell");//highlight actual cell selected
                    prevCol.getStyleClass().add("headerColor");//highlight column header when select
                    
                    cellSelected.setText("Cells Selected: " + lastCol + lastRow + "\nValue: " + t.getTableColumn().getCellData(lastRow));//show position and value of that cell
                    if (change.getList().size() > 1)
                    {
                        cellSelected.setText("Cells Selected: " + startCol + startRow + ":" + lastCol + lastRow);//show position and value of that cell
                    }
                }

                //for any rows selected, higlight corresponding index header
                for (Row r : rowz)
                {
                    r.setSelected(true);
                    colorIndexCell();//reformat index column if needed
                }
            }
        });

        //listener for detecting cell selection cancel
        rowz.addListener(new ListChangeListener<Row>()
        {
            @Override
            public void onChanged(Change<? extends Row> change)
            {
                while (change.next())
                {
                    for (Row r : change.getRemoved())
                    {
                        r.setSelected(false);
                    }
                }
            }
        });

        //when hovering over table make sure listview scrollbar is synced with tableview scroll
        table.hoverProperty().addListener((obs, oldScene, newScene) ->
        {
            if (newScene != null)
            {
                s1 = (ScrollBar) indexList.lookup(".scroll-bar:vertical");
                verti = (ScrollBar) table.lookup(".scroll-bar:vertical");
                
                //find horizontal scroll bar
                Iterator barIter = table.lookupAll(".scroll-bar:horizontal").iterator();
                barIter.next();
                horiz = (ScrollBar) barIter.next();//get horizontal scroll bar

                s1.valueProperty().bindBidirectional(verti.valueProperty());
            }
        });
        
        //cell text box Selection listener
        enterCellButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event)
            {
                String getText = cellText.getText().toUpperCase();
                try
                {
                    if(!getText.equals("ALL"))
                    {
                        selectCells(getText);
                    }
                }
                catch(NumberFormatException e)
                {
                    cellText.setText("Invalid cells");
                    System.out.println("Error: " + e.getMessage());
                }    
            }       
        });
        //cell text box Selection listener
        cellText.setOnKeyReleased(new EventHandler<KeyEvent>() 
        {
            @Override
            public void handle(KeyEvent t)
            {
                try
                {
                    String getText = cellText.getText().toUpperCase();

                    //System.out.println(getText);
                    if(getText.equals("ALL"))
                    {
                        table.getSelectionModel().selectAll();
                    }
                    else if(t.getCode().equals(t.getCode().ENTER))
                    {
                        selectCells(getText);
                    }   
                }
                catch(NumberFormatException e)
                {
                    cellText.setText("Invalid cells");
                    System.out.println("Error: " + e.getMessage());
                }
            }
        });
    }

    private void setupSideMenuStyle()
    {
        this.bPane.getStyleClass().add("bPane");//style side menu
        this.filePane.setStyle("-fx-background-color: #E97425;");
        this.editPane.setStyle("-fx-background-color: #E97425;");
        this.cellSelected.getStyleClass().add("myCellLabel");
        this.fileTitle.getStyleClass().add("myTextStyle");
        this.editTitle.getStyleClass().add("myTextStyle");
        this.toolsTitle.getStyleClass().add("myTextStyle");
    }
    
    private void setupTooltips()
    {
        //buttons
        this.saveButton.setTooltip(new Tooltip("Save\nCTRL + S"));
        this.saveAsButton.setTooltip(new Tooltip("Save As"));
        this.openButton.setTooltip(new Tooltip("Open\nCTRL + O"));
        this.newButton.setTooltip(new Tooltip("New\nCTRL + N"));
        this.printButton.setTooltip(new Tooltip("Print\nCTRL + P"));
        this.exportButton.setTooltip(new Tooltip("Export\nCTRL + E"));
        this.undoButton.setTooltip(new Tooltip("Undo\nCTRL + Z"));
        this.redoButton.setTooltip(new Tooltip("Redo\nCTRL + Y"));
        this.copyButton.setTooltip(new Tooltip("Copy\nCTRL + C"));
        this.cutButton.setTooltip(new Tooltip("Cut\nCTRL + X"));
        this.pasteButton.setTooltip(new Tooltip("Paste\nCTRL + V"));
        this.feedbackButton.setTooltip(new Tooltip("Feedback\n-We'd like to hear from you!"));
        this.helpButton.setTooltip(new Tooltip("Help\n-How to use"));
        this.settingsButton.setTooltip(new Tooltip("Settings\n-Set how JSheets does some things"));
        
        //text field
        cellText.setTooltip(new Tooltip("Enter to select cell positions\n" + 
                                        "For single selection type a letter followed by number eg. A1\n" + 
                                        "For range selection follow this format A1:A20 and replace letters and numbers to suit you\n" +
                                        "Press enter when selection is ready."));
    }

    /*
     *highlights index header cell when cell is selected
     */
    private void colorIndexCell()
    {
        indexList.setCellFactory((ListView<Row> param) -> new ListCell<Row>()
        {
            @Override
            protected void updateItem(Row item, boolean empty)
            {
                super.updateItem(item, empty);

                if (!empty)
                {
                    //if not index not selected use default style
                    if (!item.isSelected())
                    {
                        setText(item.getIndex());
                        getStyleClass().clear();
                        setStyle(null);
                        getStyleClass().add("list-cell");
                    }
                    else if (item.isSelected())//otherwise if selected highlight index
                    {
                        setText(item.getIndex());
                        setStyle("-fx-control-inner-background: gold;"
                                + "-fx-border-width: 1 ;"
                                + "-fx-alignment: center;"
                                + "-fx-cell-size: 34px;"
                                + "-fx-border-color: whitesmoke;");
                    }
                }

            }
        });
    }
    
    /*
        Setup up each column to listen for cell value change
    */
    private void setupColForEdit(TableColumn<Row, String> editCol)
    {
        editCol.setOnEditCommit(cellEditHandler -> {
            Row changeRow = (Row) table.getItems().get(cellEditHandler.getTablePosition().getRow());
            char colIdx = cellEditHandler.getTableColumn().getId().charAt(0);
            String newVal = cellEditHandler.getNewValue();   
            switch(colIdx)
            {
                case 'A':
                    changeRow.setA(newVal);
                    break;
                case 'B':
                    changeRow.setB(newVal);
                    break;
                case 'C':
                    changeRow.setC(newVal);
                    break;
                case 'D':
                    changeRow.setD(newVal);
                    break;
                case 'E':
                    changeRow.setE(newVal);
                    break;
                case 'F':
                    changeRow.setF(newVal);
                    break;
                case 'G':
                    changeRow.setG(newVal);
                    break;
                case 'H':
                    changeRow.setH(newVal);
                    break;
                case 'I':
                    changeRow.setI(newVal);
                    break;    
                case 'J':
                    changeRow.setJ(newVal);
                    break;    
                case 'K':
                    changeRow.setK(newVal);
                    break; 
            }
        });
    }
    
    /*
        handling opening of other external windows, feedback settings help/tutorial
    */
    private void openWindow(String url, String style, String title, boolean isResizable)
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource(url));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            
            scene.getStylesheets().add(style);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(isResizable);
            stage.show();
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    /*
        prompt user to save before opening another file
    */
    private Optional<ButtonType> changeFilePrompt(String prompt)
    {
        //alert user if they want to save current sheet before creating a new one
        ButtonType yesSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType noSave = new ButtonType("Don't Save", ButtonBar.ButtonData.NO);
        ButtonType noClose = new ButtonType("Stay Here", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert a = new Alert(AlertType.CONFIRMATION, prompt, 
                            yesSave, noSave, noClose); 
        a.getDialogPane().setPrefSize(500, 300);

        return a.showAndWait();
    }
    
    /*
        function to create a pie chart
    */
    private void createPie()
    {
        try
        {
            PieChartWindow thePie = new PieChartWindow();
            for(int i = 0; i < (this.selectedCells.size()/2); i++)
            {
                TablePosition tp = this.selectedCells.get(i);
                String key = tp.getTableColumn().getCellData(tp.getRow()).toString();
                double value = Double.valueOf(tp.getTableColumn().getCellData(tp.getRow() + 1).toString());
                thePie.addSlice(key, value);
    //            System.out.println(key + value);
            }
        
            thePie.startWindow();
        }
        catch(NumberFormatException ne)
        {
            Alert numErr = new Alert(AlertType.WARNING, "Error: a value in second selected row not a number");
            numErr.showAndWait();
        }
        catch(IndexOutOfBoundsException e)
        {
            Alert err = new Alert(AlertType.WARNING, "No cells selected!");
            err.showAndWait();
        }
    }
    
    private void sumCalcWindow()
    {
        if(!this.selectedCells.isEmpty())
        {
            TextInputDialog sumWindow = new TextInputDialog();
            sumWindow.setHeaderText("Enter cell to put sum result");
            Optional<String> res = sumWindow.showAndWait();
            if(res.get().length() > 1 && res != null)
            {
                try
                {
                    int rowNum = Integer.valueOf(res.get().substring(1));
                    String colId = String.valueOf(res.get().charAt(0));

                    if(colId.charAt(0) >= 'A' && colId.charAt(0) <= 'K' && rowNum <= 30 && !this.selectedCells.isEmpty())
                    {
                        try
                        {
                            int result = 0;
                            for(TablePosition tp : this.selectedCells)
                            {
                                result += Integer.valueOf((String) tp.getTableColumn().getCellData(tp.getRow()));
                            }
                            table.getSelectionModel().clearAndSelect(rowNum, this.cols.get(colId));
                            table.getSelectionModel().getSelectedItem().setByChar(colId.charAt(0), String.valueOf(result));
                            table.getItems().set(rowNum, table.getSelectionModel().getSelectedItem());
                            table.getSelectionModel().clearAndSelect(rowNum, this.cols.get(colId));
                        }
                        catch(NumberFormatException e)
                        {
                            Alert sumAlert = new Alert(AlertType.WARNING, "Error invalid numeric value: " + e.getMessage());
                            sumAlert.showAndWait();
                        }
                    }
                }     
                catch(NumberFormatException e)
                {
                    Alert sumAlert = new Alert(AlertType.WARNING, "Error cell row must be a number: " + e.getMessage());
                    sumAlert.showAndWait();
                }
            }
        }
        else
        {
            Alert sumAlert = new Alert(AlertType.WARNING, "Please select cells!");
            sumAlert.showAndWait();
        }
    }
    
    //used to change title of window, just import a name of the file
    private void setTitleOfScreen(String name)
    {
        Stage mainStage = (Stage) anchor.getScene().getWindow();
        mainStage.setTitle("Your current sheet = [ " + name + " ] " + " - JSheets");
    }
    
    /*
        Read file into spreadsheet
    */
    private void readFile(File file)
    {
        BufferedReader reader;
        String lineRead;
        String[] lineArr;
        int count = 0;
        try
        {
            reader = new BufferedReader(new FileReader(file));
            table.getItems().clear();
            ObservableList<Row> list = FXCollections.observableArrayList(this.resetRows());
            table.setItems(list);
            
            this.setTitleOfScreen(file.getName());
            this.currFile = file;
            
            lineRead = reader.readLine();
            while(lineRead != null && count < 31)
            {
                lineArr = lineRead.split(",", -1);
                
                if(lineArr.length > 0)
                {
                    Row r = new Row(String.valueOf(count));//get current line and store in row format
                    for(int i = 0; i < lineArr.length; i++)
                    {
                        if(i < 11)
                            r.setByIndex(i, lineArr[i]);                      
                    }
                    table.getItems().set(count, r);//set row to new row
                }
                lineRead = reader.readLine();
                count++;
            }
            reader.close();
        }
        catch(IOException e)
        {
            System.out.println("error reading file" + e.getMessage());
        }
    }
    //write to file
    private void writeToFile(File file, String ext)
    {
        PrintWriter writer;
        try
        {
            String output = "";
            if(file.getAbsolutePath().contains(".csv"))
            {
                writer = new PrintWriter(file);
            }
            else
            {
                writer = new PrintWriter(file.getAbsolutePath() + "." + ext);
            }
            
            this.setTitleOfScreen(file.getName());
            this.currFile = file;
            
            for(Row row : table.getItems())
            {
                output += row.csvString() + "\n";
            }
            writer.println(output);
            writer.close();
            Alert success = new Alert(AlertType.INFORMATION, "Save to " + file.getName() + " Successful");
            success.showAndWait();
        }
        catch(IOException e)
        {
            System.out.println("error with saving file");
        }
    }
    
    private void selectCells(String getText)
    {
        if(getText.length() >= 2 && !getText.contains(":"))
        {
            int rowNum = Integer.valueOf(getText.substring(1));
            String colId = String.valueOf(getText.charAt(0));

            if(rowNum >= 0 && cols.containsKey(colId) && rowNum <= indexList.getItems().size())
            {
                table.getSelectionModel().clearSelection();
                //System.out.println("Hey I'm a cell " + getText.length());
                table.getSelectionModel().select(rowNum, cols.get(colId));
            }
            else
            {
                throw new NumberFormatException("invalid row index: " + rowNum);
            }
        }
        else if(getText.length() >= 5 && getText.contains(":"))
        {
            System.out.println("Hey I'm a bunch of cells");            
            String startCol, endCol;
            int startRow, endRow;
            String[] strArr = getText.split(":");

            startCol = strArr[0].substring(0,1);
            endCol = strArr[1].substring(0,1);
            startRow = Integer.valueOf(strArr[0].substring(1));
            endRow = Integer.valueOf(strArr[1].substring(1));

            if(cols.containsKey(startCol) && cols.containsKey(endCol))
            {
                if(startRow <= indexList.getItems().size() && 
                        endRow <= indexList.getItems().size() && startRow >= 0 && endRow >= 0)
                {
                    table.getSelectionModel().clearSelection();
                    table.getSelectionModel().selectRange(startRow, cols.get(startCol), endRow, cols.get(endCol));
                }
                else
                {
                    throw new NumberFormatException("invalid bunch row index");
                }                      
            }
        }
    }
    
    /*
        Create empty rows when creating a new sheet or loading another
    */
    private List<Row> resetRows()
    {
        List<Row> result = new ArrayList<>();
        for (int i = 0; i <= 30; i++)
        {
            Row r = new Row(String.valueOf(i));
            result.add(r);
        }
        
        return result;
    }

    /*
        Reference: https://community.oracle.com/message/11334815#11334815
        This decorator class allows for mouse drag selection and input of cells
        Constructor takes in an existing cellFactory and decorates it for cell drag
        Solution from oracle community
     */
    private class DragSelectionCellFactory implements Callback<TableColumn<Row, String>, TableCell<Row, String>>
    {
        private final Callback<TableColumn<Row, String>, TableCell<Row, String>> factory ;

        public DragSelectionCellFactory(Callback<TableColumn<Row,String>, TableCell<Row, String>> factory) {
            this.factory = factory ;
        }
    
        @Override
        public TableCell<Row, String> call(TableColumn<Row, String> col)
        {
            TableCell<Row, String> cell = this.factory.call(col);
            
            //listeners for dragging for cell selection
            cell.setOnDragDetected(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    cell.startFullDrag();
                    cell.getTableColumn().getTableView().getSelectionModel().select(cell.getIndex(), cell.getTableColumn());
                }
            });
            cell.setOnMouseDragEntered(new EventHandler<MouseDragEvent>()
            {

                @Override
                public void handle(MouseDragEvent event)
                {
                    cell.getTableColumn().getTableView().getSelectionModel().select(cell.getIndex(), cell.getTableColumn()); 
                    
                }

            });

            //scroll slightly after each cell selection
            cell.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t)
                {
                    //change in direction of mouse movement
                    double xChange = t.getSceneX() - (horiz.getMax()- 50);
                    double yChange = t.getSceneY() - (verti.getHeight() - 200.0);//take 200 allow scroll before reach edge
                    
                    //vertical scroll down
                    if(verti.getValue() <= verti.getHeight() && yChange > 0.0)
                    {
                        verti.setValue(verti.getValue() + 0.005);
                    }
                    //scroll up
                    else if(verti.getValue() >= verti.getMinHeight() && yChange < 0.0)
                    {
                        verti.setValue(verti.getValue() - 0.005);
                    }
                    //horizontal scroll to right.
                    if(horiz.getValue() <= horiz.getMax() && xChange > 0.0)
                    {
                        horiz.setValue(horiz.getValue() + 0.75);
                    }
                    //horizontal scroll to left
                    else if(horiz.getValue() >= horiz.getMinWidth() && xChange < 0.0)
                    {
                        horiz.setValue(horiz.getValue() - 0.75);
                    }
                    
                    System.out.println(t.getSceneX() + " - " + horiz.getWidth() + " = " + xChange + "x.getVal " + horiz.getValue());                   
                }
            });

            cell.setTooltip(new Tooltip("\nTip.\n double click to begin editing cell\n Press enter to set changes"));
            return cell;
        }
    }

}
