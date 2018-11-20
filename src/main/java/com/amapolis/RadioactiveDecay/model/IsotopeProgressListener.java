package com.amapolis.RadioactiveDecay.model;

import com.amapolis.RadioactiveDecay.model.isotope.Isotope;

import java.util.Map;

public interface IsotopeProgressListener {
    public void onProgress(int progressInPercent, double time,Map<Isotope, Double> isotopes);
}
