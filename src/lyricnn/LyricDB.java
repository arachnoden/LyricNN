/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lyricnn;

import java.io.Serializable;
import java.util.ArrayList;
import neuralnetwork.NeuralNetwork;

/**
 *
 * @author Den
 */
public class LyricDB implements Serializable{
    private NeuralNetwork nn;
    private ArrayList<String> sortedWords;
    
    public void setNN(NeuralNetwork n){
        nn = n;
    }
    
    public void createNN(int sens, int[] networkMap){
        nn = new NeuralNetwork(sens, networkMap, true);
    }
    public NeuralNetwork getNN(){
        return nn;
    }
    public ArrayList<String> getSortWords(){
        if(sortedWords==null)sortedWords = new ArrayList<>();
        return sortedWords;
    }
}
