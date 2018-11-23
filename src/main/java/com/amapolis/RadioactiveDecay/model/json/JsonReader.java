/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amapolis.RadioactiveDecay.model.json;

import com.amapolis.RadioactiveDecay.model.isotope.Isotope;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author Niklas
 */
public class JsonReader {
    
    Set<Isotope> simpleIsotopeList;
    Set<Isotope> isotopeList;

    public JsonReader() {
        
    }
    
    public void scannJson(){
        JSONParser parser = new JSONParser();
        
        try {
            Object rootObject = parser.parse(new FileReader("/jason/isotope.json"));
            JSONObject root = (JSONObject) rootObject;
                    
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JsonReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JsonReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(JsonReader.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
}
