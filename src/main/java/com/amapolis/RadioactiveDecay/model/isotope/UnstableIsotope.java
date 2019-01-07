package com.amapolis.RadioactiveDecay.model.isotope;

/**
 * Class for unstable isotopes. Contains some more information than stable isotopes.
 */
public class UnstableIsotope extends Isotope {
    
    private double halfTimeInS, decayFactor;
    private Isotope emergingIsotope;
    private double approachValue;

    /**
     * Constructor
     * @param id (ex: "Ga75")
     * @param elementName (ex: "Gallium")
     * @param numberNeutrons (ex: 6)
     * @param numberProtons (ex. 9)
     * @param decayType (ex. DecayType.BETA_MINUS)
     * @param halfTimeInS (ex 126)
     */
    public UnstableIsotope(String id, String elementName, int numberNeutrons, int numberProtons, DecayType decayType, double halfTimeInS) {
        super(id, elementName, numberNeutrons, numberProtons, decayType);
        this.halfTimeInS = halfTimeInS;
        this.decayFactor = Math.pow(0.5, 1 / halfTimeInS);
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

    public void setEmergingIsotope(Isotope emergingIsotope) {
        this.emergingIsotope = emergingIsotope;
    }
}
