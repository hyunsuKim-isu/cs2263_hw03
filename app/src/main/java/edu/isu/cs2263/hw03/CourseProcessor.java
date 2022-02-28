package edu.isu.cs2263.hw03;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert.AlertType;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Add, store, and load courses and creates a UI that helps to do it comfortably.
 */
public class CourseProcessor extends Application{
    private ArrayList<Course> course = new ArrayList<Course>();
    private TableView<Course> table = new TableView<Course>();
    private ObservableList<Course> courseObservableList = FXCollections.observableArrayList(course);
    private FilteredList<Course> filteredList = new FilteredList<Course>(courseObservableList);

    /**
     * @param c information of course from enter button
     */
    public void add(Course c){
        course.add(c);
    }

    /**
     * transform object to json and make file
     */
    public void toJson(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String courseJson = gson.toJson(courseObservableList);
        try{
            FileWriter file = new FileWriter("./save.json");
            file.write(courseJson);
            file.flush();
            file.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * transform json to object from file
     */
    public void readJson(){
        try{
            Reader reader = Files.newBufferedReader(Paths.get("./save.json"));
            Gson gson = new Gson();
            ArrayList<Course> newList = gson.fromJson(reader, new TypeToken<ArrayList<Course>>(){}.getType());
            newList.forEach(System.out::println);
            courseObservableList.clear();
            for(int i =0; i < newList.size(); i++){
                courseObservableList.add(i, newList.get(i));
            }
            table.setItems(filteredList);
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage stage)throws Exception{
        // window title
        stage.setTitle("Courses Editor");

        //Drop down list
        ComboBox comboBox = new ComboBox();
        comboBox.setPrefSize(100,20);
        comboBox.getItems().addAll(
                Course.departmentsFullName
        );

        //Buttons
        Button enterBtn = new Button("Enter");
        Button saveBtn = new Button("Save");
        Button loadBtn = new Button("Load");
        Button closeBtn = new Button("Close");
        Button deptBtn = new Button("Display Departments");
        Button allBtn = new Button("Display All");

        // Text Field
        TextField courseNumField = new TextField();
        courseNumField.setPrefWidth(100);
        courseNumField.setPromptText("Course Number");
        TextField nameField = new TextField();
        nameField.setPrefWidth(100);
        nameField.setPromptText("Name");
        TextField creditField = new TextField();
        creditField.setPrefWidth(100);
        creditField.setPromptText("Credits");

        //HBox Containers
        HBox dropDownBox = new HBox();
        dropDownBox.setPadding(new Insets(10,10,10,10));

        HBox textFieldBox = new HBox();
        textFieldBox.getChildren().addAll(courseNumField, nameField, creditField);
        textFieldBox.setSpacing(3);

        HBox btnBox = new HBox();
        btnBox.setPadding(new Insets(10,10,10,0));
        btnBox.setMargin(enterBtn, new Insets(0,5,0,0));
        btnBox.setMargin(loadBtn, new Insets(0,5,0,0));
        btnBox.getChildren().addAll(enterBtn,saveBtn, loadBtn, closeBtn);

        GridPane gridPane = new GridPane();
        gridPane.setMinSize(300,600);
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        gridPane.add(comboBox, 0,0);
        gridPane.add(courseNumField, 0,1);
        gridPane.add(nameField,0,2);
        gridPane.add(creditField, 0,3);
        gridPane.add(btnBox, 0,4);
        gridPane.add(deptBtn, 0,5);
        gridPane.add(allBtn, 0,6);


        // Button actions
        closeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Close");
                alert.setContentText("Are you sure to close?");
                Optional<ButtonType> result = alert.showAndWait();
                ButtonType button = result.orElse(ButtonType.CANCEL);
                if(button == ButtonType.OK){
                    stage.close();
                }else{
                }
            }
        });

        enterBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CourseProcessor c = new CourseProcessor();
                String department = (String) comboBox.getValue();
                String courseNum = courseNumField.getText();
                String name = nameField.getText();
                String credits = creditField.getText();
                String dept = Course.getDeptName(department);
                if(department.isEmpty() | courseNum.isEmpty() | name.isEmpty() | credits.isEmpty()){
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Warning");
                    alert.setContentText("The field cannot be left blank");
                    alert.show();
                }else{
                    Course course = new Course(dept, courseNum, name, credits);
                    courseObservableList.add(course);
                    c.add(course);
                    courseNumField.clear();
                    nameField.clear();
                    creditField.clear();
                }
            }
        });

        deptBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String department = (String) comboBox.getValue();
                String dept = Course.getDeptName(department);
                Predicate<Course> containDept = i -> i.getDepartment().contains(dept);
                filteredList.setPredicate(containDept);
            }
        });

        allBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                filteredList.setPredicate(null);
            }
        });

        saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                toJson();
            }
        });

        loadBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                readJson();
            }
        });

        // Table
        TableView table = new TableView();
        table.setPlaceholder(new Label("No Courses"));
        TableColumn<Course, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        TableColumn<Course, String> courseNumCol = new TableColumn<>("Course Num");
        courseNumCol.setCellValueFactory(new PropertyValueFactory<>("courseNum"));
        TableColumn<Course, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Course, String> creditCol = new TableColumn<>("credits");
        creditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));

        table.setItems(filteredList);
        table.getColumns().addAll(deptCol, courseNumCol, nameCol, creditCol);

        VBox vb = new VBox();
        vb.getChildren().addAll(table, textFieldBox);
        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(gridPane);
        borderPane.setRight(vb);

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        Application.launch(args);
    }




}
