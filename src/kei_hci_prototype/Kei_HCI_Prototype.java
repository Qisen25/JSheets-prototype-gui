/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kei_hci_prototype;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Kei Wang 19126089
 */
public class Kei_HCI_Prototype extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/CSS/main/tablecss.css");
        stage.setScene(scene);
        stage.setTitle("JSheets");
        stage.show();
        
        Parent helpRoot = FXMLLoader.load(getClass().getResource("/help_window/HelpWindow.fxml"));
        Stage tutorial = new Stage();
        Scene otherScene = new Scene(helpRoot);
        otherScene.getStylesheets().add("/CSS/help/helpStyle.css");
        tutorial.setScene(otherScene);
        tutorial.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
