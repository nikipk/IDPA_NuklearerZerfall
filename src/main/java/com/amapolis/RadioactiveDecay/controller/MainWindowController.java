package com.amapolis.RadioactiveDecay.controller;

import com.amapolis.RadioactiveDecay.model.DecayCalculator;
import com.amapolis.RadioactiveDecay.model.isotope.DecayType;
import com.amapolis.RadioactiveDecay.model.isotope.StableIsotope;
import com.amapolis.RadioactiveDecay.model.isotope.UnstableIsotope;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;


public class MainWindowController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(MainWindowController.class);
    private Collection<XYChart.Series<Number,Number>> lineChartSeries;
    private DecayCalculator decayCalculator;
    private ObservableList<IsotopeTableElement> isotopes;

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
    private void handleButtonAdd(ActionEvent ae){
        log.info("Add button clicked!");
        isotopes.add(new IsotopeTableElement(new UnstableIsotope("asd", "sadasd", 12, 12, DecayType.BETA_MINUS, 120.2), 10.0));
        isotopes.add(new IsotopeTableElement(new StableIsotope("asd", "sadasd", 12, 12), 10.0));
    }

    @FXML
    private void handleButtonDelete(ActionEvent ae){
        log.info("Delete button clicked!");
        IsotopeTableElement ite = isotopeTable.getSelectionModel().getSelectedItem();
        //check if something is selected
        if(ite != null) {
            isotopes.remove(ite);
        } else {
            showAlert("Nothing selected!", "Please select the row you would like to delete.");
        }
    }

    @FXML
    private void handleButtonCalculateExact(ActionEvent ae){
        log.info("CalculateExact button clicked!");
        XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        series.setName("Amount of Isotopes");
        series.getData().add(new XYChart.Data<Number, Number>(10, 120.0));
        series.getData().add(new XYChart.Data<Number, Number>(100, 240.0));
        lineChart.getData().add(series);
    }

    @FXML
    private void handleButtonCalculateApproach(ActionEvent ae){
        log.info("CalculateApproach button clicked!");
    }

    @FXML
    private void handleButtonClearGraph(ActionEvent ae){
        log.info("ClearGraph button clicked!");
        //todo maybe better method
        lineChart.getData().setAll();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Started MainWindowController");
        decayCalculator = new DecayCalculator();
        lineChartSeries = new ArrayList<>();
        isotopes = FXCollections.observableArrayList();
        isotopeTable.setItems(isotopes);

        //set TableView
        idCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, String>("id"));
        elementNameCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, String>("elementName"));
        electronsProtonsCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, Integer>("numberProtons"));
        neutronsCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, Integer>("numberNeutrons"));
        massCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, Integer>("atomicMass"));
        halfLifeCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, Double>("halfTimeInS"));
        amountCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, Double>("amount"));
        decayTypeCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, String>("decayType"));

    }

    private void showAlert(String title, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.show();
    }
}
