package com.amapolis.RadioactiveDecay;

import com.amapolis.RadioactiveDecay.model.IsotopeSetManager;
import com.amapolis.RadioactiveDecay.model.isotope.Isotope;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class MainApp extends Application {
    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        log.info("Starting radioactive decay calculator");

        String fxmlFile = "/fxml/MainWindow.fxml";
        log.debug("Loading FXML for main view from: {}", fxmlFile);
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));

        log.debug("Showing JFX scene");
        Scene scene = new Scene(rootNode);

        stage.setTitle("Radioactive decay calculator");
        stage.setScene(scene);
        //Set application icon
        stage.getIcons().add(new Image("/images/logo.png"));
        //close entire Application when clicking on exit (red cross in windows)
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.show();
    }
}
