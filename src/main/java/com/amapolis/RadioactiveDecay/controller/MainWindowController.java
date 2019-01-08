package com.amapolis.RadioactiveDecay.controller;

import com.amapolis.RadioactiveDecay.model.DecayCalculator;
import com.amapolis.RadioactiveDecay.model.exception.InvalidIsotopeException;
import com.amapolis.RadioactiveDecay.model.exception.NegativeIsotopeAmountInApproachCalculationException;
import com.amapolis.RadioactiveDecay.model.isotope.Isotope;
import com.amapolis.RadioactiveDecay.model.utils.TimeCalc;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class MainWindowController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(MainWindowController.class);
    private ObservableList<IsotopeTableElement> isotopesInTable;
    private Map<Isotope, XYChart.Data> isotopeBarXYCharts;
    private Thread backgroundThread;
    private boolean threadPaused;

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
    private BarChart barChart;

    @FXML
    private TextField precisionLevel, timeoutInNs, timeoutInMs, zeroTolerance;

    @FXML
    private CheckBox animated;

    /**
     * Handles the "Add Isotope" - Button action. Opens a new window where the user can select an isotope.
     * @param ae
     * @throws IOException
     */
    @FXML
    private void handleButtonAdd(ActionEvent ae) throws IOException {
        log.info("Add button clicked!");

        //open new window to select the isotope
        Stage stage = new Stage();
        String fxmlFile = "/fxml/ChooseIsotope.fxml";
        log.debug("Loading FXML for isotope selection view from: {}", fxmlFile);
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = loader.load(getClass().getResourceAsStream(fxmlFile));
        ChooseIsotopeController chooseIsotopeController = loader.getController();
        chooseIsotopeController.setMainWindowController(this);

        log.debug("Showing JFX scene");
        Scene scene = new Scene(rootNode);

        stage.setTitle("Radioactive decay calculator");
        //Set application icon
        stage.getIcons().add(new Image("/images/logo.png"));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles the "Delete Isotope" - Button action. Removes the selected isotope from the table.
     * @param ae
     */
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

    /**
     * Handles the "Calculate decay exact" - Button. Starts the exact decay calculation for the isotopes in the list.
     * @param ae
     */
    @FXML
    //Suppress Intellij intern waring due to having similar code for the exact method
    @SuppressWarnings("Duplicates")
    private void handleButtonCalculateExact(ActionEvent ae) {
        log.info("CalculateExact button clicked!");
        resetBackgroundThread();
        try {
            DecayCalculator decayCalculator = new DecayCalculator();

            //get declared zeroToleranceValue
            double zeroToleranceValue;
            try {
                zeroToleranceValue = Double.parseDouble(zeroTolerance.getText());
            } catch (NumberFormatException nfx) {
                showAlert("Zero tolerance value error", "The value in the zero tolerance field couldn't be parsed into a number.");
                return;
            }
            decayCalculator.setZeroTolerance(zeroToleranceValue);

            //atomic Objects because they are used in other threads => threadsafe
            //get declared precision level
            final AtomicInteger precision;
            try {
                precision = new AtomicInteger(Integer.parseInt(precisionLevel.getText()));
                if(precision.get() <= 0){
                    showAlert("Precision level error", "The value in the precision field must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException nfe) {
                showAlert("Precision level error", "The value in the precision field couldn't be parsed into a number.");
                return;
            }

            //get declared timeout between time step
            final AtomicInteger timeStepInMs;
            final AtomicInteger timeStepInNs;
            try {
                timeStepInMs = new AtomicInteger(Integer.parseInt(timeoutInMs.getText()));
                timeStepInNs = new AtomicInteger(Integer.parseInt(timeoutInNs.getText()));
            } catch (NumberFormatException nfe) {
                showAlert("Time step error", "The value in the time step field couldn't be parsed into a number.");
                return;
            }

            if (timeStepInMs.doubleValue() + timeStepInNs.doubleValue() / 1000 < 0.1) {
                showAlert("Time step error", "The value for the time step must greater than 100ns or displaying issues might occur.");
                return;
            }

            //converts the table isotope element to a map
            Map<Isotope, Double> initialIsotope = tableElementsToMap(isotopesInTable);

            //map must at least contain one isotope
            if (initialIsotope.isEmpty()) {
                showAlert("No isotope added", "You must select at least 1 Isotope.");
                return;
            }

            //get a set with all occurring isotopes (not a isotope two times)
            Set<Isotope> allOccurringIsotopes = decayCalculator.getAllOccurringIsotopes(initialIsotope.keySet());

            //one XYChart.Series for each isotope
            Map<Isotope, XYChart.Series> isotopeSeries = new LinkedHashMap<>();

            //adds for each isotope the same instance to the line chart and the map
            addIsotopeSeriesToLineGraph(allOccurringIsotopes, isotopeSeries);

            setUpBarChart(allOccurringIsotopes);

            //Create new background Task => separation from ui thread
            Task backgroundTask = new Task<Object>() {
                @Override
                //Suppress Intellij intern waring due to having similar code for the approach method
                @SuppressWarnings("Duplicates")
                protected Object call() {
                    try {
                        //This interface gets executed between each time step. => better separation from model/view/controller
                        //keep everything where it belongs.
                        decayCalculator.setIsotopeProgressListener((time, isotopes) -> {
                            //The updateValue function only takes one parameter, so this POJO object was necessary.
                            TimeIsotope isotope = new TimeIsotope(time, isotopes);
                            //update the value for each time step
                            updateValue(isotope);
                            try {
                                //let thread sleep for desired time/desired time step
                                //WARNING this is a bad approach! More about why in the documentation
                                Thread.sleep(timeStepInMs.get(), timeStepInNs.get());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        //Start calculation from decay
                        decayCalculator.getIsotopeTimeLineExact(precision.doubleValue(), initialIsotope);
                    } catch (InvalidIsotopeException e) {
                        e.printStackTrace();
                        showAlert("Invalid isotope exception", "An invalid isotope was detected. Try again or choose an other isotope.");
                    } finally {
                        return null;
                    }
                }
            };
            //Create and start new background thread
            backgroundThread = new Thread(backgroundTask);
            backgroundThread.start();
            //add a listener the the value of the background thread
            setUpdateListenerTimeLineChart(isotopeSeries, backgroundTask);
        } catch (InvalidIsotopeException e) {
            e.printStackTrace();
            showAlert("Invalid isotope exception", "An invalid isotope was detected. Try again or choose an other isotope.");
        }
    }

    /**
     * Handles the "Calculate decay approach" - Button. Starts the approach decay calculation for the isotopes in the list.
     * @param ae
     */
    @FXML
    //Suppress Intellij intern waring due to having similar code for the exact method
    @SuppressWarnings("Duplicates")
    private void handleButtonCalculateApproach(ActionEvent ae) {
        log.info("CalculateApproach button clicked!");
        resetBackgroundThread();
        try {
            DecayCalculator decayCalculator = new DecayCalculator();
            //get declared zeroToleranceValue
            double zeroToleranceValue;
            try {
                zeroToleranceValue = Double.parseDouble(zeroTolerance.getText());
            } catch (NumberFormatException nfx) {
                showAlert("Zero tolerance value error", "The value in the zero tolerance field couldn't be parsed into a number.");
                return;
            }
            decayCalculator.setZeroTolerance(zeroToleranceValue);

            //atomic Objects because they are used in other threads => threadsafe
            //get declared precision level
            final AtomicInteger precision;
            try {
                precision = new AtomicInteger(Integer.parseInt(precisionLevel.getText()));
                if(precision.get() <= 0){
                    showAlert("Precision level error", "The value in the precision field must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException nfe) {
                showAlert("Precision level error", "The value in the precision field couldn't be parsed into a number.");
                return;
            }
            //get declared timeout between time step
            final AtomicInteger timeStepInMs;
            final AtomicInteger timeStepInNs;

            try {
                timeStepInMs = new AtomicInteger(Integer.parseInt(timeoutInMs.getText()));
                timeStepInNs = new AtomicInteger(Integer.parseInt(timeoutInNs.getText()));
            } catch (NumberFormatException nfe) {
                showAlert("Time step error", "The value in the time step field couldn't be parsed into a number.");
                return;
            }

            if (timeStepInMs.doubleValue() + timeStepInNs.doubleValue() / 1000 < 0.1) {
                showAlert("Time step error", "The value for the time step must greater than 100ns or displaying issues might occur.");
                return;
            }

            //converts the table isotope element to a map
            Map<Isotope, Double> initialIsotope = tableElementsToMap(isotopesInTable);

            //map must at least contain one isotope
            if (initialIsotope.isEmpty()) {
                showAlert("No isotope added", "You must select at least 1 Isotope.");
                return;
            }

            //get a set with all occurring isotopes (not a isotope two times)
            Set<Isotope> allOccurringIsotopes = decayCalculator.getAllOccurringIsotopes(initialIsotope.keySet());

            //one XYChart.Series for each isotope
            Map<Isotope, XYChart.Series> isotopeSeries = new LinkedHashMap<>();

            //adds for each isotope the same instance to the line chart and the map
            addIsotopeSeriesToLineGraph(allOccurringIsotopes, isotopeSeries);

            setUpBarChart(allOccurringIsotopes);

            //Create new background Task => separation from ui thread
            Task backgroundTask = new Task<Object>() {
                @Override
                //Suppress Intellij intern waring due to having similar code for the exact method
                @SuppressWarnings("Duplicates")
                protected Object call() {
                    try {
                        //This interface gets executed between each time step. => better separation from model/view/controller
                        //keep everything where it belongs.
                        decayCalculator.setIsotopeProgressListener((time, isotopes) -> {
                            //The updateValue function only takes one parameter, so this POJO object was necessary.
                            TimeIsotope isotope = new TimeIsotope(time, isotopes);
                            //update the value for each time step
                            updateValue(isotope);
                            try {
                                //let thread sleep for desired time/desired time step
                                //WARNING this is a bad approach! More about why in the documentation
                                Thread.sleep(timeStepInMs.get(), timeStepInNs.get());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        //Start calculation from decay
                        decayCalculator.getIsotopeTimeLineApproach(precision.doubleValue(), initialIsotope);
                    } catch (InvalidIsotopeException e) {
                        e.printStackTrace();
                        showAlert("Invalid isotope exception", "An invalid isotope was detected. Try again or choose an other isotope.");
                    } catch (NegativeIsotopeAmountInApproachCalculationException e) {
                        e.printStackTrace();
                        showAlert("Negative isotope exception", "A negative amount of isotopes was detected. Try again, use the exact method or choose an other isotope.");
                    } finally {
                        return null;
                    }
                }
            };
            //Create and start new background thread
            backgroundThread = new Thread(backgroundTask);
            backgroundThread.start();
            //add a listener the the value of the background thread
            setUpdateListenerTimeLineChart(isotopeSeries, backgroundTask);
        } catch (InvalidIsotopeException e) {
            e.printStackTrace();
            showAlert("Invalid isotope exception", "An invalid isotope was detected. Try again or choose an other isotope.");
        }
    }

    /**
     * adds for each isotope the same instance to the line chart and the map
     *
     * @param allOccurringIsotopes
     * @param isotopeSeries
     */
    private void addIsotopeSeriesToLineGraph(Set<Isotope> allOccurringIsotopes, Map<Isotope, XYChart.Series> isotopeSeries) {
        for (Isotope isotope : allOccurringIsotopes) {
            XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
            series.setName(isotope.getId());
            lineChart.getData().add(series);
            lineChart.setAnimated(animated.isSelected());
            isotopeSeries.put(isotope, series);
        }
    }

    /**
     * Handles the "Clear graphs" - Button. This button clears all graphs and resets the calculation background thread.
     * @param ae
     */
    @FXML
    private void handleButtonClearGraph(ActionEvent ae) {
        log.info("ClearGraph button clicked!");
        resetBackgroundThread();
        lineChart.getData().clear();
        resetBarChart();
    }

    /**
     * Handles the "Pause/Continue" - Button. Pauses or continues the background calculation thread.
     * @param ae
     */
    @FXML
    private void handleButtonPause(ActionEvent ae) {
        log.info("Pause button clicked!");
        if (backgroundThread != null) {
            if (!threadPaused) {
                threadPaused = true;
                backgroundThread.suspend();
            } else {
                threadPaused = false;
                backgroundThread.resume();
            }
        }
    }

    /**
     * set initial settings
     * @param location
     * @param resources
     */
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
        decayTypeCol.setCellValueFactory(new PropertyValueFactory<IsotopeTableElement, String>("decayType"));

        //make tooltips appear faster
        hackTooltipStartTiming();
    }

    /**
     * General method to show an alert
     * @param title
     * @param message
     */
    public static void showAlert(String title, String message) {
        log.error("An error message was shown, \"" + title + "\", \"" + message + "\"");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/images/logo.png"));
        alert.setTitle("Error!");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * add an isotope from outside. Here needed for the "ChooseIsotopeWindow".
     * @param isotope
     * @param amount
     */
    public void addIsotopeToTable(Isotope isotope, double amount) {
        isotopesInTable.add(new IsotopeTableElement(isotope, amount));
    }

    /**
     * This method converts isotope table elements from the tableView to a Map.
     * The map only contains each isotope once and adds up the amount-values.
     * @param isotopeTableElements
     * @return Map<Isotope, Double>
     */
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

    /**
     * Set a change listener to the value in between a time step.
     * @param isotopeSeries
     * @param backgroundTask
     */
    private void setUpdateListenerTimeLineChart(Map<Isotope, XYChart.Series> isotopeSeries, Task backgroundTask) {
        backgroundTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                TimeIsotope timeIsotope = (TimeIsotope) newValue;
                barChart.setTitle("Isotopes at: " + TimeCalc.getTimeAsString(timeIsotope.getTime()));
                for (Isotope iso : timeIsotope.getIsotopes().keySet()) {
                    isotopeSeries.get(iso).getData().add(new XYChart.Data<Number, Number>(timeIsotope.getTime(), timeIsotope.getIsotopes().get(iso)));
                    isotopeBarXYCharts.get(iso).setYValue(timeIsotope.getIsotopes().get(iso));
                }
            }
        });
    }

    /**
     * This method sets up the bar chart for the next decay calculation.
     * @param allOccurringIsotopes
     */
    private void setUpBarChart(Set<Isotope> allOccurringIsotopes) {
        barChart.getData().clear();
        isotopeBarXYCharts = new LinkedHashMap<>();
        XYChart.Series barChartSeries = new XYChart.Series();
        barChartSeries.setName("Isotopes");
        for (Isotope iso : allOccurringIsotopes) {
            XYChart.Data tmpChartData = new XYChart.Data<>(iso.getId(), 0);
            isotopeBarXYCharts.put(iso, tmpChartData);
            barChartSeries.getData().add(tmpChartData);
        }
        barChart.getData().add(barChartSeries);
        barChart.setAnimated(false);
    }

    /**
     * resets the background thread.
     */
    private void resetBackgroundThread() {
        threadPaused = false;
        if (backgroundThread != null) {
            backgroundThread.stop();
        }
    }

    /**
     * Reset the bar chart => is empty after this method
     */
    private void resetBarChart() {
        barChart.setTitle("Amount isotopes at given time");
        barChart.getData().clear();
    }

    /**
     * This function is from StackOverFlow and makes tooltips show to the desired time. Does only have to be used once for entire application.
     * https://stackoverflow.com/questions/26854301/how-to-control-the-javafx-tooltips-delay
     */
    public static void hackTooltipStartTiming() {
        try {
            Tooltip tooltip = new Tooltip();
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(50)));

            Field fieldTimer2 = objBehavior.getClass().getDeclaredField("hideTimer");
            fieldTimer2.setAccessible(true);
            Timeline objTimer2 = (Timeline) fieldTimer2.get(objBehavior);

            objTimer2.getKeyFrames().clear();
            objTimer2.getKeyFrames().add(new KeyFrame(new Duration(100000)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
