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

    Set<Isotope> simpleIsotopeList;
    Set<Isotope> isotopeList;

    public JsonReader() {
        simpleIsotopeList = new HashSet<>();
        isotopeList = new HashSet<>();
    }

    public void scannJson() {
        JSONParser parser = new JSONParser();

        try {
            Object rootObject = parser.parse(new FileReader("D:\\IDPA_NuclearDecay\\IDPA_RadioactiveDecay\\src\\main\\resources\\json\\isotopes.json"));
            JSONObject root = (JSONObject) rootObject;

            JSONArray isotopeArray = (JSONArray) root.get("nucs");
            JSONArray nameArray = (JSONArray) root.get("elementnames");
            JSONArray shortNameArray = (JSONArray) root.get("elements");
            JSONArray decayArray = (JSONArray) root.get("decays");

            for(int i = 0;i < isotopeArray.size();i++) {
                JSONObject isotopeObject = (JSONObject) isotopeArray.get(i);
                if (isotopeObject.containsKey("h")) {
                    int numberNeurons = Integer.parseInt((String) isotopeObject.get("n"));
                    int numberProtons = Integer.parseInt((String) isotopeObject.get("z"));
                    String id = (String) shortNameArray.get(numberProtons);
                    String elementName = (String) nameArray.get(numberProtons);
                    if (((String) isotopeObject.get("h")).equals("stable")) {
                        simpleIsotopeList.add(new StableIsotope(id, elementName, numberNeurons, numberProtons));
                        System.out.println(id+", "+elementName+", "+numberNeurons+", "+numberProtons);
                    } else {
                        if(isotopeObject.containsKey("dm")) {
                            JSONArray isotopeDecayArray = (JSONArray) isotopeObject.get("dm");
                            JSONObject isotopeDecayType = (JSONObject) isotopeDecayArray.get(0);
                            int decayNumber = Integer.parseInt((String) isotopeDecayType.get("a"));
                            if (!(decayNumber >= 59)) {
                                String decayTypeString = (String) decayArray.get(decayNumber);
                                String halfTimeString = (String) isotopeObject.get("h");
                                simpleIsotopeList.add(new UnstableIsotope(id, elementName, numberNeurons, numberProtons, getDecayTypeFromString(decayTypeString), getHalfTimeFromString(halfTimeString)));
                                System.out.println(id + ", " + elementName + ", " + numberNeurons + ", " + numberProtons+ ", " +decayTypeString+ ", "+halfTimeString);
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
    }

    public DecayType getDecayTypeFromString(String decayTypeString){
        return null;
    }

    public double getHalfTimeFromString(String halfTimeString){
        return 0.0;
    }


    public static void main(String[] args) throws Exception {
        JsonReader jsr = new JsonReader();
        jsr.scannJson();
    }
}
