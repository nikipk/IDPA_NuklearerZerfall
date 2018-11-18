package com.amapolis.RadioactiveDecay.model.isotope;

/**
 * This Class represents the base information of one Isotope
 */
public abstract class Isotope {
    private String id;
    private int atomicNumber, atomicMass;
    private DecayType decayType;

    public Isotope(String id, int atomicNumber, int atomicMass, DecayType decayType) {
        this.id = id;
        this.atomicNumber = atomicNumber;
        this.atomicMass = atomicMass;
        this.decayType = decayType;
    }

    public String getId() {
        return id;
    }

    public int getAtomicMass() {
        return atomicMass;
    }

    public int getAtomicNumber() {
        return atomicNumber;
    }

    public DecayType getDecayType() {
        return decayType;
    }
}
