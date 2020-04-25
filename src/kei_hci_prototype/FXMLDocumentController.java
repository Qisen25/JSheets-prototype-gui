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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author student
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private Label cellSelected;
    @FXML
    private TextField cellText;
    @FXML
    private TableView table;
    @FXML
    private Pane bPane;
    
    private Map<String, TableColumn<Row, String>> cols;
    private TableColumn prev = null;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bPane.getStyleClass().add("bPane");
        //cellSelected.getStyleClass().add("cellSelected");
        
        cols = new HashMap<>();
        TableColumn<Row, String> index = new TableColumn<>("index");
        index.setId("index");
        index.setSortable(false);
        PropertyValueFactory<Row, String> prop = new PropertyValueFactory<>("index");
        index.setCellValueFactory(prop);
        table.getColumns().add(index);
        
        for(char i = 'A'; i <= 'D'; i++)
        {
            TableColumn<Row, String> inCol = new TableColumn<>(String.valueOf(i));
            inCol.setId(String.valueOf(i));
            cols.put(String.valueOf(i), inCol);
            PropertyValueFactory<Row, String> inProp = new PropertyValueFactory<>(String.valueOf(i));
            inCol.setCellValueFactory(inProp);
            table.getColumns().add(inCol);
//            PropertyValueFactory<Row, String> prop = new PropertyValueFactory<>("index");
        }
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setCellSelectionEnabled(true);
        
        for(int i = 1; i < 20; i++)
        {
            Row r = new Row(String.valueOf(i));
            table.getItems().add(r);
        }
        
        ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();

        selectedCells.addListener(new ListChangeListener<TablePosition>() {
          @Override
          public void onChanged(Change<? extends TablePosition> change) {
              if(!change.getList().get(0).getTableColumn().getId().equalsIgnoreCase("index"))
              {
                if(prev != null)
                    prev.getStyleClass().clear();
                
                prev = change.getList().get(0).getTableColumn();
                int row = Integer.valueOf(change.getList().get(0).getRow()) + 1;
                String col = change.getList().get(0).getTableColumn().getText();
                System.out.println(prev.getCellData(row - 1));
                //System.out.println(change.getList().get(0).getTableColumn().getStyleableParent());
                change.getList().get(0).getTableColumn().getStyleClass().add("table-row-cell");
                change.getList().get(0).getTableColumn().getStyleClass().add("headerColor");
                cellText.setText(col + row);
              }
          }
        });
    }    
    
}
