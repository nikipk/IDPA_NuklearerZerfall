package com.amapolis.RadioactiveDecay.controller;

import com.amapolis.RadioactiveDecay.model.IsotopeSetManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.Set;

import com.amapolis.RadioactiveDecay.model.isotope.Isotope;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChooseIsotopeController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ChooseIsotopeController.class);
    private Set<Isotope> isotopeSet;
    private ObservableList<Isotope> shownOptions;
    private MainWindowController mainWindowController;

    @FXML
    private TableView<Isotope> optionTable;

    @FXML
    private TableColumn<Isotope, String> optionCol;

    @FXML
    private TextField inputField, amountField;

    /**
     * Adds the selected amount of the selected isotope to the main window.
     *
     * @param actionEvent 
     * throws NumberFormatException
     */
    @FXML
    private void selectIsotope(ActionEvent actionEvent) {
        Isotope selectedIsotope = optionTable.getSelectionModel().getSelectedItem();
        //check validity of the isotope selected
        if (selectedIsotope != null) {
            double amount;
            //check validity of amount
            try {
                amount = Double.parseDouble(amountField.getText());
            } catch (NumberFormatException e) {
                MainWindowController.showAlert("Invalid amount", "The value in the amount field couldn't be parsed into a number.");
                return;
            }
            //check validity of amount
            if (amount > 0) {
                //adding the selected amount of the selected isotope to the main window.
                mainWindowController.addIsotopeToTable(selectedIsotope, amount);
                log.info(amount + " atoms of the isotope " + selectedIsotope.getId() + " selected");
                //closing window
                ((Stage) optionTable.getScene().getWindow()).close();
            } else {
                MainWindowController.showAlert("Invalid amount", "The amount of isotopes must be greater than 0.");
            }
        } else {
            MainWindowController.showAlert("No isotope selected", "You have to select an isotope from the option list.");
        }
    }

    /**
     * Updates the option table according to the given input
     */
    @FXML
    private void setOptionTable() {
        //clearing the option table
        shownOptions.clear();
        //get list of isotopes
        ArrayList<Isotope> options = getOptionList(inputField.getText());
        //add isotopes to the option table
        for (Isotope option : options) {
            shownOptions.add(option);
        }
    }
    
    /**
     * Returns a list of all Isotopes that fit the given input
     * @param inputString
     * @return ArrayList <Isotope>
     */
    private ArrayList<Isotope> getOptionList(String inputString) {
        //init ArrayList
        ArrayList<Isotope> optionList = new ArrayList<>();
        //check for -
        if (inputString.toLowerCase().contains("-")) {
            String[] inputs = inputString.toLowerCase().split("-");
            String isotopeName = inputs[0].toLowerCase();
            //check if there is a input after the -
            if (inputs.length > 1) {
                String isotopeNumber = "";
                isotopeNumber = inputs[1].toLowerCase();
                //add every isotope that matches the input
                for (Isotope isotope : isotopeSet) {
                    if ((isotope.getId().toLowerCase().contains(isotopeName) || isotope.getElementName().toLowerCase().contains(isotopeName)) && ((isotope.getNumberNeutrons() + isotope.getNumberProtons() + "").toLowerCase().contains(isotopeNumber))) {
                        optionList.add(isotope);
                    }
                }
            } else {
                //add every isotope that matches the input
                for (Isotope isotope : isotopeSet) {
                    if (isotope.getId().toLowerCase().contains(isotopeName) || isotope.getElementName().toLowerCase().contains(isotopeName)) {
                        optionList.add(isotope);
                    }
                }
            }
        } else {
            //add every isotope that matches the input
            for (Isotope isotope : isotopeSet) {
                if (isotope.getId().toLowerCase().contains(inputString.toLowerCase()) || isotope.getElementName().toLowerCase().contains(inputString.toLowerCase())) {
                    optionList.add(isotope);
                }
            }
        }
        return sortOptionList(optionList);
    }
    
    /**
     * Sorts a given list of Isotopes 
     * @param unsortedIsotopeList
     * @return ArrayList<Isotope>
     */
    private ArrayList<Isotope> sortOptionList(ArrayList<Isotope> unsortedIsotopeList) {
        //init ArrayList
        ArrayList<Isotope> sortedIsotopeList = new ArrayList<>();
        //get the size of the unsorted list
        int size = unsortedIsotopeList.size();
        for (int i = 0; i < size; i++) {
            //get top option
            Isotope topOption = getTopOption(unsortedIsotopeList);
            //add top option to sorted list
            sortedIsotopeList.add(topOption);
            //remove top option from unsorted list
            unsortedIsotopeList.remove(topOption);
        }
        return sortedIsotopeList;
    }
    
    /**
     * Returns the isotope with the lowest mass number from a given list
     * @param options
     * @return Isotope
     */
    private Isotope getTopOption(ArrayList<Isotope> options) {
        //init first list item as top isotope
        Isotope topOption = options.get(0);
        for (Isotope option : options) {
            //check if new isotope is better than current 
            if (option.getAtomicMass() < topOption.getAtomicMass()) {
                //set new top option
                topOption = option;
            }
        }
        return topOption;
    }
    
    /**
     * Initializes the mainWindowController
     * @param mainWindowController 
     */
    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }
    
    /**
     * initializez values
     * @param location
     * @param resources 
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //init all possible Isotopes
        isotopeSet = IsotopeSetManager.getInstance().getIsotopeSet();
        //init option table
        shownOptions = FXCollections.observableArrayList();
        optionTable.setItems(shownOptions);
        optionCol.setCellValueFactory(new PropertyValueFactory<Isotope, String>("id"));
    }
}
