package com.amapolis.RadioactiveDecay.controller;

import com.amapolis.RadioactiveDecay.model.DecayCalculator;
import com.amapolis.RadioactiveDecay.model.exception.InvalidIsotopeException;
import com.amapolis.RadioactiveDecay.model.isotope.DecayType;
import com.amapolis.RadioactiveDecay.model.isotope.Isotope;
import com.amapolis.RadioactiveDecay.model.isotope.StableIsotope;
import com.amapolis.RadioactiveDecay.model.isotope.UnstableIsotope;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;


public class MainWindowController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(MainWindowController.class);
    private Collection<XYChart.Series<Number, Number>> lineChartSeries;
    private DecayCalculator decayCalculator;
    private ObservableList<IsotopeTableElement> isotopesInTable;

    @FXML
    private TableView<IsotopeTableElement> isotopeTable;

    @FXML
    private TableColumn<IsotopeTableElement, String> idCol, elementNameCol;

    @FXML
    private TableColumn<IsotopeTableElement, Integer> electronsProtonsCol, neutronsCol, massCol;

    @FXML
    private TableColumn<IsotopeTableElement, Double> halfLifeCol;

    @FXML
    private TableColumn<IsotopeTableElement, Double> amountCol;

    @FXML
    private TableColumn<IsotopeTableElement, String> decayTypeCol;

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private NumberAxis xAxis, yAxis;

    @FXML
    private BarChart barChart;

    @FXML
    private TextField precisionLevel;

    @FXML
    private void handleButtonAdd(ActionEvent ae) throws IOException {
        log.info("Add button clicked!");

        Stage stage = new Stage();
        String fxmlFile = "/fxml/ChooseIsotope.fxml";
        log.debug("Loading FXML for main view from: {}", fxmlFile);
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = loader.load(getClass().getResourceAsStream(fxmlFile));
        ChooseIsotopeController chooseIsotopeController = loader.getController();
        chooseIsotopeController.setMainWindowController(this);

        log.debug("Showing JFX scene");
        Scene scene = new Scene(rootNode);
        //scene.getStylesheets().add("/styles/styles.css");
        //todo set Icon

        stage.setTitle("Radioactive decay calculator");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleButtonDelete(ActionEvent ae) {
        log.info("Delete button clicked!");
        IsotopeTableElement ite = isotopeTable.getSelectionModel().getSelectedItem();
        //check if something is selected
        if (ite != null) {
            isotopesInTable.remove(ite);
        } else {
            showAlert("Nothing selected!", "Please select the row you would like to delete.");
        }
    }

    @FXML
    private void handleButtonCalculateExact(ActionEvent ae) {
        log.info("CalculateExact button clicked!");
        try {
            Map<Isotope, Double> initialIsotope = tableElementsToMap(isotopesInTable);
            Set<Isotope> allOccurringIsotopes = decayCalculator.getAllOccurringIsotopes(initialIsotope.keySet());

            LinkedHashMap<Isotope, XYChart.Series> isotopeSeries = new LinkedHashMap<>();

            for(Isotope isotope:allOccurringIsotopes){
                XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
                series.setName(isotope.getId());
                lineChart.getData().add(series);
                isotopeSeries.put(isotope, series);
            }

            decayCalculator.setIsotopeProgressListener((time, isotopes) -> {
                for (Isotope i:isotopes.keySet()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            isotopeSeries.get(i).getData().add(new XYChart.Data<Number, Number>(time, isotopes.get(i)));
                        }
                    });
                }
            });

            double precision = Double.parseDouble(precisionLevel.getText());
            decayCalculator.getIsotopeTimeLineExact(precision, initialIsotope);

        } catch (InvalidIsotopeException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleButtonCalculateApproach(ActionEvent ae) {
        log.info("CalculateApproach button clicked!");
        try{
        }catch (Exception e){

        }
    }

    @FXML
    private void handleButtonClearGraph(ActionEvent ae) {
        log.info("ClearGraph button clicked!");
        //todo maybe better method
        lineChart.getData().setAll();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Started MainWindowController");
        decayCalculator = new DecayCalculator();
        lineChartSeries = new ArrayList<>();
        isotopesInTable = FXCollections.observableArrayList();
        isotopeTable.setItems(isotopesInTable);

        //set TableView
        idCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, String>("id"));
        elementNameCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, String>("elementName"));
        electronsProtonsCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, Integer>("numberProtons"));
        neutronsCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, Integer>("numberNeutrons"));
        massCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, Integer>("atomicMass"));
        halfLifeCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, Double>("halfTimeInS"));
        amountCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, Double>("amount"));
        //todo make amount editable in table, => doesn't work (disabled edit in fxml too)
        //amountCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        decayTypeCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, String>("decayType"));

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.show();
    }

    public void addIsotopesToTable(Collection<IsotopeTableElement> isotopesPara) {
        isotopesInTable.addAll(isotopesPara);
    }

    public void addIsotopeToTable(IsotopeTableElement isotopePara) {
        isotopesInTable.add(isotopePara);
    }

    private Map<Isotope, Double> tableElementsToMap(Collection<IsotopeTableElement> isotopeTableElements){
        Map<Isotope, Double> returnMap = new LinkedHashMap<>();
        for(IsotopeTableElement isotopeTableElement: isotopeTableElements){
            if(returnMap.containsKey(isotopeTableElement.getIsotope())){
                returnMap.put(isotopeTableElement.getIsotope(), returnMap.get(isotopeTableElement.getIsotope()) + isotopeTableElement.getAmount());
            } else {
                returnMap.put(isotopeTableElement.getIsotope(), isotopeTableElement.getAmount());
            }
        }
        return returnMap;
    }
}
