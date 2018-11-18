package com.amapolis.RadioactiveDecay.model.isotope;

/**
 * This Class represents the base information of one Isotope
 */
public abstract class Isotope {
    private String id;
    private int atomicNumber;
    private double atomicMassInU;
    private DecayType decayType;

    public Isotope(String id, int atomicNumber, double atomicMassInU, DecayType decayType) {
        this.id = id;
        this.atomicNumber = atomicNumber;
        this.atomicMassInU = atomicMassInU;
        this.decayType = decayType;
    }

    public String getId() {
        return id;
    }

    public double getAtomicMassInU() {
        return atomicMassInU;
    }

    public int getAtomicNumber() {
        return atomicNumber;
    }

    public DecayType getDecayType() {
        return decayType;
    }
}
