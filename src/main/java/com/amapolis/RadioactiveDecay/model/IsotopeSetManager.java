package com.amapolis.RadioactiveDecay.model;

import com.amapolis.RadioactiveDecay.model.isotope.Isotope;
import com.amapolis.RadioactiveDecay.model.json.JsonReader;

import java.util.Set;

/**
 * This is a singleton class that insures that the same set of isotopes is used throughout the entire application.
 * Using a singleton makes sure that we only use as much Ram as required and do not create multiple instances for the same data.
 */
public class IsotopeSetManager {
    private static IsotopeSetManager instance;
    private Set<Isotope> isotopeSet;

    public IsotopeSetManager() {
        JsonReader jsonReader = new JsonReader();
        this.isotopeSet = jsonReader.getIsotopeSet();
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
