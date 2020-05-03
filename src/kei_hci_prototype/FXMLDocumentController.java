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
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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

        cols = new HashMap<>();
//        TableColumn<Row, String> index = new TableColumn<>("index");
//        index.setId("index");
//        index.setSortable(false);
//        PropertyValueFactory<Row, String> prop = new PropertyValueFactory<>("index");
//        index.setCellValueFactory(prop);
//        table.getColumns().add(index);

        //set columns headers for table
        for (char i = 'A'; i <= 'K'; i++)
        {
            TableColumn<Row, String> inCol = new TableColumn<>(String.valueOf(i));
            inCol.setId(String.valueOf(i)); 
            PropertyValueFactory<Row, String> inProp = new PropertyValueFactory<>(String.valueOf(i));
            Callback<TableColumn<Row, String>, TableCell<Row, String>> cellFactory = new DragSelectionCellFactory();
            inCol.setCellFactory(cellFactory);
            inCol.setCellValueFactory(inProp);
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

                    prevCol.getStyleClass().add("table-row-cell");//highlight actual cell selected
                    prevCol.getStyleClass().add("headerColor");//highlight column header when select
                    cellText.setText(lastCol + lastRow);
                    if (change.getList().size() > 1)
                    {
                        cellText.setText(startCol + startRow + ":" + lastCol + lastRow);
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
                String getText = cellText.getText();

                System.out.println(getText);
                if(getText.length() >= 2 && !getText.contains(":") && t.getCode().equals(t.getCode().ENTER))
                {
                    int rowNum = Integer.valueOf(getText.substring(1));
                    String colId = String.valueOf(getText.charAt(0));

                    if(rowNum < 0)
                        rowNum = 0;

                    table.getSelectionModel().clearSelection();
                    System.out.println("Hey I'm a cell " + getText.length());
                    table.getSelectionModel().select(rowNum, cols.get(colId));
                }
                else if(getText.length() == 4)
                {
                    System.out.println("Hey I'm a bunch of cells");
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
                                + "-fx-cell-size: 26;"
                                + "-fx-border-color: whitesmoke;");
                    }
                }

            }
        });
    }

    /*
        Reference: https://community.oracle.com/message/11334815#11334815
        This class allows for mouse drag selection of cells
        Solution from oracle community
     */
    private class DragSelectionCell extends TableCell<Row, String>
    {
        public DragSelectionCell()
        {
            //listeners for dragging for cell selection
            setOnDragDetected(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    startFullDrag();
                    getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());
                }
            });
            setOnMouseDragEntered(new EventHandler<MouseDragEvent>()
            {

                @Override
                public void handle(MouseDragEvent event)
                {
                    getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn()); 
                    
                }

            });

            //scroll slightly after each cell selection
            setOnMouseDragged(new EventHandler<MouseEvent>() {
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

        }
        
        @Override
        public void updateItem(String item, boolean empty)
        {
            super.updateItem(item, empty);
            if (empty)
            {
                setText(null);
            }
            else
            {
                setText(item);
            }
        }
    }

    private class DragSelectionCellFactory implements Callback<TableColumn<Row, String>, TableCell<Row, String>>
    {
        @Override
        public TableCell<Row, String> call(final TableColumn<Row, String> col)
        {
            return new DragSelectionCell();
        }
    }

}
