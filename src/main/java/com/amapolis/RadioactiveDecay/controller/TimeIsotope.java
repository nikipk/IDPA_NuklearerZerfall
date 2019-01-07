package com.amapolis.RadioactiveDecay.controller;

import com.amapolis.RadioactiveDecay.model.isotope.Isotope;

import java.util.Map;

/**
 * POJO. Contains time and isotope.
 */
public class TimeIsotope {
    private double time;
    private Map<Isotope, Double> isotopes;

    public TimeIsotope(double time, Map<Isotope, Double> isotopes) {
        this.time = time;
        this.isotopes = isotopes;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public Map<Isotope, Double> getIsotopes() {
        return isotopes;
    }

    public void setIsotopes(Map<Isotope, Double> isotopes) {
        this.isotopes = isotopes;
    }
}
