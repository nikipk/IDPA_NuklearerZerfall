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
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ChooseIsotopeController implements Initializable {

    private Set<Isotope> isotopeSet;
    private ObservableList<Isotope> shownOptions;
    private MainWindowController mainWindowController;

    @FXML
    private TableView<Isotope> optionTable;

    @FXML
    private TableColumn<Isotope, String> optionCol;

    @FXML
    private TextField inputField, amountField;

    /*
    public ChooseIsotopeController() {
        isotopeSet = new JsonReader().getIsotopeSet();

        double start = System.currentTimeMillis();
        ArrayList<Isotope> testList = getOptionList("u");
        System.out.println("t1: "+(System.currentTimeMillis()-start));
        start = System.currentTimeMillis();
        ArrayList<Isotope> testList1 = getOptionList("ir");
        System.out.println("t2: "+(System.currentTimeMillis()-start));
        start = System.currentTimeMillis();
        ArrayList<Isotope> testList2 = getOptionList("tr");
        System.out.println("t3: "+(System.currentTimeMillis()-start));
        start = System.currentTimeMillis();
        ArrayList<Isotope> testList3 = getOptionList("FE");
        System.out.println("t4: "+(System.currentTimeMillis()-start));
        start = System.currentTimeMillis();
        ArrayList<Isotope> testList4 = getOptionList("34");
        System.out.println("t3: "+(System.currentTimeMillis()-start));

        System.out.println("done");
    }
     */
    @FXML
    private void selectIsotope(ActionEvent ae) {
        double amount = 0;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (Exception e) {
        }
        if (amountField.getText() != "") {
            if (amount > 0) {
                Isotope selectedIsotope = optionTable.getSelectionModel().getSelectedItem();
                if (selectedIsotope != null) {
                    mainWindowController.addIsotopeToTable(selectedIsotope, amount);
                    System.out.println(amount + " atoms of the isotope " + selectedIsotope.getId() + " selected");
                    ((Stage) optionTable.getScene().getWindow()).close();
                } else {
                    showAlert("invalid isotope", "An isotope has to be selected.");
                }
            } else {
                showAlert("invalid amount", "The amount of isotopes should be greater than 0.");
            }
        } else {
            showAlert("invalid amount", "The amount of isotopes should be greater than 0.");
        }
    }

    @FXML
    private void setOptionTable() {
        shownOptions.clear();
        try {
            ArrayList<Isotope> options = getOptionList(inputField.getText());
            for (Isotope option : options) {
                shownOptions.add(option);
            }
        } catch (Exception e) {
        }
    }

    private ArrayList<Isotope> getOptionList(String inputString) {
        ArrayList<Isotope> optionList = new ArrayList<>();
        if (inputString.toLowerCase().contains("-")) {
            String[] inputs = inputString.toLowerCase().split("-");
            String isotopeName = inputs[0].toLowerCase();
            if (inputs.length > 1) {
                String isotopeNumber = "";
                isotopeNumber = inputs[1].toLowerCase();
                for (Isotope isotope : isotopeSet) {
                    if ((isotope.getId().toLowerCase().contains(isotopeName) || isotope.getElementName().toLowerCase().contains(isotopeName)) && ((isotope.getNumberNeutrons() + isotope.getNumberProtons() + "").toLowerCase().contains(isotopeNumber))) {
                        optionList.add(isotope);
                    }
                }
            } else {
                for (Isotope isotope : isotopeSet) {
                    if (isotope.getId().toLowerCase().contains(isotopeName) || isotope.getElementName().toLowerCase().contains(isotopeName)) {
                        optionList.add(isotope);
                    }
                }
            }
        } else {
            for (Isotope isotope : isotopeSet) {
                if (isotope.getId().toLowerCase().contains(inputString.toLowerCase()) || isotope.getElementName().toLowerCase().contains(inputString.toLowerCase())) {
                    optionList.add(isotope);
                }
            }
        }
        return sortOptionList(optionList);
    }

    private ArrayList<Isotope> sortOptionList(ArrayList<Isotope> unsortedIsotopeList) {
        ArrayList<Isotope> sortedIsotopeList = new ArrayList<>();
        int size = unsortedIsotopeList.size();
        for (int i = 0; i < size; i++) {
            Isotope topOption = getTopOption(unsortedIsotopeList);
            sortedIsotopeList.add(topOption);
            unsortedIsotopeList.remove(topOption);
        }
        return sortedIsotopeList;
    }

    private Isotope getTopOption(ArrayList<Isotope> options) {
        Isotope topOption = options.get(0);
        for (Isotope option : options) {
            if (option.getAtomicMass() < topOption.getAtomicMass()) {
                topOption = option;
            }
        }
        return topOption;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.show();
    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isotopeSet = IsotopeSetManager.getInstance().getIsotopeSet();
        shownOptions = FXCollections.observableArrayList();
        optionTable.setItems(shownOptions);
        optionCol.setCellValueFactory(new PropertyValueFactory<Isotope, String>("id"));
    }
}
