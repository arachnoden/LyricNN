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
    
    public NeuralNetwork getNN(int sens, int[] networkMap){
        if(nn==null)nn = new NeuralNetwork(sens, networkMap);
        return nn;
    }
    public NeuralNetwork getNN(){
        return nn;
    }
    public ArrayList<String> getSortWords(){
        if(sortedWords==null)sortedWords = new ArrayList<>();
        return sortedWords;
    }
}
