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
    private SimpleStringProperty E;
    private SimpleStringProperty F;
    private SimpleStringProperty G;
    private SimpleStringProperty H;
    private SimpleStringProperty I;
    private SimpleStringProperty J;
    private SimpleStringProperty K;
    
    private SimpleStringProperty[] cols = {this.A, this.B, this.C, this.D,
                                            this.E, this.F, this.G, this.H,
                                            this.I, this.J, this.K};
    
    private boolean selected = false;

    public Row(String index) {
        this.index = index;
        
        for(int i = 0; i < cols.length; i++)
        {
            cols[i] = new SimpleStringProperty("Empty");
        }
    }

    //getters
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

    public String getE()
    {
        return cols[4].get();
    }

    public String getF()
    {
        return cols[5].get();
    }

    public String getG()
    {
        return cols[6].get();
    }

    public String getH()
    {
        return cols[7].get();
    }

    public String getI()
    {
        return cols[8].get();
    }

    public String getJ()
    {
        return cols[9].get();
    }

    public String getK()
    {
        return cols[10].get();
    }
    

    //setters
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

    public void setE(String E)
    {
        this.cols[4].set(E);
    }

    public void setF(String F)
    {
        this.cols[5].set(F);
    }

    public void setG(String G)
    {
        this.cols[6].set(G);
    }

    public void setH(String H)
    {
        this.cols[7].set(H);
    }

    public void setI(String I)
    {
        this.cols[8].set(I);
    }

    public void setJ(String J)
    {
        this.cols[9].set(J);
    }

    public void setK(String K)
    {
        this.cols[10].set(K);
    }
    
    //row return index
    public String toString()
    {
        return index;
    }

    //row selection
    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean isSelected)
    {
        this.selected = isSelected;
    }
    
}
