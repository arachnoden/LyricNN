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
import javafx.geometry.Insets;
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
import javafx.stage.DirectoryChooser;
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
    //private String pathWords;
    boolean cbLissInst = true;
    private String filePath;
    private static String savePath;
    private static TextField tfTasks;
    private static TextField tfAnswers;
    private static HBox hbNomery = new HBox(5);
    private static HBox hbSlovaRez = new HBox(5);
    static EventHandler<ActionEvent> wordsPod;
    static TextField tfHidLay = stvorTFdlCyfry(50);
    static TextField tfLearCo = stvorTFdlCyfry(50);
    static TextField tfShurns = stvorTFdlCyfry(50);
    static TextField tfWrdCoun = stvorTFdlCyfry(50);
    static int num;
    @Override
    public void start(Stage primaryStage) {
        HBox hbSlova = new HBox(5);
        
        HBox hbLoadSave = new HBox(5);
        Button save = new Button("Зберегти");
        Button load = new Button("Завантажити");
        TabPane tp = new TabPane();
        VBox vbTAnl = new VBox(5);
        HBox hbButPan = new HBox(5);
        
        //cbMass = new ComboBox[10];//1
        EventHandler<ActionEvent> podij = (ActionEvent aEv) -> {
            //ComboBox<String> cb = (ComboBox<String>)aEv.getSource();
            //tAr.appendText(" "+cb.getSelectionModel().getSelectedItem());
            updateNumbers();
        };
        
        Tab wordSelect = new Tab("Відбір слів");
            wordSelect.setClosable(false);

            VBox root1 = new VBox(5);

            Button knpZavant = new Button("Загрузить слова");
            Label lblKnpCount = new Label("Количество слов");
            TextArea taSlova = new TextArea();
            taSlova.setWrapText(true);
            knpZavant.setOnAction(pdj ->{
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
            });
            
            Button knpCrTasAnsw = new Button("Создать/дополнить задания/ответы");
            knpCrTasAnsw.setOnAction(pdj ->{
                task = new double[smpTskWrd.size()-num][ldb.getSortWords().size()];// ------------------- зробити потім можливість налаштування кількості --------------
                answers = new double[smpTskWrd.size()-num][ldb.getSortWords().size()];// ------------------- зробити потім можливість налаштування кількості --------------
                for (int i = 0; i < smpTskWrd.size()-num; i++) {
                    double x = 1000.0;
                    //for (int j = 0; j < 10; j++) {
                    for (int j=num-1;j>=0;j--) {
                        if(task[i][ldb.getSortWords().indexOf(smpTskWrd.get(i+j))]==0.0){
                            task[i][ldb.getSortWords().indexOf(smpTskWrd.get(i+j))]/*+*/=x/1000.0;//1.0;
                            x=x-75.0;
                        }
                    }
                    answers[i][ldb.getSortWords().indexOf(smpTskWrd.get(i+num))]=1.0;
                }
                
            });
            
            Button knpZberVFajl = new Button("Сохранить в файл");
            knpZberVFajl.setOnAction(pdj ->{
                /*FileChooser fcZb = new FileChooser();
                File flOsZb = new File(savePath);
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
                //File fl2 = fcZb.showSaveDialog(primaryStage);
                fcZb.setInitialDirectory(fl.getParentFile());
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
                }*/
                //File flOsZb = new File(savePath);
                File fl = new File(savePath+"\\"+tfTasks.getText()+".txt");
                //flOsZb = fl;
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
                //File fl2 = fcZb.showSaveDialog(primaryStage);
                //fcZb.setInitialDirectory(fl.getParentFile());
                File fl2 = new File(savePath+"\\"+tfAnswers.getText()+".txt");
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
        
        
        
        //lblNom = new Label[10];//2
        /*for (int i = 0; i < 10; i++) {
            lblNom[i] = new Label();
            hbNomery.getChildren().add(lblNom[i]);
        }*/
        
        wordsPod = (ActionEvent aEv) -> {
            //------------------------------------------------------------------------
            /*if(cbLissInst){
                for (int i = 0; i < cbMass.length; i++) {
                    cbMass[i].setOnAction(null);
                }
                cbLissInst=false;
            }*/
            Button knp = (Button)aEv.getSource();
            System.out.println(""+knp.getText());
            tAr.appendText(" "+knp.getText());
            for (int i = 0; i < num; i++) {
                if(i<num-1)cbMass[i].getSelectionModel().select(cbMass[i+1].getSelectionModel().getSelectedIndex());
                else cbMass[i].getSelectionModel().select(knp.getText());
            }
            updateNumbers();
        };
        
        
        
        Button crNeuNet = new Button("Создать нейронную сеть");
        
        crNeuNet.setOnAction(pdj ->{
            int cnt = Integer.parseInt(tfHidLay.getText());
            int[] str = new int[cnt];
            int siz = ldb.getSortWords().size();
            for (int i = 0; i < cnt; i++) {
                str[i]=siz;
            }
            ldb.createNN(siz, str);
            createCBXS(podij,hbSlova);
        });
        
        /*btnWords = new Button[10];//3
        for (int i = 0; i < 10; i++) {
            btnWords[i] = new Button("Slovo");
            hbSlovaRez.getChildren().add(btnWords[i]);
            btnWords[i].setOnAction(wordsPod);
        }*/
        
        
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
            createCBXS(podij,hbSlova);
        });
        
        hbLoadSave.getChildren().addAll(crNeuNet,save,load);
        
        tAr = new TextArea();
        tAr.setWrapText(true);
        tAr.setEditable(false);
        
        root2.getChildren().addAll(hbLoadSave,vhidDann,lbl,btn,hbSlova,hbNomery,hbSlovaRez,tAr);
        Tab tbExec = new Tab("Робота з словами");
        tbExec.setClosable(false);
        tbExec.setContent(root2);
        
        Tab tbSett = new Tab("Настройки");
            
            VBox vbSett = new VBox(5);
            vbSett.setPadding(new Insets(10));
            HBox hbLoadPath = new HBox(5);
            Button knpLPath = new Button("Директория с текстом");
            Label lblLPath = new Label("Путь к файлам с текстом");
            Label lblSavPath = new Label("Сохранять файлы в этом каталоге");
            knpLPath.setOnAction(pdj ->{
                DirectoryChooser dc = new DirectoryChooser();
                dc.setTitle("Оберіть директорію з планшетами");
                File fl = dc.showDialog(primaryStage);
                if(fl==null)return;

                filePath = fl.getAbsolutePath();
                lblLPath.setText(fl.getAbsolutePath());
            });
            
            
            
            hbLoadPath.getChildren().addAll(knpLPath,lblLPath);
            
            HBox hbSave = new HBox(5);
            
            Button knpPathSave = new Button("Выберите папку для сохранения");
            
            knpPathSave.setOnAction(pdj -> {
                DirectoryChooser dc = new DirectoryChooser();
                dc.setTitle("Оберіть директорію з планшетами");
                File fl = dc.showDialog(primaryStage);
                if(fl==null)return;

                savePath = fl.getAbsolutePath();
                lblSavPath.setText(fl.getAbsolutePath());
            });
            
            HBox hbTasks = new HBox(5);
            HBox hbAnsw = new HBox(5);
            tfTasks = new TextField("Tasks");
            tfAnswers = new TextField("Answers");
            hbTasks.getChildren().addAll(new Label("файл заданий"),tfTasks);
            hbAnsw.getChildren().addAll(new Label("файл ответов"),tfAnswers);
            

            tfHidLay.setText("3");
            tfLearCo.setText("3");
            tfShurns.setText("30");
            tfWrdCoun.setText("10");
            
            
            hbSave.getChildren().addAll(knpPathSave,lblSavPath);
            vbSett.getChildren().addAll(hbLoadPath,hbSave,hbTasks,hbAnsw,
                    new HBox(new Label("Скрытых нейронов:  "),tfHidLay),
                    new HBox(new Label("Коефициент обучения:  "),tfLearCo),
                    new HBox(new Label("Уверенность сети:  "),tfShurns),
                    new HBox(new Label("Кол. провер. слов:  "),tfWrdCoun));
        tbSett.setContent(vbSett);
        
        tp.getTabs().addAll(tbSett,wordSelect,textAnalyze,tbExec);
        Scene scene = new Scene(tp, 1000, 450);
        
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void createCBXS(EventHandler<ActionEvent> podij, HBox hbSlova) {
        for (int i = 0; i < 10; i++) {
            cbMass[i] = new ComboBox<>();
            cbMass[i].getItems().addAll(ldb.getSortWords());
            cbMass[i].getSelectionModel().select(0);
            cbMass[i].setOnAction(podij);
            hbSlova.getChildren().add(cbMass[i]);
        }
    }
    
    /**
     * Вмикає вікно завантаження слів з файлу
     * @param priSt сцена до якої прив'язується вікно
     * @param ta текстова зона в яку завантажуються слова
     */
    public void loadWindow(Stage priSt, TextArea ta) {
        FileChooser fch = new FileChooser();
        fch.setInitialDirectory(new File(filePath));
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
        
        //double taskSet[][] = loadArrayFromFile("E:\\Users\\Den\\Desktop\\001.txt", ldb.getSortWords().size());
        //double answerSet[][] = loadArrayFromFile("E:\\Users\\Den\\Desktop\\002.txt", ldb.getSortWords().size());
        System.out.println(savePath+tfTasks.getText()+".txt");
        double taskSet[][] = loadArrayFromFile(savePath+"\\"+tfTasks.getText()+".txt", ldb.getSortWords().size());
        double answerSet[][] = loadArrayFromFile(savePath+"\\"+tfAnswers.getText()+".txt", ldb.getSortWords().size());
        lbl.textProperty().bind(nn.messageProperty());
        nn.setParameters(taskSet, answerSet, (Double.parseDouble(tfLearCo.getText())/10), (Double.parseDouble(tfShurns.getText())/100));
        new Thread(nn).start();
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
        double x = 1000.0;
        for (int i=9;i>=0;i--) {
            if(nums2[nums[i]]==0.0)nums2[nums[i]]=x/1000.0;// тестовий режим ________________________++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            x=x-75.0;
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
    
    public static void initiate(){
        cbMass = new ComboBox[num];
        lblNom = new Label[num];
        for (int i = 0; i < 10; i++) {
            lblNom[i] = new Label();
            hbNomery.getChildren().add(lblNom[i]);
        }
        btnWords = new Button[10];//3
        for (int i = 0; i < 10; i++) {
            btnWords[i] = new Button("Slovo");
            hbSlovaRez.getChildren().add(btnWords[i]);
            btnWords[i].setOnAction(wordsPod);
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
