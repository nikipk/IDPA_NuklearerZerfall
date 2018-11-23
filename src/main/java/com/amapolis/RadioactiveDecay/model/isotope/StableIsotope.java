package com.amapolis.RadioactiveDecay.model.isotope;

/**
 * Class for stable isotopes
 */
public class StableIsotope extends Isotope {

    /**
     * Constructor
     * @param id (ex. "As75")
     * @param elementName (ex. Asodium)
     * @param numberNeutrons (ex. 7)
     * @param numberProtons (ex. 14)
     */
    public StableIsotope(String id, String elementName, int numberNeutrons, int numberProtons) {
        super(id, elementName, numberNeutrons, numberProtons, DecayType.STABLE);
    }
}
