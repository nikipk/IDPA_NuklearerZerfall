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
import com.amapolis.RadioactiveDecay.model.json.JsonReader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ChooseIsotopeController implements Initializable {

    private Set<Isotope> isotopeSet;
    private ObservableList<IsotopeOptionTableElement> shownOptions;

    @FXML
    private TableView<IsotopeOptionTableElement> optionTable;

    @FXML
    private TableColumn<IsotopeOptionTableElement, String> optionCol;

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
    private void setOptionTable(){
        shownOptions.clear();
        try {
            ArrayList<Isotope> options = getOptionList(inputField.getText());
            for(Isotope option: options){
                shownOptions.add(new IsotopeOptionTableElement(option));
            }
        } catch (Exception e){
        }
    }

    @FXML
    private void selectIsotope(ActionEvent ae){
        Isotope selectedIsotope = optionTable.getSelectionModel().getSelectedItem().getOption();
        int amount = Integer.parseInt(amountField.getText());
        System.out.println(amount+" atoms of the isotope "+selectedIsotope.getId()+ " selected");
    }

    private ArrayList<Isotope> getOptionList(String inputString) {
        if (isValidInput(isotopeSet, inputString.toLowerCase())) {
            ArrayList<Isotope> optionList = new ArrayList<>();
            for (Isotope isotope : isotopeSet) {
                if (isotope.getId().toLowerCase().contains(inputString.toLowerCase())) {
                    optionList.add(isotope);
                } else if (isotope.getElementName().toLowerCase().contains(inputString.toLowerCase())) {
                    optionList.add(isotope);
                }
            }
            return sortOptionList(optionList);
        }
        return null;
    }


    private boolean isValidInput(Set<Isotope> isotopeSet, String inputString) {
        for (Isotope isotope : isotopeSet) {
            if (isotope.getId().toLowerCase().contains(inputString) || isotope.getElementName().toLowerCase().contains(inputString)) {
                return true;
            }
        }
        return false;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isotopeSet = IsotopeSetManager.getInstance().getIsotopeSet();
        shownOptions = FXCollections.observableArrayList();
        optionTable.setItems(shownOptions);
        optionCol.setCellValueFactory(new PropertyValueFactory<IsotopeOptionTableElement, String>("id"));
    }
}
