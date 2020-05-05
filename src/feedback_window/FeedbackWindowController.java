/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feedback_window;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author beepbeep
 */
public class FeedbackWindowController implements Initializable
{
    
    @FXML
    void closeHandler(ActionEvent event) {
        Button but = (Button) event.getSource();
        Stage stage = (Stage) but.getScene().getWindow();
        stage.close();
    }

    @FXML
    void submitHandler(ActionEvent event){
        Button but = (Button) event.getSource();
        Stage stage = (Stage) but.getScene().getWindow();

        stage.close();//close window
    }
    
    @FXML
    private Label guiXp, feelLabel;
    
    @FXML
    private ListView<Boolean> easeList, layoutList, expList, faceList;
    
    private List<Boolean> defaultStars, defaultFaces;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        guiXp.setText("Rate this GUI experience compared to command line spreadsheets");
        guiXp.setWrapText(true);
        feelLabel.setText("How do you feel when using JSheets");
        feelLabel.setWrapText(true);
        
        defaultStars = new ArrayList<>();
        defaultFaces = new ArrayList<>();
        
        setupRatings();
    }    
    
    private void setupRatings()
    {
        easeList.setOrientation(Orientation.HORIZONTAL);
        layoutList.setOrientation(Orientation.HORIZONTAL);
        expList.setOrientation(Orientation.HORIZONTAL);
        faceList.setOrientation(Orientation.HORIZONTAL);
        
        for(int i = 0; i < 5; i++)
        {
            easeList.getItems().add(false);
            layoutList.getItems().add(false);
            expList.getItems().add(false);
            
            //default false state
            defaultStars.add(Boolean.FALSE);
        }
        
        for(int i = 0; i < 3; i++)
        {
            faceList.getItems().add(false);
            
            //default false state
            defaultFaces.add(Boolean.FALSE);
        }
        
        String[] faceIcons = {"blankSad", "blankOk", "blankHappy", "fillSad", "fillOk", "fillHappy"};
        
        setupStars(easeList, "fullStar", "blankStar", defaultStars);
        setupStars(layoutList, "fullStar", "blankStar", defaultStars);
        setupStars(expList, "fullStar", "blankStar", defaultStars);
        setupFaces(faceList, faceIcons, defaultFaces);
    }
    
    private void setupStars(ListView<Boolean> inList, String fill, String blank, List<Boolean> defaultRatings)
    {
        inList.setCellFactory((ListView<Boolean> param) -> new ListCell<Boolean>()
        {
            @Override
            protected void updateItem(Boolean item, boolean empty)
            {
                super.updateItem(item, empty);

                if (!empty)
                {
                    //if selected then full star
                    if (item)
                    {
                        getStyleClass().add(fill);
                    }
                    else//otherwise blank star
                    {
                        getStyleClass().add(blank);
                        //System.out.println("Click feedback " + getIndex());
                    }
                    
                    setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event)
                        {
                            System.out.println("Click feedback ");
                            int index = inList.getSelectionModel().getSelectedIndex();

                            inList.getItems().setAll(defaultRatings);
                            
                            for(int i = 0; i <= index; i++)
                            {
                                System.out.println("Click at pos " + i);
                                inList.getItems().set(i, Boolean.TRUE);
                            }
                            setupStars(inList, fill, blank, defaultRatings);
                        }                                              
                    });
                }
            }
        });
    }  
    
    private void setupFaces(ListView<Boolean> inList, String[] faces, List<Boolean> defaultRatings)
    {
        inList.setCellFactory((ListView<Boolean> param) -> new ListCell<Boolean>()
        {
            @Override
            protected void updateItem(Boolean item, boolean empty)
            {
                super.updateItem(item, empty);

                if (!empty)
                {   
                    int x = getIndex();
                    
                    switch(x)
                    {
                        case 0:
                            setFaces(item, x);
                            break;
                            
                        case 1:
                            setFaces(item, x);
                            break;
                            
                        case 2:
                            setFaces(item, x);
                            break;
                    }
                    
                    setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event)
                        {
                            int index = inList.getSelectionModel().getSelectedIndex();

                            inList.getItems().setAll(defaultRatings);
                            
                            inList.getItems().set(index, Boolean.TRUE);
                                
                            setupFaces(inList, faces, defaultRatings);
                        }                                              
                    });
                }
            }
            
            protected void setFaces(Boolean displayFill, int index)
            {
                if (displayFill)
                {                
                    getStyleClass().add(faces[index + 3]);
                }
                else//otherwise blank star
                {
                    getStyleClass().add(faces[index]);
                }
            }
        });
    }    
}
