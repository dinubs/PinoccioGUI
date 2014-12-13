/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalproject;

import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


// Pinoccio API
import io.pinocc.pinocico.java.*;
import com.google.gson.*;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.KeyCode;
import org.json.JSONArray;

/**
 *
 * @author Gavin
 */
public class FinalProject extends Application {
    
    public PinoccioAPI pinoccioAPI = new PinoccioAPI();
    public static String key = "";
    public static File file = new File("config.txt");
    public static TextField middleTextField = new TextField();
    public ArrayList<FollowScout> scouts = new ArrayList<>();
    public ArrayList<String> pastCommands = new ArrayList<>();
    public static Scout currentScout;
    public int currentCommandIndex;
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane pane = new BorderPane();
        
        GridPane header = new GridPane();
        header.setPadding(new Insets(15));
        header.setHgap(5);
        header.setVgap(5);
        
        Image pinoccioImage = new Image("https://raw.githubusercontent.com/gavindinubilo/gavindinubilo.github.io/master/images/pinoccio.png");
        ImageView pinoccioImg = new ImageView(pinoccioImage);
        pinoccioImg.setFitWidth(100);
        pinoccioImg.setFitHeight(100);
        Label headerText = new Label();
        headerText.setText("Pinoccio");
        headerText.setId("header");
        header.add(pinoccioImg, 0, 0);
        header.add(headerText, 1, 0);
        pane.setTop(header);
        
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(15));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        
        TextField apiKeyField = new TextField();
        apiKeyField.setMinWidth(200);
        apiKeyField.setPromptText("Api Key");
        apiKeyField.setFocusTraversable(false);
        apiKeyField.setFocusTraversable(true);
        Button saveApiKey = new Button("Save Api Key");
        saveApiKey.setMinWidth(200);
        gridPane.add(apiKeyField, 0, 0);
        gridPane.add(saveApiKey, 0, 1);
        
        apiKeyField.setOnAction(e -> {
            this.getApiStuff(apiKeyField, gridPane);
        });
        saveApiKey.setOnAction(e -> {
            this.getApiStuff(apiKeyField, gridPane);
        });
        
        pane.setLeft(gridPane);
        if(!this.key.equals("")) {
            apiKeyField.setText(this.key);
            getApiStuff(apiKeyField, gridPane);
        }
        
        GridPane centerGridPane = new GridPane();
        centerGridPane.setPadding(new Insets(15));
        centerGridPane.setHgap(15.0);
        centerGridPane.setVgap(15.0);
        
        centerGridPane.add(middleTextField, 0, 0);
        middleTextField.setPromptText("Enter a command");
        middleTextField.setDisable(true);
        middleTextField.setMinWidth(400);
        middleTextField.setOnAction(e -> {
            System.out.println(e.toString());
            currentScout.sendCommand(middleTextField.getText());
            pastCommands.add(middleTextField.getText());
            int size = Math.min(pastCommands.size() - 1, 4);
            System.out.println(size);
            
            for(int i = size; i > -1; i--) {
                if (centerGridPane.getChildren().size() > i + 1) {
                    centerGridPane.getChildren().remove(i + 1);
                }
                
                Label temp = new Label(pastCommands.get(pastCommands.size() - 1 - i));
                temp.setId("middleLabel");
                temp.setMinWidth(400);
                temp.setContentDisplay(ContentDisplay.LEFT);
                temp.setPadding(new Insets(5));
                
                centerGridPane.add(temp, 0, i + 1);
                System.out.println("YES " + i);
            }
            this.currentCommandIndex = 1;
        });
        middleTextField.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            
            if (code.equals(KeyCode.UP)) {
                if (this.currentCommandIndex == this.pastCommands.size()) {
                    return;
                }
                this.currentCommandIndex += 1;
                middleTextField.setText(this.pastCommands.get(pastCommands.size() - this.currentCommandIndex));
            }
            if (code.equals(KeyCode.DOWN)) {
                if (this.currentCommandIndex == 0) {
                    return;
                }
                this.currentCommandIndex -= 1;
                middleTextField.setText(this.pastCommands.get(pastCommands.size() - this.currentCommandIndex));
            }
        });
        
        pane.setCenter(centerGridPane);
        
                
        Scene scene = new Scene(pane, 650, 600);
        scene.getStylesheets().add("resources/the.css");
        
        primaryStage.setTitle("Pinoccio");
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        if (file.exists()) {
            Scanner input = new Scanner(file);
            key = input.next();
        }
                        
        launch(args);
    }
    
    public void getApiStuff(TextField tf, GridPane gp) {
        try {
                String api = tf.getText();
                this.key = api;
                JsonArray troopsInAccount = pinoccioAPI.troopsInAccount(api);
                JsonArray scoutsInAccount = pinoccioAPI.scoutsInTroop(2, api);
                int troopSize = troopsInAccount.size();
                int scoutSize = scoutsInAccount.size();
                int count = 2;
                if (gp.getChildren().size() == 3) {
                    System.out.println(gp.getChildren().get(2).toString());
                    gp.getChildren().remove(2);
                }
                for(int x = 0; x < troopSize; x++){
                    JsonObject troop = troopsInAccount.get(x).getAsJsonObject();
                    String troopName = troop.get("name").getAsString();
                    Label troopLabel = new Label(troopName);
                    troopLabel.setMinWidth(200);
                    troopLabel.setId("troopLabel");
                    gp.add(troopLabel, 0, count);
                    count++;
                    for(int i = 0; i < scoutSize; i++) {
                        JsonObject scout = scoutsInAccount.get(i).getAsJsonObject();
                        String name = scout.get("name").getAsString();
                        String troopId = troop.get("id").getAsString();
                        String scoutId = scout.get("id").getAsString();
                        FollowScout followScout = new FollowScout(troopId, scoutId, name, this.key);
                        scouts.add(followScout);
                        System.out.println(scouts.get(scouts.size() - 1).name);
                        Button temp = new Button(name);
                        temp.setId("scoutButton");
                        temp.setMinWidth(200);
                        gp.add(temp, 0, count);
                        
                        temp.setOnAction(e -> {
                            middleTextField.setDisable(false);
                            currentScout = followScout;
                            
                        });
                        
                        count++;
                    }
                }
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
                    out.println(this.key);
                    out.close();
                } catch (IOException e) {
                }     
            } catch (Exception ex) {
                Label temp = new Label("Invalid API Key");
                gp.add(temp, 0, 1);
                Logger.getLogger(FinalProject.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
}
