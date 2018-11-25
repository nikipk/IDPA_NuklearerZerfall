/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amapolis.RadioactiveDecay.model.json;

import com.amapolis.RadioactiveDecay.model.isotope.DecayType;
import com.amapolis.RadioactiveDecay.model.isotope.Isotope;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amapolis.RadioactiveDecay.model.isotope.StableIsotope;
import com.amapolis.RadioactiveDecay.model.isotope.UnstableIsotope;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * @author Niklas
 */
public class JsonReader {

    private Set<Isotope> isotopeList;
    private ArrayList<String> idList;
    private ArrayList<String> elementNameList;

    public JsonReader() {
        isotopeList = new HashSet<>();
        idList = new ArrayList<>();
        elementNameList = new ArrayList<>();
    }

    /*
    public void scannOldJson() {
        JSONParser parser = new JSONParser();
        try {
            Object rootObject = parser.parse(new FileReader("../IDPA_RadioactiveDecay/src/main/resources/json/isotopes.json"));
            JSONObject root = (JSONObject) rootObject;

            JSONArray isotopeArray = (JSONArray) root.get("nucs");
            JSONArray nameArray = (JSONArray) root.get("elementnames");
            JSONArray shortNameArray = (JSONArray) root.get("elements");
            JSONArray decayArray = (JSONArray) root.get("decays");

            for (int i = 0; i < shortNameArray.size(); i++) {
                idList.add((String) shortNameArray.get(i));
                elementNameList.add((String) nameArray.get(i));
            }

            for (int i = 0; i < isotopeArray.size(); i++) {
                JSONObject isotopeObject = (JSONObject) isotopeArray.get(i);
                if (isotopeObject.containsKey("h")) {
                    int numberNeurons = Integer.parseInt((String) isotopeObject.get("n"));
                    int numberProtons = Integer.parseInt((String) isotopeObject.get("z"));
                    String id = (String) shortNameArray.get(numberProtons);
                    String elementName = (String) nameArray.get(numberProtons);
                    if (((String) isotopeObject.get("h")).equals("stable")) {
                        isotopeList.add(new StableIsotope(id, elementName, numberNeurons, numberProtons));
                        //System.out.println("STABLE, "+id+", "+elementName+", "+numberNeurons+", "+numberProtons);
                    } else {
                        if (isotopeObject.containsKey("dm")) {
                            JSONArray isotopeDecayArray = (JSONArray) isotopeObject.get("dm");
                            JSONObject isotopeDecayType = (JSONObject) isotopeDecayArray.get(0);
                            int decayNumber = Integer.parseInt((String) isotopeDecayType.get("a"));
                            if (!(decayNumber >= 59)) {
                                String decayTypeString = (String) decayArray.get(decayNumber);
                                if (isValidDecayType(decayTypeString)) {
                                    String halfTimeString = (String) isotopeObject.get("h");
                                    System.out.println(halfTimeString);
                                    double test = getHalfTimeFromString(halfTimeString);
                                    isotopeList.add(new UnstableIsotope(id, elementName, numberNeurons, numberProtons, getOldDecayTypeFromString(decayTypeString), getHalfTimeFromString(halfTimeString)));
                                    //System.out.println("UNSTABLE, "+id + ", " + elementName + ", " + numberNeurons + ", " + numberProtons + ", " + decayTypeString + ", " + halfTimeString);
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JsonReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JsonReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(JsonReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("done scanning");
    }

    public boolean isValidDecayType(String decayTypeString) {
        if (decayTypeString.contains("&beta;-")) {
            return true;
        } else if (decayTypeString.contains("&beta;+")) {
            return true;
        } else if (decayTypeString.contains("&alpha;")) {
            return true;
        }
        return false;
    }

    public DecayType getOldDecayTypeFromString(String decayTypeString) {
        if (decayTypeString.contains("&beta;-")) {
            return DecayType.BETA_MINUS;
        } else if (decayTypeString.contains("&beta;+")) {
            return DecayType.BETA_PLUS;
        } else {
            return DecayType.ALPHA;
        }
    }

    public double getHalfTimeFromString(String halfTimeString) {
        String[] values = halfTimeString.split(" ");
        String timeValueString = values[0];
        String timeStampString = values[1];
        //System.out.println(halfTimeString+"   -   "+timeValueString);
        if (timeValueString.contains("E+")) {
            String base = timeValueString.split("E+")[0];
            String exponent = timeValueString.split("E+")[1];
            //System.out.println(Math.pow(Double.parseDouble(base), Double.parseDouble(exponent)));
            return Math.pow(Double.parseDouble(base), Double.parseDouble(exponent));
        } else if (timeValueString.contains("E-")) {
            String base = timeValueString.split("E-")[0];
            String exponent = timeValueString.split("E-")[1];
            //System.out.println(Double.parseDouble(base)/ Math.pow(10, Double.parseDouble(exponent)));
            return Double.parseDouble(base) / Math.pow(10, Double.parseDouble(exponent));
        } else if (timeValueString.contains("E")) {
            String base = timeValueString.split("E")[0];
            String exponent = timeValueString.split("E")[1];
            //System.out.println(Math.pow(Double.parseDouble(base), Double.parseDouble(exponent)));
            return Math.pow(Double.parseDouble(base), Double.parseDouble(exponent));
        } else {
            switch (timeStampString) {
                case "s":
                    return Double.parseDouble(timeValueString);
                case "ms":
                    return Double.parseDouble(timeValueString) / 1000;
                case "?s":
                    return Double.parseDouble(timeValueString) / 1000000;
                case "ns":
                    return Double.parseDouble(timeValueString) / 1000000000;
                case "m":
                    return Double.parseDouble(timeValueString) * 60;
                case "h":
                    return Double.parseDouble(timeValueString) * 3600;
                case "d":
                    return Double.parseDouble(timeValueString) * 86400;
                case "y":
                    return Double.parseDouble(timeValueString) * 31536000;
            }
        }
        return 0.0;
    }

    public void writeNewJson() {
        JSONObject root = new JSONObject();
        JSONArray isotopeArray = new JSONArray();
        JSONArray idArray = new JSONArray();
        JSONArray elementNameArray = new JSONArray();
        for (Isotope isotope : isotopeList) {
            JSONObject isotopeObject = new JSONObject();
            if (isotope.getDecayType().isStable()) {
                isotopeObject.put("d", "S");
            } else {
                if (isotope.getDecayType() == DecayType.BETA_MINUS) {
                    isotopeObject.put("d", "B-");
                } else if (isotope.getDecayType() == DecayType.BETA_PLUS) {
                    isotopeObject.put("d", "B+");
                } else {
                    isotopeObject.put("d", "A");
                }
                double halfTime = ((UnstableIsotope) isotope).getHalfTimeInS();
                isotopeObject.put("h", halfTime);
            }
            isotopeObject.put("z", isotope.getNumberProtons());
            isotopeObject.put("n", isotope.getNumberNeutrons());
            isotopeArray.add(isotopeObject);
        }
        for (String id : idList) {
            idArray.add(id);
        }
        for (String elementName : elementNameList) {
            elementNameArray.add(elementName);
        }
        root.put("isotopes", isotopeArray);
        root.put("ids", idArray);
        root.put("elementnames", elementNameArray);
        try {
            Files.write(Paths.get("../IDPA_RadioactiveDecay/src/main/resources/json/newIsotopes.json"), root.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("done writing");
    }
    */

    public void scannJson() {
        JSONParser parser = new JSONParser();
        try {
            Object rootObject = parser.parse(new FileReader("../IDPA_RadioactiveDecay/src/main/resources/json/newIsotopes.json"));
            JSONObject root = (JSONObject) rootObject;

            JSONArray isotopeArray = (JSONArray) root.get("isotopes");
            JSONArray nameArray = (JSONArray) root.get("elementnames");
            JSONArray shortNameArray = (JSONArray) root.get("ids");

            for (int i = 0; i < shortNameArray.size(); i++) {
                idList.add((String) shortNameArray.get(i));
                elementNameList.add((String) nameArray.get(i));
            }

            for (int i = 0; i < isotopeArray.size(); i++) {
                JSONObject isotopeObject = (JSONObject) isotopeArray.get(i);
                int numberProtons = Math.toIntExact((long) isotopeObject.get("z"));
                int numberNeurons = Math.toIntExact((long) isotopeObject.get("n"));
                if (((String) isotopeObject.get("d")).equals("S")) {
                    isotopeList.add(new StableIsotope(((String) shortNameArray.get(numberProtons)), ((String) nameArray.get(numberProtons)), numberNeurons, numberProtons));
                } else {
                    isotopeList.add(new UnstableIsotope(((String) shortNameArray.get(numberProtons)), ((String) nameArray.get(numberProtons)), numberNeurons, numberProtons, getDecayTypeFromString((String) isotopeObject.get("d")), (double) isotopeObject.get("h")));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JsonReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JsonReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(JsonReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("done scanning Isotopes");
    }

    public DecayType getDecayTypeFromString(String decayTypeString) {
        if (decayTypeString.contains("B-")) {
            return DecayType.BETA_MINUS;
        } else if (decayTypeString.contains("B+")) {
            return DecayType.BETA_PLUS;
        } else {
            return DecayType.ALPHA;
        }
    }

    public void giveEmergingIsotopes() {
        for (Isotope isotope : isotopeList) {
            if (!(isotope.getDecayType() == DecayType.STABLE)) {
                int numberNeuronsEmergingIsotope;
                int numberProtonsEmergingIsotope;
                if (isotope.getDecayType() == DecayType.BETA_MINUS) {
                    numberNeuronsEmergingIsotope = isotope.getNumberNeutrons() - 1;
                    numberProtonsEmergingIsotope = isotope.getNumberProtons() + 1;
                } else if (isotope.getDecayType() == DecayType.BETA_PLUS) {
                    numberNeuronsEmergingIsotope = isotope.getNumberNeutrons() + 1;
                    numberProtonsEmergingIsotope = isotope.getNumberProtons() - 1;
                } else {
                    numberNeuronsEmergingIsotope = isotope.getNumberNeutrons() - 2;
                    numberProtonsEmergingIsotope = isotope.getNumberProtons() - 2;
                }
                for (Isotope emergingIsotope : isotopeList) {
                    if(emergingIsotope.getNumberNeutrons() == numberNeuronsEmergingIsotope && emergingIsotope.getNumberProtons() == numberProtonsEmergingIsotope){
                        ((UnstableIsotope)isotope).setEmergingIsotope(emergingIsotope);
                    }
                }
            }
        }
        System.out.println("done giving emerging isotopes");
    }

    public void printDecayTrace(){
        int counter = 0;
        System.out.println("started printing");
        for(Isotope isotope: isotopeList){
            if(!(isotope.getDecayType() == DecayType.STABLE)){
                try {
                    Isotope testIsotope = isotope;
                    System.out.print(testIsotope.getId() + "  >  ");
                    testIsotope = ((UnstableIsotope) testIsotope).getEmergingIsotope();
                    while (!(testIsotope.getDecayType() == DecayType.STABLE)) {
                        System.out.print(testIsotope.getId() + "  >  ");
                        testIsotope = ((UnstableIsotope) testIsotope).getEmergingIsotope();
                    }
                    System.out.println(testIsotope.getId());
                    counter++;
                } catch (Exception e){
                    System.out.println("UNDEFINED");
                }
            }else{
                System.out.println(isotope.getId());
            }
        }
        System.out.println(counter + " possible start isotope");
    }

    public static void main(String[] args) throws Exception {
        JsonReader jsr = new JsonReader();
        jsr.scannJson();
        jsr.giveEmergingIsotopes();
        jsr.printDecayTrace();
        //jsr.writeNewJson();
    }
}
