/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kei_hci_prototype;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

/*
 * Class handling creation of spreadsheet views
 * REFERENCES: stackoverflow and javafx docs use to help set up listeners and views
 *             for drag cell selection https://community.oracle.com/message/11334815#11334815
 *@author   Kei Wang 19126089
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private AnchorPane anchor;
    @FXML
    private TextField cellText;
    @FXML
    private TableView table;
    @FXML
    private Pane bPane, spreadPane, filePane, editPane;
    @FXML
    private ListView<Row> indexList;
    @FXML
    private Label fileTitle, editTitle, cellSelected, toolsTitle;
    @FXML
    private Button saveButton, openButton, newButton, exportButton, printButton, 
            undoButton, redoButton;
    
    private Map<String, TableColumn<Row, String>> cols;
    private TableColumn prev = null;
    private Row prevRow = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupSideMenuStyle();
        
        cols = new HashMap<>();
//        TableColumn<Row, String> index = new TableColumn<>("index");
//        index.setId("index");
//        index.setSortable(false);
//        PropertyValueFactory<Row, String> prop = new PropertyValueFactory<>("index");
//        index.setCellValueFactory(prop);
//        table.getColumns().add(index);
        
        //set columns headers for table
        for(char i = 'A'; i <= 'K'; i++)
        {
            TableColumn<Row, String> inCol = new TableColumn<>(String.valueOf(i));
            inCol.setId(String.valueOf(i));
            cols.put(String.valueOf(i), inCol);//put in a set? maybe remove this soon
            PropertyValueFactory<Row, String> inProp = new PropertyValueFactory<>(String.valueOf(i));
            Callback<TableColumn<Row, String>, TableCell<Row, String>> cellFactory = new DragSelectionCellFactory();
            inCol.setCellFactory(cellFactory);
            inCol.setCellValueFactory(inProp);
            table.getColumns().add(inCol);//add columns to table
            inCol.setMinWidth(100.0);//set cell size to atleast 100
        }
        //table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        //initialise index and cells
        //indexList.getItems().add(" ");
        for(int i = 1; i <= 30; i++)
        {
            Row r = new Row(String.valueOf(i));
            table.getItems().add(r);
            indexList.getItems().add(r);//String.valueOf(i));
        }
        
        ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
        
        //table.setOnMouseEntered(eh -> {
            table.getSelectionModel().setCellSelectionEnabled(true);//use cell selection model
        //});
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        

        //listen for change in cell selection
        selectedCells.addListener(new ListChangeListener<TablePosition>() {
          @Override
          public void onChanged(Change<? extends TablePosition> change) {
              if(!change.getList().get(0).getTableColumn().getId().equalsIgnoreCase("index"))
              {
                //clear previous selected column header style
                if(prev != null)
                    prev.getStyleClass().clear();
                
                //set previous selected row to not selected
                if(prevRow != null)
                    prevRow.setSelected(false);
//                
                prev = change.getList().get(0).getTableColumn();
                int row = Integer.valueOf(change.getList().get(0).getRow()) + 1;
                String col = change.getList().get(0).getTableColumn().getText();
                System.out.println(prev.getCellData(row - 1));

                change.getList().get(0).getTableColumn().getStyleClass().add("table-row-cell");//highlight actual cell selected
                change.getList().get(0).getTableColumn().getStyleClass().add("headerColor");//highlight column header when select
                cellText.setText(col + row);
                
                prevRow = (Row) table.getSelectionModel().getSelectedItem();
                prevRow.setSelected(true);
                colorIndexCell();//reformat index column if needed
                
              }
          }
        });
        
        //when hovering over table make sure listview scrollbar is synced with tableview scroll
        table.hoverProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                // not showing...
            } else {
                ScrollBar s1 = (ScrollBar) indexList.lookup(".scroll-bar");
                ScrollBar s2 = (ScrollBar) table.lookup(".scroll-bar");
                
                s1.valueProperty().bindBidirectional(s2.valueProperty());
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
    
    /*
     *highlights index cell when cell is selected
     */
    private void colorIndexCell()
    {
        indexList.setCellFactory((ListView<Row> param) -> new ListCell<Row>() {
            @Override
            protected void updateItem(Row item, boolean empty) {
                super.updateItem(item, empty);
                
                if(!empty)
                {
                    //if not index not selected use default style
                    if(!item.isSelected())
                    {
                        setText(item.getIndex());
                        getStyleClass().clear();
                        setStyle(null);
                        getStyleClass().add("list-cell");  
                        //System.out.println("Not True");
                    }
                    else if(item.isSelected())//otherwise if selected highlight index
                    {
                        setText(item.getIndex());
                        setStyle("-fx-control-inner-background: gold;");
                    }
                }
                
            }
        });
    }
    
    /*
    Reference: https://community.oracle.com/message/11334815#11334815
    */
    public class DragSelectionCell extends TableCell<Row, String> {  
        
        public DragSelectionCell() {  
            setOnDragDetected(new EventHandler<MouseEvent>() {  
                @Override  
                public void handle(MouseEvent event) {  
                    
                    getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());
                    startFullDrag();  
                }  
            });  
            setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {  
  
                @Override  
                public void handle(MouseDragEvent event) {  
                    getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());  
                }  
                
            });  

            setOnMouseDragExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());  
                }
            
            });
        }  
        @Override  
        public void updateItem(String item, boolean empty) {  
            super.updateItem(item, empty);  
            if (empty) {  
                setText(null);  
            } else {  
                setText(item);  
            }  
        }  
        
    }  
    
    public class DragSelectionCellFactory implements Callback<TableColumn<Row, String>, TableCell<Row, String>> {  
  
        @Override  
        public TableCell<Row, String> call(final TableColumn<Row, String> col) {            
            return new DragSelectionCell();  
        }  
        
    }
    
}
