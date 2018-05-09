/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lyricnn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    //private static List<String> words;
    private static NeuralNetwork nn1;
    private static ComboBox<String>[] cbMass;
    private static Label lblNom[];
    /**
     * Массив кнопок з словами
     */
    private static Button btnWords[];
    //private static ArrayList<String> sortedWords;
    private static LyricDB ldb = new LyricDB();
    /**
     * Слова на основі яких потрібно натренувати нейронну мережу
     */
    List<String> smpTskWrd;
    double[][] task;
    double[][] answers;
    TextArea tAr;
    String pathWords;
    boolean cbLissInst = true;
    @Override
    public void start(Stage primaryStage) {
        HBox hbSlova = new HBox(5);
        HBox hbNomery = new HBox(5);
        cbMass = new ComboBox[10];
        EventHandler<ActionEvent> podij = (ActionEvent aEv) -> {
            ComboBox<String> cb = (ComboBox<String>)aEv.getSource();
            tAr.appendText(" "+cb.getSelectionModel().getSelectedItem());
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
            knpZavant.setOnAction(pdj ->{
                //testLbl.setText(taSlova.getText().replaceAll("\n", ","));
                /*FileChooser fch = new FileChooser();
                fch.setInitialDirectory(new File("C:/"));
                fch.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Текст", "*.txt"),
                        new FileChooser.ExtensionFilter("Всі файли", "*.*"));
                File fl = fch.showOpenDialog(primaryStage);
                if(fl==null)return;*/
                //System.out.println(""+fl.getAbsolutePath());
                //words = Arrays.asList(loadWordsFromFile(fl.getAbsolutePath()));
                /*for (int i = 0; i < 10; i++) {
                    cbMass[i] = new ComboBox<>();
                    cbMass[i].getItems()
                            .addAll(words);
                    cbMass[i].getSelectionModel().select(0);
                    cbMass[i].setOnAction(podij);
                    hbSlova.getChildren().add(cbMass[i]);//потім перемістити цей пункт в нову кнопку з відфільтрованими словами
                }*/
                //taSlova.appendText(loadWordsFromFile(fl.getAbsolutePath()));
                loadWindow(primaryStage, taSlova);
            });
            
            Button knpDelDup = new Button("Удалить дубликаты");
            knpDelDup.setOnAction(act ->{
                System.out.println("knp txt - "+taSlova.getText());
                taSlova.setText(arrayListToString(deleteDups(taSlova.getText()/*.toLowerCase()*/)));
            });
            
            root1.getChildren().addAll(knpZavant,lblKnpCount,taSlova,knpDelDup);
        
        wordSelect.setContent(root1);
        
        //------- 2 створення--------------------------------------------------------------------------------------------------------------
        
        Tab textAnalyze = new Tab("Анализ текста");
        textAnalyze.setClosable(false);
        
            VBox vbTAnl = new VBox(5);
            HBox hbButPan = new HBox(5);
            
            TextArea taTAnl = new TextArea();
            taTAnl.setWrapText(true);
            
            Button knpLoadWords = new Button("Загрузить пример");
            knpLoadWords.setOnAction(pdj ->{
                taTAnl.clear();
                loadWindow(primaryStage,taTAnl);
            });
            
            Button knpMemoryWords = new Button("Создать");
            knpMemoryWords.setOnAction(pdj ->{
                smpTskWrd = Arrays.asList(taTAnl.getText().split("\n"));
                //task = new double[smpTskWrd.size()-10][sortedWords.size()];// ------------------- зробити потім можливість налаштування кількості --------------
                //answers = new double[smpTskWrd.size()-10][sortedWords.size()];// ------------------- зробити потім можливість налаштування кількості --------------
                //checkedWords.forEach(wrd -> System.out.println(wrd));
                //System.out.println("size - "+checkedWords.size());
            });
            
            Button knpCrTasAnsw = new Button("Создать/дополнить задания/ответы");
            knpCrTasAnsw.setOnAction(pdj ->{
                task = new double[smpTskWrd.size()-10][ldb.getSortWords().size()];// ------------------- зробити потім можливість налаштування кількості --------------
                answers = new double[smpTskWrd.size()-10][ldb.getSortWords().size()];// ------------------- зробити потім можливість налаштування кількості --------------
                for (int i = 0; i < smpTskWrd.size()-10; i++) {
                    double x = 1000.0;
                    //for (int j = 0; j < 10; j++) {
                    for (int j=9;j>=0;j--) {
                        if(task[i][ldb.getSortWords().indexOf(smpTskWrd.get(i+j))]==0.0){
                            task[i][ldb.getSortWords().indexOf(smpTskWrd.get(i+j))]=x/1000.0;
                            x=x-75.0;
                        }
                    }
                    answers[i][ldb.getSortWords().indexOf(smpTskWrd.get(i+10))]=1.0;
                }
                
            });
            
            Button knpZberVFajl = new Button("Сохранить в файл");
            knpZberVFajl.setOnAction(pdj ->{
                FileChooser fcZb = new FileChooser();
                File flOsZb = new File("C:/");
                //if(!flOsZb.exists()&&!flOsZb.isDirectory())flOsZb = new File("C:/");
                fcZb.setInitialDirectory(flOsZb);
                fcZb.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Текст", "*.txt"),
                        new FileChooser.ExtensionFilter("Всі файли", "*.*"));
                File fl = fcZb.showSaveDialog(primaryStage);
                flOsZb = fl;
                if(fl==null)return;
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(fl,true))){
                    for (double[] ds : task) {
                        for (double d : ds) {
                            bw.write(d+",");
                        }
                        bw.newLine();
                    }
                } catch (Exception e) {
                    System.out.println("Not mathc count");
                }
                File fl2 = fcZb.showSaveDialog(primaryStage);
                if(fl2==null)return;
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(fl2,true))){
                    for (double[] ds : answers) {
                        for (double d : ds) {
                            bw.write(d+",");
                        }
                        bw.newLine();
                    }
                } catch (Exception e) {
                    System.out.println("Not mathc count");
                }
            });
            
            hbButPan.getChildren().addAll(knpLoadWords,knpMemoryWords,knpCrTasAnsw,knpZberVFajl);
            
            vbTAnl.getChildren().addAll(hbButPan,taTAnl);
            
        textAnalyze.setContent(vbTAnl);
        
        //------------3 робота з нейронною мережею-----------------------------------------------------------------------------------------------------------------
        
        VBox root2 = new VBox(5);
        HBox vhidDann = new HBox(5);
        
        
        
        lblNom = new Label[10];
        for (int i = 0; i < 10; i++) {
            lblNom[i] = new Label();
            hbNomery.getChildren().add(lblNom[i]);
        }
        
        EventHandler<ActionEvent> wordsPod = (ActionEvent aEv) -> {
            //------------------------------------------------------------------------
            if(cbLissInst){
                for (int i = 0; i < cbMass.length; i++) {
                    cbMass[i].setOnAction(null);
                }
                cbLissInst=false;
            }
            Button knp = (Button)aEv.getSource();
            System.out.println(""+knp.getText());
            tAr.appendText(" "+knp.getText());
            for (int i = 0; i < 10; i++) {
                if(i<9)cbMass[i].getSelectionModel().select(cbMass[i+1].getSelectionModel().getSelectedIndex());
                else cbMass[i].getSelectionModel().select(knp.getText());
            }
            updateNumbers();
        };
        
        HBox hbSlovaRez = new HBox(5);
        
        Button crNeuNet = new Button("Создать нейронную сеть");
        
        crNeuNet.setOnAction(pdj ->{
            //nn1 = new NeuralNetwork(ldb.getSortWords().size(), new int[]{ldb.getSortWords().size(),ldb.getSortWords().size()/*,262,262*/,ldb.getSortWords().size()});
            ldb.createNN(ldb.getSortWords().size(), new int[]{ldb.getSortWords().size(),ldb.getSortWords().size()/*,262,262*/,ldb.getSortWords().size()});
            for (int i = 0; i < 10; i++) {
                cbMass[i] = new ComboBox<>();
                cbMass[i].getItems().addAll(ldb.getSortWords());
                cbMass[i].getSelectionModel().select(0);
                cbMass[i].setOnAction(podij);
                hbSlova.getChildren().add(cbMass[i]);
            }
        });
        
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
        
        //nn1 = new NeuralNetwork(79, new int[]{79,79/*,262,262*/,79});
        
        Button btn = new Button("Train");
        btn.setOnAction(act -> {
            trainNetwork(ldb.getNN(),lbl);
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
                oos.writeObject(ldb);
            } catch (IOException ex) {
                System.out.println("Помилка запису файлу");
                System.out.println(ex.getClass().getSimpleName());
            }
        });
        load.setOnAction(pdj ->{
            FileChooser fch = new FileChooser();
            fch.setInitialDirectory(new File("C:/"));
            fch.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Нейронна мережа", "*.nmz"),
                    new FileChooser.ExtensionFilter("Всі файли", "*.*"));
            File fl = fch.showOpenDialog(primaryStage);
            if(fl==null)return;
            try(FileInputStream fis = new FileInputStream(fl)){
                ObjectInputStream ois = new ObjectInputStream(fis);
                ldb = (LyricDB)ois.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Помилка завантаження файлу");
                System.out.println(ex.getClass().getSimpleName());
            }
        });
        
        hbLoadSave.getChildren().addAll(crNeuNet,save,load);
        
        tAr = new TextArea("balblblfjdk");
        tAr.setWrapText(true);
        tAr.setEditable(false);
        
        root2.getChildren().addAll(hbLoadSave,vhidDann,lbl,btn,hbSlova,hbNomery,hbSlovaRez,tAr);
        Tab tbExec = new Tab("Робота з словами");
        tbExec.setClosable(false);
        tbExec.setContent(root2);
        tp.getTabs().addAll(wordSelect,textAnalyze,tbExec);
        Scene scene = new Scene(tp, 1000, 450);
        
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Вмикає вікно завантаження слів з файлу
     * @param priSt сцена до якої прив'язується вікно
     * @param ta текстова зона в яку завантажуються слова
     */
    public void loadWindow(Stage priSt, TextArea ta) {
        FileChooser fch = new FileChooser();
        fch.setInitialDirectory(new File("C:/"));
        fch.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Текст", "*.txt"),
                new FileChooser.ExtensionFilter("Всі файли", "*.*"));
        File fl = fch.showOpenDialog(priSt);
        if(fl==null)return;
        ta.appendText(loadWordsFromFile(fl.getAbsolutePath()));
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
        double taskSet[][] = loadArrayFromFile("E:\\Users\\Den\\Desktop\\001.txt", ldb.getSortWords().size());
        double answerSet[][] = loadArrayFromFile("E:\\Users\\Den\\Desktop\\002.txt", ldb.getSortWords().size());
        lbl.textProperty().bind(nn.messageProperty());
        nn.setParameters(taskSet, answerSet, 0.4, 0.2);
        System.out.println("done 2");
        new Thread(nn).start();
        System.out.println("done 3");
    }
    
    /**
     * Завантажує завдання та відповіді з текстових файлів у массив
     * @param path шлях до файлу
     * @param rows строк
     * @param columns колонок
     * @return массив завдання/відповіді
     */
    private static double[][] loadArrayFromFile(String path, int columns) {
        double[][] arr = null;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            ArrayList<String> alSt = new ArrayList<>();
            String middle=null;
            while((middle = br.readLine()) != null){
                alSt.add(middle);
            }
            arr = new double[alSt.size()][columns]; // создали массив который вернем из метода

            for (int i = 0; i < alSt.size(); i++) {
            //while((middle = br.readLine()) != null){
                //String st[] = br.readLine().split(",");
                String st[] = alSt.get(i).split(",");
                //String st[] = middle.split(",");
                for (int j = 0; j < columns; j++) {
                    arr[i][j]=Double.parseDouble(st[j]);
                }
                i++;
            }
            System.out.println("done");
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
    //private static String[] loadWordsFromFile(String path){
    private static String loadWordsFromFile(String path){
        String st = null;
        String middle;
        String middle2="";
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            while((middle = br.readLine()) != null){
                middle2 += middle+" ";//br.readLine().replaceAll(",;.;!;\\?", "").split(" ");
            }
            st = middle2.replaceAll("\\pP", "").toLowerCase()/*.split(" ")*/;
            st = st.replaceAll(" ", "\n");
        } catch (Exception e) {
            System.out.println("Not mathc count");
        }
        //System.out.println("count words - "+st.length);
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
        double[] nums2 = new double[ldb.getSortWords().size()];
        for (int i = 0; i < 10; i++) {
            System.out.println("check nums - "+nums[i]);
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
        //double ans[] = nn1.getAnswer(question); // нічого не змінювалося тому, що завдання було старим(останнє введене питаня/завдання), необхідно змінювати завдання.
        double ans[] = ldb.getNN().getAnswer(question); // нічого не змінювалося тому, що завдання було старим(останнє введене питаня/завдання), необхідно змінювати завдання.
        double biggest[] = new double[10];
        int biggestNumb[] = new int[10];
        for (int i = 0; i < ans.length; i++) {
            checkNumber(biggest, ans[i], biggestNumb, i);
        }
        return biggestNumb;
    }
    
    /**
     * Заповнити словами текстові мітки
     * @param numbers порядкові номера слів
     */
    public static void fillTextAnswer(int[] numbers){
        for (int i = 0; i < 10; i++) {
            //btnWords[i].setText(words.get(numbers[i]));
            //btnWords[i].setText(sortedWords.get(numbers[i]));
            btnWords[i].setText(ldb.getSortWords().get(numbers[i]));
        }
    }
    
    /**
     * Видаляє дублюючіся слова з списку
     * @param wrd строка зі словами
     * @return массив відсортованих слів
     */
    public ArrayList<String> deleteDups(String wrd){
        List<String> uns = Arrays.asList(wrd.split("\n"));
        //ArrayList<String> sorted = new ArrayList<>();
        
        passCheck(uns/*, sorted*/);
        /*uns=sorted;
        uns.add(uns.get(0));
        uns.remove(0);
        sorted=new ArrayList<>();
        passCheck(uns, sorted);*/
        
        //return sortedWords;
        return ldb.getSortWords();
    }
    
    /**
     * Видаляє дублікати слів
     * @param uns несортовані слова
     */
    public void passCheck(List<String> uns/*, ArrayList<String> sorted*/) {
        for (String un : uns) {
            boolean copy=false;
            //if(sortedWords==null)sortedWords=new ArrayList<>();
            //if(ldb.getSortWords()==null)ldb.getSortWords()=new ArrayList<>();
            for (String str : ldb.getSortWords()) {
                //System.out.println(str+" -- "+un);
                //System.out.println(str+" -- "+un+" -- "+str.equals(un));
                if(str.equals(un)){
                    //System.out.println("copy = true!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    copy=true;
                }
                //System.out.println("copy - "+copy);
            }
            //if(!copy)sortedWords.add(un);
            if(!copy)ldb.getSortWords().add(un);
            
        }
        /*Set<String> stStr = new HashSet<String>();
        stStr.addAll(uns);
        sorted.addAll(stStr);*/
    }
    
    public static void checkNumber(double[] arr, double chck, int[] mas, int numb){
        double buffer = 0.0;
        int bufferN = 0;
        for (int i = 0; i < arr.length; i++) {
            if(chck>arr[i]){
                buffer=arr[i];
                arr[i]=chck;
                chck=buffer;
                
                bufferN = mas[i];
                mas[i] = numb;
                numb = bufferN;
            }
        }
    }
    
    /**
     * Перетворює массив в строку
     * @param wrds массив слів які потрібно перетворити в строку
     * @return строку слів з массива
     */
    public String arrayListToString(ArrayList<String> wrds){
        System.out.println("ArrToStr");
        String txt="";
        for (String wrd : wrds) {
            txt+=wrd+"\n";
        }
        return txt;
    }
}
