/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kei_hci_prototype;

import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author Kei Wang 19126089
 */
public class PieChartWindow
{
    private PieChart pie;
    
    public PieChartWindow()
    {
        pie = new PieChart();
    }
    
    public void addSlice(String key, double value)
    {
        pie.getData().add(new PieChart.Data(key, value));
    }
    
    public void startWindow()
    {
        Stage stage = new Stage();
        StackPane pane = new StackPane(pie);
        Scene sc = new Scene(pane, 400, 400);
        stage.setScene(sc);
        stage.show();
    }
}
