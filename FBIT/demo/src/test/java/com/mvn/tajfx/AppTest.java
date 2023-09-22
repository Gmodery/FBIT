package com.mvn.tajfx;

import java.time.Duration;
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AppTest extends Application {
    private static ArrayList<String> mainList = new ArrayList<String>();
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String version = "2022.08.20";

    public static void main (String[] args) {
        launch(args);
    }

    private static void search() {
        driver.get("LINK_TO_TRACKING_SITE");

        try {wait.until(ExpectedConditions.presenceOfElementLocated(By.name("iitraks")));}
        catch (Exception ex) {driver.quit(); System.exit(0);}
        
        WebElement trackingBox = driver.findElement(By.name("iitraks"));
        for (String trk : mainList) {
            trackingBox.sendKeys(trk + '\n');
        }
        driver.findElement(By.name("submit")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/form[1]/table[1]/tbody/tr[4]/td[1]")));
        System.out.println("\n\nReady to examine tracking IDs \n");
        String xpath1 = "/html/body/form[";
        String xpath2 = "]/table[2]/tbody/tr[2]";
        
        ArrayList<String> goodList = new ArrayList<String>();
        Boolean isValid = true;

        for (int i = 0 ; i < mainList.size() ; i++) {
            String path = xpath1 + (i+1) + xpath2;

            try {
                isValid = driver.findElements(By.xpath(path)).size() > 0;
            } catch (Exception ex) {ex.printStackTrace(); break;}

            if (isValid) {
                goodList.add(mainList.get(i));
            }
        }

        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("LINK_TO_TRACKING_SITE");
        
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("iitraks")));
        } catch (Exception ex) {
            driver.close();
            System.exit(0);
        }

        trackingBox = driver.findElement(By.name("iitraks"));

        for (String trk : goodList) {
            trackingBox.sendKeys(trk + '\n');
        }
        driver.findElement(By.name("submit")).click();

    }

    private static void initializeDriver() {
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
       
        wait = new WebDriverWait(driver, Duration.ofSeconds(360));
    }

    private static ArrayList<String> generateTrackingList(String userTracking, int[] posArray) {
        String val = "";
        String newTracking = "";
        ArrayList<String> list = new ArrayList<String>();

        for (int i = 0, counter = 2 ; i < Math.pow(10, posArray[2]) ; i++) {
            if (i / 10 == 0) val = ("00" + i);
            else if (i / 100 == 0) val = ("0" + i);
            else if (i / 1000 == 0) val = Integer.toString(i);

                for (int j = 0 ; j < userTracking.length() ; j++) {
                    if ((j == posArray[0]) || (j == posArray[1])) {
                        newTracking += val.substring(counter, counter+1);
                        counter--;
                    }
                    else newTracking += userTracking.substring(j, j+1);
                }

            list.add(newTracking);
            newTracking = "";
            counter = 2;
        }

        return list;
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle("FBIT");

        Label titleLabel = new Label("FBIT");
            titleLabel.setFont(new Font("Arial", 36));
            titleLabel.setTranslateX(272);
        Label subTitleLabel = new Label("FedEx Barcode Identification Tool");
            titleLabel.setFont(new Font("Arial", 24));
            subTitleLabel.setTranslateX(209);

        HBox digitBox = new HBox();
        final Button trackButton = new Button("Track");
            trackButton.setPrefSize(75, 30);
            trackButton.setTranslateX(260);
            trackButton.setTranslateY(30);
        Button clearButton = new Button("Clear");
            clearButton.setPrefSize(45, 15);
            clearButton.setTranslateX(275);
            clearButton.setTranslateY(33);
        Button infoButton = new Button("Info");
            infoButton.setPrefSize(37, 15);
            infoButton.setTranslateX(279);
            infoButton.setTranslateY(36);

        VBox layout = new VBox(titleLabel, subTitleLabel, digitBox, trackButton, clearButton, infoButton);

        final Label errorLabel = new Label();
            errorLabel.setWrapText(true);
        final TextField[] tfArray = new TextField[12];
        final boolean[] hasValue = new boolean[12];

        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (TextField tf : tfArray) {
                    tf.clear();
                    tf.setStyle("-fx-border-color: grey ; -fx-border-width: 0.1px ;");
                }
            }
        });

        trackButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                trackButton.setDisable(true);

                String userTracking = "";

                for (int i = 0 ; i < tfArray.length ; i++) {
                    userTracking += tfArray[i].getText();
                }

                boolean hasMissingValue = false;
                for (int i = 0; i < userTracking.length(); i++) {
                    if (userTracking.substring(i, i + 1).equals("*")) {
                        hasMissingValue = true;
                        break;
                    }
                    if (i == userTracking.length()-1) {
                        try {
                            throw new Exception("Too few missing values");
                        } catch (Exception e) {errorLabel.setText("\nPending Error: " + e.getMessage());}
                    }
                }

                if (userTracking.length() == 12 && hasMissingValue) {
                    trackButton.setText("Tracking...");

                    try {
                        int[] posArray = { -1, -1, -1};
                        int c = 0;

                            posArray[0] = -1;
                            posArray[1] = -1;
                            for (int i = 0; i < userTracking.length(); i++) {
                                if (userTracking.substring(i, i + 1).equals("*")) {
                                    if (posArray[1] == -1) {
                                        posArray[c] = i;
                                        c++;
                                    } else {
                                        throw new Exception("Too many missing values");
                                    }
                                }
                            }
                            posArray[2] = c;


                        mainList = generateTrackingList(userTracking, posArray);


                        initializeDriver();

                        try {
                            search();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            driver.close();
                        }
                    
                        trackButton.setDisable(false);
                    } catch(Exception ex) {
                        errorLabel.setText("\nPending Error: " + ex.getMessage());
                        trackButton.setText("Track");
                        trackButton.setDisable(false);
                    }
                }
                else trackButton.setDisable(false);
            }
        });
        
        infoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Label secondLabel = new Label("Driver must be located at: " + System.getProperty("user.dir") + "\\chromedriver.exe");
                    secondLabel.setWrapText(true);
                Label thirdLabel = new Label("\nVersion: " + version);
                    thirdLabel.setWrapText(true);

                VBox secondaryLayout = new VBox();
                secondaryLayout.getChildren().addAll(secondLabel, thirdLabel, errorLabel);

                Scene secondScene = new Scene(secondaryLayout, 400, 150);

                // New window (Stage)
                Stage newWindow = new Stage();
                newWindow.setTitle("Info");
                newWindow.setScene(secondScene);

                // Set position of second window, related to primary window.
                newWindow.setX(primaryStage.getX() + 300);
                newWindow.setY(primaryStage.getY() + 50);

                newWindow.show();
            }
        });

        for (int i = 0, c = 30 ; i < 12 ; i++) {
            final TextField tf = new TextField();
            tf.setPrefSize(35, 50);
            tf.setFont(new Font("Arial", 18));
            tf.setTranslateY(12);
            tf.setTranslateX(c);
            c += 10;
            
            tfArray[i] = tf;
            digitBox.getChildren().add(tf);
        }

        for (int i = 0 ; i < tfArray.length ; i++) {

            final TextField tf = tfArray[i];
            hasValue[i] = false;
            
            final TextField tf2;
            if (!(i == tfArray.length-1)) {tf2 = tfArray[i+1];}
            else tf2 = tf;

            tf.textProperty().addListener(new ChangeListener<String>() { // Stay red until valid entry
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                        if (newValue.length() > 0 && newValue.charAt(0) == '*') {
                            tf.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");
                                for (int j = 0 ; j < tfArray.length ; j++) {
                                    if (tfArray[j] == tf) {
                                        hasValue[j] = true;
                                        break;
                                    }
                                }
                                // trackButton.setDisable(false);
                            tf2.requestFocus();
                        }
                        else if (newValue.length() > 0 && (newValue.charAt(0) < 48 || newValue.charAt(0) > 57)) { // Not a number
                            tf.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                            tf.setText(oldValue);
                            for (int j = 0; j < tfArray.length; j++) {
                            if (tfArray[j] == tf) {
                                hasValue[j] = false;
                                break;
                            }
                        }
                        } 
                        else if (newValue.length() > 0) {
                            tf.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");
                            tf2.requestFocus();
                            for (int j = 0; j < tfArray.length; j++) {
                                if (tfArray[j] == tf) {
                                    hasValue[j] = true;
                                    break;
                                }
                            }
                            // trackButton.setDisable(false);
                        }
                        
                        if (newValue.length() > 1) {
                            tf.setText(oldValue);
                        }
                    

                }
            });
        }

        final Scene scene = new Scene(layout, 600, 215);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        try {driver.quit();} catch (Exception ex) {};
        System.exit(0);
    }
}