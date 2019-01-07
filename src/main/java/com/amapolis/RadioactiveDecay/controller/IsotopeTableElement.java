package com.amapolis.RadioactiveDecay.controller;

import com.amapolis.RadioactiveDecay.model.isotope.Isotope;
import com.amapolis.RadioactiveDecay.model.isotope.UnstableIsotope;

/**
 * This class creates an object for the tableView because the tableView doesn't work with Maps.
 */
public class IsotopeTableElement {
    private Isotope isotope;
    private double amount;

    public IsotopeTableElement(Isotope isotope, double amount) {
        this.isotope = isotope;
        this.amount = amount;
    }

    public Isotope getIsotope() {
        return isotope;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getId(){
        return isotope.getId();
    }

    public String getElementName(){
        return isotope.getElementName();
    }

    public int getNumberProtons(){
        return isotope.getNumberProtons();
    }

    public int getNumberNeutrons(){
        return isotope.getNumberNeutrons();
    }

    public int getAtomicMass(){
        return isotope.getAtomicMass();
    }

    public Double getHalfTimeInS(){
        if(isotope instanceof UnstableIsotope){
            return ((UnstableIsotope) isotope).getHalfTimeInS();
        } else {
            return null;
        }
    }

    public String getDecayType(){
        return isotope.getDecayType().toString();
    }


}
