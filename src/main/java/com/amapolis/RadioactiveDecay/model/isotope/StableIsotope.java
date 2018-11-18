package com.amapolis.RadioactiveDecay.model.isotope;

/**
 * Class for stable isotopes
 */
public class StableIsotope extends Isotope {

    /**
     * Constructor
     * @param id (ex. "As75")
     * @param atomicNumber (ex. 33)
     * @param atomicMass (ex. 75)
     * @param decayType (ex. DecayType.STABLE)
     */
    public StableIsotope(String id, int atomicNumber, int atomicMass, DecayType decayType) {
        super(id, atomicNumber, atomicMass, decayType);
    }
}
