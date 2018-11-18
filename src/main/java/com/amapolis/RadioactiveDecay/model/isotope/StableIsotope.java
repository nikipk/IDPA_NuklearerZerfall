package com.amapolis.RadioactiveDecay.model.isotope;

/**
 * Class for stable isotopes
 */
public class StableIsotope extends Isotope {
    /**
     * Constructor
     * @param id ("Ga75")
     * @param atomicNumber (31)
     * @param atomicMassInU
     * @param decayType
     */
    public StableIsotope(String id, int atomicNumber, double atomicMassInU, DecayType decayType) {
        super(id, atomicNumber, atomicMassInU, decayType);
    }
}
