/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kei_hci_prototype;

import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author student
 */
public class Row {
    
    private String index;
//    private SimpleStringProperty A = new SimpleStringProperty("poop ");
//    private SimpleStringProperty B = new SimpleStringProperty("poop ");
//    private SimpleStringProperty C = new SimpleStringProperty("poop ");
//    private SimpleStringProperty D = new SimpleStringProperty("poop ");
    
    //these are not useable please refer to cols array
    //just to trick PropertyValueFactory to allow values to be observed
    private SimpleStringProperty A;
    private SimpleStringProperty B;
    private SimpleStringProperty C;
    private SimpleStringProperty D;
    
    private SimpleStringProperty[] cols = {this.A, this.B, this.C, this.D};

    public Row(String index) {
        this.index = index;
        
        for(int i = 0; i < cols.length; i++)
        {
            cols[i] = new SimpleStringProperty("Empty");
        }
    }

    public String getIndex() {
        return index;
    }

    public String getA() {
        return cols[0].get();
    }

    public String getB() {
        return cols[1].get();
    }

    public String getC() {
        return cols[2].get();
    }

    public String getD() {
        return cols[3].get();
    }

    public void setA(String A) {
        this.cols[0].set(A);
    }

    public void setB(String B) {
        this.cols[1].set(B);
    }

    public void setC(String C) {
        this.cols[2].set(C);
    }

    public void setD(String D) {
        this.cols[3].set(D);
    }
  
}
