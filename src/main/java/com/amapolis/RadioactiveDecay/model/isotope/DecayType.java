package com.amapolis.RadioactiveDecay.model.isotope;

/**
 * This enum contains information about the decay.
 */
public enum DecayType {
    STABLE(true), ALPHA(false), BETA_MINUS(false), BETA_PLUS(false);

    private final boolean stable;

    DecayType(boolean stable) {
        this.stable = stable;
    }

    public boolean isStable() {
        return stable;
    }
}
