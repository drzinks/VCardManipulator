package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main extends Application {

    private Desktop desktop = Desktop.getDesktop();
    private Stage stage;
    private List<String> contactList = new ArrayList<String>();

    @Override
    public void start(final Stage stage) {
        this.stage = stage;
        stage.setTitle("vCard Manipulator");

        final FileChooser fileChooser = new FileChooser();

        final Button openButton = new Button("Choose vCard file..");

        openButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        configureFileChooser(fileChooser);
                        File file = fileChooser.showOpenDialog(stage);
                        if (file != null) {
                            openFile(file);
                        }
                    }
                });


        final GridPane inputGridPane = new GridPane();

        GridPane.setConstraints(openButton, 0, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openButton);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(rootGroup,400,500));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private static void configureFileChooser(final FileChooser fileChooser){
        //fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home")+"/Desktop")
        );
    }


    private void openFile(File file) {
        try {
            //desktop.open(file);
            final VCReader vcReader = new VCReader(new FileReader(file));
           //List<String> contactList = new ArrayList<String>();
            for (String contactName : vcReader) {
//                System.out.println(contactName);
                if(contactName != null)
                    contactList.add(contactName);
            }
            Group root = new Group();
            stage.setScene(new Scene(root));
            final ListView<String> listView = new ListView<String>();
            listView.setItems(FXCollections.observableArrayList(
                 contactList
            ));
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    String s = listView.getSelectionModel().getSelectedItem();
                    System.out.println(s);
                    vcReader.remove(s,contactList);
                    listView.setItems(FXCollections.observableArrayList(
                            contactList));
                }
            });
            root.getChildren().add(listView);
        } catch (IOException ex) {
            Logger.getLogger(
                    Main.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }



}