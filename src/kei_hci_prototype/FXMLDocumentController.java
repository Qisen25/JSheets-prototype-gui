/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kei_hci_prototype;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
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
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
    private AnchorPane anchor, spreadPane;
    @FXML
    private TextField cellText;
    @FXML
    private TableView table;
    @FXML
    private Pane bPane, filePane, editPane;
    @FXML
    private ListView<Row> indexList;
    @FXML
    private Label fileTitle, editTitle, cellSelected, toolsTitle;
    @FXML
    private Button saveButton, openButton, newButton, exportButton, printButton,
            undoButton, redoButton;

    private Map<String, TableColumn<Row, String>> cols;
    private TableColumn prevCol = null;
    private Row prevRow = null;
    private ObservableList<Row> rowz;
    private  ScrollBar s1, verti, horiz;
    private double sc = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        setupSideMenuStyle();
        setupButtonTooltip();

        cols = new HashMap<>();
//        TableColumn<Row, String> index = new TableColumn<>("index");
//        index.setId("index");
//        index.setSortable(false);
//        PropertyValueFactory<Row, String> prop = new PropertyValueFactory<>("index");
//        index.setCellValueFactory(prop);
//        table.getColumns().add(index);
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
        ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
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
                    cellSelected.setText("Cells Selected: " + lastCol + lastRow);
                    if (change.getList().size() > 1)
                    {
                        cellSelected.setText("Cells Selected: " + startCol + startRow + ":" + lastCol + lastRow);
                    }
                }

                //for any rows selected higlight corresponding index header
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
                horiz = (ScrollBar) barIter.next();
                //System.out.println(horiz.orientationProperty());
                
//                for(Node n : table.lookupAll(".scroll-bar:horizontal"))
//                {
//                    ScrollBar h = (ScrollBar) n;
//                    System.out.println(h.orientationProperty());
//                }

                s1.valueProperty().bindBidirectional(verti.valueProperty());
            }
        });
        
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
                    else if(getText.length() >= 2 && !getText.contains(":") && t.getCode().equals(t.getCode().ENTER))
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
                    else if(getText.length() >= 5 && getText.contains(":") && t.getCode().equals(t.getCode().ENTER))
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
    
    private void setupButtonTooltip()
    {
        this.saveButton.setTooltip(new Tooltip("Save\nCTRL + S"));
    }

    /*
     *highlights index cell when cell is selected
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
                        verti.setValue(verti.getValue() + 0.01);
                    }
                    //scroll up
                    else if(verti.getValue() >= verti.getMinHeight() && yChange < 0.0)
                    {
                        verti.setValue(verti.getValue() - 0.01);
                    }
                    //horizontal scroll to right.
                    if(horiz.getValue() <= horiz.getMax() && xChange > 0.0)
                    {
                        horiz.setValue(horiz.getValue() + 0.95);
                    }
                    //horizontal scroll to left
                    else if(horiz.getValue() >= horiz.getMinWidth() && xChange < 0.0)
                    {
                        horiz.setValue(horiz.getValue() - 0.95);
                    }
                    
                    System.out.println(t.getSceneX() + " - " + horiz.getWidth() + " = " + xChange + "x.getVal " + horiz.getValue());                   
                }
            });
            
            return cell;
        }
    }

}
