package com.amapolis.RadioactiveDecay;

import com.amapolis.RadioactiveDecay.model.IsotopeSetManager;
import com.amapolis.RadioactiveDecay.model.isotope.Isotope;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class MainApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);
    private Set<Isotope> isotopeSet;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void start(Stage stage) throws Exception {

        log.info("Starting radioactive decay calculator");

        //to initially load all isotopes from the json file and create a reference so that it doesn't get collected by the garbage collector
        //todo might not be nessacry to create a reference here or maybe load it only when needed
        //todo ask mrs. Lüthi
        isotopeSet = IsotopeSetManager.getInstance().getIsotopeSet();

        String fxmlFile = "/fxml/MainWindow.fxml";
        log.debug("Loading FXML for main view from: {}", fxmlFile);
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));

        log.debug("Showing JFX scene");
        Scene scene = new Scene(rootNode);
        //scene.getStylesheets().add("/styles/styles.css");
        //todo set Icon

        stage.setTitle("Radioactive decay calculator");
        stage.setScene(scene);
        stage.show();
    }
}
