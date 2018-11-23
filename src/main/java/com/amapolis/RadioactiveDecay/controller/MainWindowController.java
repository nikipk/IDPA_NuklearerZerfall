package com.amapolis.RadioactiveDecay.controller;

import com.amapolis.RadioactiveDecay.MainApp;
import com.amapolis.RadioactiveDecay.model.isotope.DecayType;
import com.amapolis.RadioactiveDecay.model.isotope.Isotope;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;


public class MainWindowController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    private TableView<Isotope> isotopeTable;

    @FXML
    private TableColumn<Isotope, String> idCol, elementNameCol, colorCol;

    @FXML
    private TableColumn<Isotope, Integer> electronsProtonsCol, neutronsCol, massCol;

    @FXML
    private TableColumn<Isotope, Double> amountCol;

    @FXML
    private TableColumn<Isotope, DecayType> decayTypeCol;

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private Axis<Number> xAxis, yAxis;

    @FXML
    private void handleButtonAdd(ActionEvent ae){
        log.info("Add button clicked!");
    }

    @FXML
    private void handleButtonDelete(ActionEvent ae){
        log.info("Delete button clicked!");
    }

    @FXML
    private void handleButtonCalculateExact(ActionEvent ae){
        log.info("CalculateExact button clicked!");
        lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        series.setName("Amount of Isotopes");
        series.getData().add(new XYChart.Data<Number, Number>(100, 120.0));
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
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Started MainWindowController");
    }
}
