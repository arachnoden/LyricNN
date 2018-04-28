/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lyricnn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
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
    private static Button btnWords[];
    TextArea tAr;
    String pathWords;
    @Override
    public void start(Stage primaryStage) {
        HBox hbSlova = new HBox(5);
        HBox hbNomery = new HBox(5);
        cbMass = new ComboBox[10];
        EventHandler<ActionEvent> podij = (ActionEvent aEv) -> {
            updateNumbers();
        };
        
        TabPane tp = new TabPane();
        Tab wordSelect = new Tab("Відбір слів");
            wordSelect.setClosable(false);

            VBox root1 = new VBox(5);

            Button knpZavant = new Button("Загрузить слова");
            Label lblKnpCount = new Label("Количество слов");
            TextArea taSlova = new TextArea();
            taSlova.setWrapText(true);
            //Label testLbl = new Label("whatWord");
            knpZavant.setOnAction(pdj ->{
                //testLbl.setText(taSlova.getText().replaceAll("\n", ","));
                FileChooser fch = new FileChooser();
                fch.setInitialDirectory(new File("C:/"));
                fch.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Текст", "*.txt"),
                        new FileChooser.ExtensionFilter("Всі файли", "*.*"));
                File fl = fch.showOpenDialog(primaryStage);
                if(fl==null)return;
                System.out.println(""+fl.getAbsolutePath());
                words = Arrays.asList(loadWordsFromFile(fl.getAbsolutePath()));
                for (int i = 0; i < 10; i++) {
                    cbMass[i] = new ComboBox<>();
                    cbMass[i].getItems()
                            .addAll(words);
                    cbMass[i].getSelectionModel().select(0);
                    cbMass[i].setOnAction(podij);
                    hbSlova.getChildren().add(cbMass[i]);
                }
                /*try(FileInputStream fis = new FileInputStream(fl)){
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    String txt = (NeuralNetwork)ois.readObject();
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println("Помилка завантаження файлу");
                    System.out.println(ex.getClass().getSimpleName());
                }*/
            });
            
            root1.getChildren().addAll(knpZavant,lblKnpCount,taSlova);
        
        wordSelect.setContent(root1);
        
        //----------------------------------------------------------------------------------------------
        
        VBox root2 = new VBox(5);
        HBox vhidDann = new HBox(5);
        
        
        
        lblNom = new Label[10];
        for (int i = 0; i < 10; i++) {
            lblNom[i] = new Label();
            hbNomery.getChildren().add(lblNom[i]);
        }
        
        EventHandler<ActionEvent> wordsPod = (ActionEvent aEv) -> {
            //------------------------------------------------------------------------
            Button knp = (Button)aEv.getSource();
            System.out.println(""+knp.getText());
            tAr.appendText(" "+knp.getText());
        };
        
        HBox hbSlovaRez = new HBox(5);
        btnWords = new Button[10];
        for (int i = 0; i < 10; i++) {
            btnWords[i] = new Button("Slovo");
            hbSlovaRez.getChildren().add(btnWords[i]);
            btnWords[i].setOnAction(wordsPod);
        }
        
        
        /*for (int i = 0; i < 10; i++) {
            cbMass[i] = new ComboBox<>();
            cbMass[i].getItems().addAll(words);
            cbMass[i].getSelectionModel().select(0);
            cbMass[i].setOnAction(podij);
            hbSlova.getChildren().add(cbMass[i]);
        }*/
        
        
        Label lbl = new Label("Total Error");
        
        nn1 = new NeuralNetwork(262, new int[]{262,262/*,262,262*/,262});
        
        Button btn = new Button("Train");
        btn.setOnAction(act -> {
            trainNetwork(nn1,lbl);
        });
        
        HBox hbLoadSave = new HBox(5);
        Button save = new Button("Зберегти");
        Button load = new Button("Завантажити");
        
        save.setOnAction(pdj ->{
            FileChooser fcZb = new FileChooser();
            fcZb.setInitialDirectory(new File("C:/"));
            fcZb.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Нейр. Мережа", "*.nmz"),
                    new FileChooser.ExtensionFilter("Всі файли", "*.*"));
            File fZb = fcZb.showSaveDialog(primaryStage);
            if(fZb==null)return;
            try(FileOutputStream fos = new FileOutputStream(fZb)){
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(nn1);
            } catch (IOException ex) {
                System.out.println("Помилка запису файлу");
                System.out.println(ex.getClass().getSimpleName());
            }
        });
        load.setOnAction(pdj ->{
            FileChooser fch = new FileChooser();
            fch.setInitialDirectory(new File("C:/"));
            fch.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Планчик", "*.nmz"),
                    new FileChooser.ExtensionFilter("Всі файли", "*.*"));
            File fl = fch.showOpenDialog(primaryStage);
            if(fl==null)return;
            try(FileInputStream fis = new FileInputStream(fl)){
                ObjectInputStream ois = new ObjectInputStream(fis);
                nn1 = (NeuralNetwork)ois.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Помилка завантаження файлу");
                System.out.println(ex.getClass().getSimpleName());
            }
        });
        
        hbLoadSave.getChildren().addAll(save,load);
        
        tAr = new TextArea("balblblfjdk");
        tAr.setWrapText(true);
        tAr.setEditable(false);
        
        root2.getChildren().addAll(hbLoadSave,vhidDann,lbl,btn,hbSlova,hbNomery,hbSlovaRez,tAr);
        Tab tbExec = new Tab("Робота з словами");
        tbExec.setClosable(false);
        tbExec.setContent(root2);
        tp.getTabs().addAll(wordSelect,tbExec);
        Scene scene = new Scene(tp, 1000, 450);
        
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
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
    private static String[] loadWordsFromFile(String path){
        String[] st = null;
        String middle="";
        String middle2="";
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            /*st = br.readLine().replaceAll(",", "").replaceAll(".", "").replaceAll("?", "").replaceAll("!", "")
                    .replaceAll(";", "").replaceAll("-", "").replaceAll(":", "").replaceAll("(", "")
                    .replaceAll(")", "").replaceAll("+", "").replaceAll("*", "").split(" ");*/
            
            while((middle = br.readLine()) != null){
                middle2 += middle;//br.readLine().replaceAll(",;.;!;\\?", "").split(" ");
            }
            st = middle2.replaceAll(",", "").replaceAll("\\.", "").replaceAll("\\?", "").replaceAll("!", "")
                    .replaceAll(";", "").replaceAll("-", "").replaceAll(":", "").replaceAll("\\(", "")
                    .replaceAll("\\)", "").replaceAll("\\+", "").replaceAll("\\*", "").split(" ");
        } catch (Exception e) {
            System.out.println("Not mathc count");
        }
        return st;
    }
    
    /**
     * Обновляє номери слів
     */
    public static void updateNumbers(){
        int[] nums = new int[10];
        for (int i = 0; i < 10; i++) {
            //заповнює мітки порядковими номерами слів
            lblNom[i].setText(""+cbMass[i].getSelectionModel().getSelectedIndex());
            nums[i]=cbMass[i].getSelectionModel().getSelectedIndex();
        }
        double[] nums2 = new double[262];
        for (int i = 0; i < 10; i++) {
            nums2[nums[i]]=1.0;
        }
        fillTextAnswer(getAnswer(nums2));
    }
    
    /**
     * Отримує відповідь нейронної мережі
     * @param question завдання
     * @return 
     */
    public static int[] getAnswer(double[] question){
        double ans[] = nn1.getAnswer(question); // нічого не змінювалося тому, що завдання було старим(останнє введене питаня/завдання), необхідно змінювати завдання.
        double biggest[] = new double[10];
        int biggestNumb[] = new int[10];
        for (int i = 0; i < ans.length; i++) {
            double checked = ans[i];//массив що перевіряється
            //int chekedNum = i;//номера які потрібно відібрати
            double buffer = 0;//буффер
            int bufferNum = 0;//буффер номерів
            for (int j = 0; j < 10; j++) {
                if(biggest[j]>checked)continue;//якщо найбільший найближчий більше за той що перевіряється - наступний
                buffer = biggest[j];//якщо ні то в буфер передається поточний найбільший
                bufferNum = biggestNumb[j];//буфер номера передається найбільший номер
                biggest[j] = checked;//в найбільший передається перевіряємий
                biggestNumb[j] = i;//в найбільший номер передається поточний перевіряємий номер
                checked = buffer;//перевіряємий тепер попередній що виявився меншим
                //chekedNum = bufferNum;
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
            btnWords[i].setText(words.get(numbers[i]));
        }
    }
}
