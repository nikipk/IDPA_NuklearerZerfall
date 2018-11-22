package com.amapolis.RadioactiveDecay.model.isotope;

/**
 * Class for unstable isotopes. Contains some more information.
 */
public class UnstableIsotope extends Isotope {
    private double halfTimeInS, decayFactor;
    private Isotope emergingIsotope;
    private double approachValue;

    /**
     * Constructor
     *
     * @param id              (ex: "Ga75")
     * @param atomicNumber    (ex: 31)
     * @param atomicMass      (ex. 44)
     * @param decayType       (ex. DecayType.BETA_MINUS)
     * @param halfTimeInS     (ex 126)
     * @param emergingIsotope (ex Ge75)
     */
    public UnstableIsotope(String id, int atomicNumber, int atomicMass, DecayType decayType, double halfTimeInS, Isotope emergingIsotope) {
        super(id, atomicNumber, atomicMass, decayType);
        this.halfTimeInS = halfTimeInS;
        this.decayFactor = Math.pow(0.5, 1 / halfTimeInS);
        this.emergingIsotope = emergingIsotope;
        this.approachValue = Math.log(2) / halfTimeInS;
    }

    public double getHalfTimeInS() {
        return halfTimeInS;
    }

    public Isotope getEmergingIsotope() {
        return emergingIsotope;
    }

    public double getDecayFactor() {
        return decayFactor;
    }

    public double getApproachValue() {
        return approachValue;
    }
}
