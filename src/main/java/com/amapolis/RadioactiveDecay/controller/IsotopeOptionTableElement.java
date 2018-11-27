package com.amapolis.RadioactiveDecay.controller;

import com.amapolis.RadioactiveDecay.model.isotope.Isotope;

public class IsotopeOptionTableElement {

    private Isotope option;

    public IsotopeOptionTableElement(Isotope option) {
        this.option = option;
    }

    public Isotope getOption() {
        return option;
    }
}
