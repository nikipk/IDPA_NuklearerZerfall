package com.amapolis.RadioactiveDecay.model;

import com.amapolis.RadioactiveDecay.MainApp;
import com.amapolis.RadioactiveDecay.model.isotope.Isotope;
import com.amapolis.RadioactiveDecay.model.json.JsonFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * This is a singleton class that insures that the same set of isotopes is used throughout the entire application.
 * Using a singleton makes sure that we only use as much Ram as required and do not create multiple instances for the same data.
 */
public class IsotopeSetManager {
    private static final Logger log = LoggerFactory.getLogger(IsotopeSetManager.class);
    private static IsotopeSetManager instance;
    private Set<Isotope> isotopeSet;

    public IsotopeSetManager() {
        log.info("Loading isotopes from json file...");
        JsonFormatter jsonReader = new JsonFormatter();
        this.isotopeSet = jsonReader.getIsotopeSet();
        log.info("Done!");
        //initially load all isotopes from the json file and create a reference so that it doesn't get collected by the garbage collector
        instance = this;
    }

    public static IsotopeSetManager getInstance() {
        if (instance == null) {
            return new IsotopeSetManager();
        } else {
            return instance;
        }
    }

    public Set<Isotope> getIsotopeSet() {
        return isotopeSet;
    }
}
