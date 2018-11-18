package com.amapolis.RadioactiveDecay.model.isotope;

/**
 * Class for unstable isotopes. Contains some more information.
 */
public class UnstableIsotope extends Isotope {
    private double halfTimeInS;
    private Isotope followingIsotope;

    public UnstableIsotope(String id, int atomicNumber, double atomicMassInU, DecayType decayType, double halfTimeInS, Isotope followingIsotope) {
        super(id, atomicNumber, atomicMassInU, decayType);
        this.halfTimeInS = halfTimeInS;
        this.followingIsotope = followingIsotope;
    }
}
