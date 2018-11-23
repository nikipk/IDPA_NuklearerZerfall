package com.amapolis.RadioactiveDecay.controller;

import com.amapolis.RadioactiveDecay.MainApp;
import com.amapolis.RadioactiveDecay.model.isotope.DecayType;
import com.amapolis.RadioactiveDecay.model.isotope.Isotope;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;

public class MainWindowController {
    private static final Logger log = LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    TableView<Isotope> isotopeTable;

    @FXML
    TableColumn<Isotope, String> idCol, elementNameCol, colorCol;

    @FXML
    TableColumn<Isotope, Integer> electronsProtonsCol, neutronsCol, massCol;

    @FXML
    TableColumn<Isotope, Double> amountCol;

    @FXML
    TableColumn<Isotope, DecayType> decayTypeCol;

    @FXML
    public void handleButtonAdd(ActionEvent ae){
        log.info("Add button clicked!");
    }

    @FXML
    public void handleButtonDelete(ActionEvent ae){
        log.info("Delete button clicked!");
    }

    @FXML
    public void handleButtonCalculateExact(ActionEvent ae){
        log.info("CalculateExact button clicked!");
    }

    @FXML
    public void handleButtonCalculateApproach(ActionEvent ae){
        log.info("CalculateApproach button clicked!");
    }

    @FXML
    public void handleButtonClearGraph(ActionEvent ae){
        log.info("ClearGraph button clicked!");
    }
}
