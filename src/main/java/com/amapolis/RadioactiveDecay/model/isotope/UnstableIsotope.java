package com.amapolis.RadioactiveDecay.model.isotope;

/**
 * Class for unstable isotopes. Contains some more information.
 */
public class UnstableIsotope extends Isotope {
    
    private double halfTimeInS, decayFactor;
    private Isotope emergingIsotope;

    /**
     *
     * Constructor
     * @param id (ex: "Ga75")
     * @param elementName (ex: "Gallium")
     * @param numberNeutrons (ex: 6)
     * @param numberProtons (ex. 9)
     * @param decayType (ex. DecayType.BETA_MINUS)
     * @param halfTimeInS (ex 126)
     * @param emergingIsotope (ex Ge75)
     */
    public UnstableIsotope(String id, String elementName, int numberNeutrons, int numberProtons, DecayType decayType, double halfTimeInS, Isotope emergingIsotope) {
        super(id, elementName, numberNeutrons, numberProtons, decayType);
        this.halfTimeInS = halfTimeInS;
        this.decayFactor = Math.pow(0.5, 1/halfTimeInS);
        this.emergingIsotope = emergingIsotope;
    }

    
}
