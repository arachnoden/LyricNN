/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lyricnn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import neuralnetwork.NeuralNetwork;

/**
 *
 * @author Den
 */
public class LyricNN extends Application {
    private static List<String> words;
    private static NeuralNetwork nn1;
    private static ComboBox<String>[] cbMass;
    private static Label lblNom[];
    private static Label lblWords[];
    @Override
    public void start(Stage primaryStage) {
        
        VBox root = new VBox(5);
        HBox vhidDann = new HBox(5);
        root.getChildren().add(vhidDann);
        
        TextField tf[] = new TextField[10];
        for (TextField textField : tf) {
            textField = stvorTFdlCyfry(35);
            textField.setText("0");
            vhidDann.getChildren().add(textField);
        }
        EventHandler<ActionEvent> podij = (ActionEvent aEv) -> {
            updateNumbers();
        };
        
        words = Arrays.asList(loadWordsFromFile("E:\\Users\\Den\\Desktop\\jar\\Words.txt", 262));
        HBox hbSlova = new HBox(5);
        HBox hbNomery = new HBox(5);
        
        
        lblNom = new Label[10];
        for (int i = 0; i < 10; i++) {
            lblNom[i] = new Label();
            hbNomery.getChildren().add(lblNom[i]);
        }
        
        HBox hbSlovaRez = new HBox(5);
        lblWords = new Label[10];
        for (int i = 0; i < 10; i++) {
            lblWords[i] = new Label("Slovo");
            hbSlovaRez.getChildren().add(lblWords[i]);
        }
        
        cbMass = new ComboBox[10];
        
        for (int i = 0; i < 10; i++) {
            cbMass[i] = new ComboBox<>();
            cbMass[i].getItems().addAll(words);
            cbMass[i].getSelectionModel().select(0);
            cbMass[i].setOnAction(podij);
            hbSlova.getChildren().add(cbMass[i]);
        }
        
        //hbSlova.getChildren().add(cbWords);
        
        Label lbl = new Label("Total Error");
        
        //nn1.setLabel(lbl);
        nn1 = new NeuralNetwork(262, new int[]{262,262/*,262,262*/,262});
        
        Button btn = new Button("Train");
        btn.setOnAction(act -> {
            trainNetwork(nn1,lbl);
        });
        
        root.getChildren().addAll(lbl,btn,hbSlova,hbNomery,hbSlovaRez);
        Scene scene = new Scene(root, 1000, 250);
        
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
        //trainNetwork(nn1);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    /**
     * Створює текстове поле для цифр
     * @param width ширина поля
     * @return текстове поле
     */
    public static TextField stvorTFdlCyfry(int width){
        TextField tfK2 = new TextField();
        tfK2.setPrefWidth(width);
        //перевіряє чи вводяться саме цифри
        tfK2.setOnKeyReleased(pdj ->{
            if(!tfK2.getText().matches("\\d+")){
                tfK2.setText("0");
                tfK2.selectAll();
            }
        });
        return tfK2;
    }

    /**
     * Тренує нейронну мережу завантажуючи приклади з текстових файлів
     * @param nn нейронна мережа яку потрібно натренувати
     * @param lbl мітка яка буде відображати хід тренування
     */
    public static void trainNetwork(NeuralNetwork nn, Label lbl){
        double taskSet[][] = loadArrayFromFile("E:\\Users\\Den\\Desktop\\jar\\Task.txt", 7, 262);
        double answerSet[][] = loadArrayFromFile("E:\\Users\\Den\\Desktop\\jar\\Answer.txt", 7, 262);
        lbl.textProperty().bind(nn.messageProperty());
        nn.setParameters(taskSet, answerSet, 0.4, 0.4);

        new Thread(nn).start();
    }
    /**
     * Завантажує завдання та відповіді з текстових файлів у массив
     * @param path шлях до файлу
     * @param rows строк
     * @param columns колонок
     * @return массив завдання/відповіді
     */
    private static double[][] loadArrayFromFile(String path,int rows, int columns) {
        double[][] arr = null;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            arr = new double[rows][columns]; // создали массив который вернем из метода

            for (int i = 0; i < rows; i++) {
                String st[] = br.readLine().split(",");
                for (int j = 0; j < columns; j++) {
                    arr[i][j]=Integer.parseInt(st[j]);
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arr;
    }
    
    /**
     * Завантажує слова з файлу
     * @param path шлях до файлу з словами
     * @param wordCount кількість слів
     * @return массив строк
     */
    private static String[] loadWordsFromFile(String path,int wordCount){
        String[] st = new String[wordCount];
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            st = br.readLine().split(",");
        } catch (Exception e) {
            System.out.println("Not mathc count");
        }
        return st;
    }
    
    /**
     * Обновляє номери слів
     */
    public static void updateNumbers(){
        for (int i = 0; i < 10; i++) {
            lblNom[i].setText(""+cbMass[i].getSelectionModel().getSelectedIndex());
        }
        fillTextAnswer(getAnswer());
    }
    
    /**
     * Отримує відповідь нейронної мережі
     * @return 
     */
    public static int[] getAnswer(){
        double ans[] = nn1.getAnswer(); // нічого не змінювалося тому, що завдання було старим(останнє введене питаня/завдання), необхідно змінювати завдання.
        double biggest[] = new double[10];
        int biggestNumb[] = new int[10];
        for (int i = 0; i < ans.length; i++) {
            double checked = ans[i];
            int chekedNum = i;
            double buffer = 0;
            int bufferNum = 0;
            for (int j = 0; j < 10; j++) {
                if(biggest[j]>checked)continue;
                buffer = biggest[j];
                bufferNum = biggestNumb[j];
                biggest[j] = checked;
                biggestNumb[j] = i;
                checked = buffer;
                chekedNum = bufferNum;
            }
        }
        return biggestNumb;
    }
    /**
     * Заповнити словами текстові мітки
     * @param numbers порядкові номера слів
     */
    public static void fillTextAnswer(int[] numbers){
        for (int i = 0; i < 10; i++) {
            lblWords[i].setText(words.get(numbers[i]));
        }
    }
}
