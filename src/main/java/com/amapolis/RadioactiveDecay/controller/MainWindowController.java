package com.amapolis.RadioactiveDecay.controller;

import com.amapolis.RadioactiveDecay.model.DecayCalculator;
import com.amapolis.RadioactiveDecay.model.IsotopeProgressListener;
import com.amapolis.RadioactiveDecay.model.exception.InvalidIsotopeException;
import com.amapolis.RadioactiveDecay.model.exception.NegativeIsotopeAmountInApproachCalculationException;
import com.amapolis.RadioactiveDecay.model.isotope.Isotope;
import com.amapolis.RadioactiveDecay.model.utils.TimeCalc;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class MainWindowController implements Initializable {
    //todo clicking on eXit should also close the background thread
    //todo error when nothing selected
    private static final Logger log = LoggerFactory.getLogger(MainWindowController.class);
    private ObservableList<IsotopeTableElement> isotopesInTable;
    private Map<Isotope, XYChart.Data> isotopeBarXYCharts;
    private Thread backgroundThread;

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
    private TextField precisionLevel, timeoutInNs, timeoutInMs;

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
        resetBackgroundThread();
        try {
            DecayCalculator decayCalculator = new DecayCalculator();

            //get declared precision level //todo can maybe be shorted
            final AtomicInteger precision = new AtomicInteger(Integer.parseInt(precisionLevel.getText()));
            //get declared timeout between timestep
            final AtomicInteger timeStepInMs = new AtomicInteger(Integer.parseInt(timeoutInMs.getText()));
            final AtomicInteger timeStepInNs = new AtomicInteger(Integer.parseInt(timeoutInNs.getText()));

            //converts the table isotope element to a map
            Map<Isotope, Double> initialIsotope = tableElementsToMap(isotopesInTable);
            //get a set with all occurring isotopes (not a isotope two times)
            Set<Isotope> allOccurringIsotopes = decayCalculator.getAllOccurringIsotopes(initialIsotope.keySet());

            //one XYChart.Series for each isotope
            Map<Isotope, XYChart.Series> isotopeSeries = new LinkedHashMap<>();

            //adds for each isotope the same instance to the line chart and the map
            addIsotopeSeriesToGraph(allOccurringIsotopes, isotopeSeries);

            //todo copy for approach
            setUpBarChart(allOccurringIsotopes);

            Task backgroundTask = new Task<Object>() {
                @Override
                protected Object call() {
                    try {
                        decayCalculator.setIsotopeProgressListener(new IsotopeProgressListener() {
                            @Override
                            public void onProgress(double time, Map<Isotope, Double> isotopes) {
                                TimeIsotope isotope = new TimeIsotope(time, isotopes);
                                updateValue(isotope);
                                try {
                                    //todo timeout in ns not sure if this works
                                    //todo mesure time and set difference as timestep
                                    Thread.sleep(timeStepInMs.get(), timeStepInNs.get());
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        decayCalculator.getIsotopeTimeLineExact(precision.doubleValue(), initialIsotope);
                    } catch (InvalidIsotopeException e) {
                        e.printStackTrace();
                        //todo error handling
                    } finally {
                        return null;
                    }
                }
            };
            backgroundThread = new Thread(backgroundTask);
            backgroundThread.start();
            setUpdateListenerTimeLineChart(isotopeSeries, backgroundTask);
        } catch (InvalidIsotopeException e) {
            e.printStackTrace();
            //todo error handling
        }
        /*
        try {
            DecayCalculator decayCalculator = new DecayCalculator();

            Map<Isotope, Double> initialIsotope = tableElementsToMap(isotopesInTable);
            Set<Isotope> allOccurringIsotopes = decayCalculator.getAllOccurringIsotopes(initialIsotope.keySet());

            Map<Isotope, XYChart.Series> isotopeSeries = new LinkedHashMap<>();

            addIsotopeSeriesToGraph(allOccurringIsotopes, isotopeSeries);

            decayCalculator.setIsotopeProgressListener((time, isotopes) -> {
                for (Isotope i : isotopes.keySet()) {
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
            //todo exception handling
            e.printStackTrace();
        }*/
    }

    @FXML
    private void handleButtonCalculateApproach(ActionEvent ae) {
        log.info("CalculateApproach button clicked!");
        resetBackgroundThread();
        try {
            DecayCalculator decayCalculator = new DecayCalculator();

            //get declared precision level //todo can maybe be shorted
            final AtomicInteger precision = new AtomicInteger(Integer.parseInt(precisionLevel.getText()));
            //get declared timeout between timestep
            final AtomicInteger timeStepInMs = new AtomicInteger(Integer.parseInt(timeoutInMs.getText()));
            final AtomicInteger timeStepInNs = new AtomicInteger(Integer.parseInt(timeoutInNs.getText()));

            //converts the table isotope element to a map
            Map<Isotope, Double> initialIsotope = tableElementsToMap(isotopesInTable);
            //get a set with all occurring isotopes (not a isotope two times)
            Set<Isotope> allOccurringIsotopes = decayCalculator.getAllOccurringIsotopes(initialIsotope.keySet());

            //one XYChart.Series for each isotope
            Map<Isotope, XYChart.Series> isotopeSeries = new LinkedHashMap<>();

            //adds for each isotope the same instance to the line chart and the map
            addIsotopeSeriesToGraph(allOccurringIsotopes, isotopeSeries);

            Task backgroundTask = new Task<Object>() {
                @Override
                protected Object call() {
                    try {
                        decayCalculator.setIsotopeProgressListener(new IsotopeProgressListener() {
                            @Override
                            public void onProgress(double time, Map<Isotope, Double> isotopes) {
                                TimeIsotope isotope = new TimeIsotope(time, isotopes);
                                updateValue(isotope);
                                try {
                                    //todo timeout in ns not sure if this works
                                    Thread.sleep(timeStepInMs.get(), timeStepInNs.get());
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        decayCalculator.getIsotopeTimeLineApproach(precision.doubleValue(), initialIsotope);
                    } catch (InvalidIsotopeException e) {
                        e.printStackTrace();
                        //todo error handling
                    } catch (NegativeIsotopeAmountInApproachCalculationException e) {
                        e.printStackTrace();
                        //todo error handling
                    } finally {
                        return null;
                    }
                }
            };
            backgroundThread = new Thread(backgroundTask);
            backgroundThread.start();
            setUpdateListenerTimeLineChart(isotopeSeries, backgroundTask);
        } catch (InvalidIsotopeException e) {
            e.printStackTrace();
            //todo error handling
        }
    }

    /**
     * adds for each isotope the same instance to the line chart and the map
     *
     * @param allOccurringIsotopes
     * @param isotopeSeries
     */
    private void addIsotopeSeriesToGraph(Set<Isotope> allOccurringIsotopes, Map<Isotope, XYChart.Series> isotopeSeries) {
        for (Isotope isotope : allOccurringIsotopes) {
            XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
            series.setName(isotope.getId());
            lineChart.getData().add(series);
            lineChart.setAnimated(false);
            isotopeSeries.put(isotope, series);
        }
    }

    @FXML
    private void handleButtonClearGraph(ActionEvent ae) {
        log.info("ClearGraph button clicked!");
        resetBackgroundThread();
        //todo maybe better method
        //todo cancle running threads
        lineChart.getData().clear();
        barChart.getData().clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Started MainWindowController");

        lineChart.setCreateSymbols(false);

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

    //todo change that normal isotope and amount
    public void addIsotopesToTable(Collection<IsotopeTableElement> isotopesPara) {
        isotopesInTable.addAll(isotopesPara);
    }

    //todo change that normal isotope and amount
    public void addIsotopeToTable(IsotopeTableElement isotopePara) {
        isotopesInTable.add(isotopePara);
    }

    private Map<Isotope, Double> tableElementsToMap(Collection<IsotopeTableElement> isotopeTableElements) {
        Map<Isotope, Double> returnMap = new LinkedHashMap<>();
        for (IsotopeTableElement isotopeTableElement : isotopeTableElements) {
            if (returnMap.containsKey(isotopeTableElement.getIsotope())) {
                returnMap.put(isotopeTableElement.getIsotope(), returnMap.get(isotopeTableElement.getIsotope()) + isotopeTableElement.getAmount());
            } else {
                returnMap.put(isotopeTableElement.getIsotope(), isotopeTableElement.getAmount());
            }
        }
        return returnMap;
    }

    private void setUpdateListenerTimeLineChart(Map<Isotope, XYChart.Series> isotopeSeries, Task backgroundTask) {
        backgroundTask.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue != null) {
                    TimeIsotope timeIsotope = (TimeIsotope) newValue;
                    barChart.setTitle("Isotopes at: " + TimeCalc.getTimeAsString(timeIsotope.getTime()));
                    for (Isotope iso : timeIsotope.getIsotopes().keySet()) {
                        isotopeSeries.get(iso).getData().add(new XYChart.Data<Number, Number>(timeIsotope.getTime(), timeIsotope.getIsotopes().get(iso)));
                        isotopeBarXYCharts.get(iso).setYValue(timeIsotope.getIsotopes().get(iso));
                    }
                }
            }
        });
    }

    private void setUpBarChart(Set<Isotope> allOccurringIsotopes){
        barChart.getData().clear();
        isotopeBarXYCharts = new LinkedHashMap<>();
        XYChart.Series barChartSeries = new XYChart.Series();
        barChartSeries.setName("Isotopes");
        for(Isotope iso: allOccurringIsotopes){
            XYChart.Data tmpChartData = new XYChart.Data<>(iso.getId(), 0);
            isotopeBarXYCharts.put(iso, tmpChartData);
            barChartSeries.getData().add(tmpChartData);
        }
        barChart.getData().add(barChartSeries);
        barChart.setAnimated(false);
        //todo set yaxis max border
    }

    private void resetBackgroundThread(){
        if(backgroundThread != null){
            backgroundThread.stop();
        }
    }

    private void resetBarChart(){
        barChart.setTitle("Amount isotopes at given time");
    }
}
