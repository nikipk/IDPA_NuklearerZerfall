package com.amapolis.RadioactiveDecay.test;

import com.amapolis.RadioactiveDecay.model.isotope.DecayType;
import com.amapolis.RadioactiveDecay.model.isotope.Isotope;
import com.amapolis.RadioactiveDecay.model.isotope.StableIsotope;
import com.amapolis.RadioactiveDecay.model.isotope.UnstableIsotope;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is a test-class for measuring the performance of the different calculation methods.
 */
public class PerformanceTestDifferentMethodCalculation {
    public static void main(String[] args) {
        UnstableIsotope isotope1 = new UnstableIsotope("I", "Test1", 69, 420, DecayType.ALPHA, 66.6);
        UnstableIsotope isotope2 = new UnstableIsotope("I", "Test2", 70, 419, DecayType.ALPHA, 69.69);
        StableIsotope isotope3 = new StableIsotope("I", "Test3", 71, 418);

        isotope1.setEmergingIsotope(isotope2);
        isotope2.setEmergingIsotope(isotope3);

        //amount of calculations
        int amount = 1000;
        System.out.println("Amount calculations: " + amount);

        System.out.println("\n------------------------");

        System.out.println("Test exact");
        long startExact = System.nanoTime();
        for(int i = 0; i < amount; i++){
            getIsotopesAtTimeExact(isotope1, 123456, 78.9);
        }
        long endExact = System.nanoTime();
        System.out.println("\nTime required: " + (endExact - startExact) + "ns");

        System.out.println("------------------------");

        System.out.println("Test approach");
        long startApproach = System.nanoTime();
        for(int i = 0; i < amount; i++){
            getIsotopesAtTimeExact(isotope1, 123456, 78.9);
        }
        long endApproach = System.nanoTime();
        System.out.println("\nTime required: " + (endApproach - startApproach) + "ns");

        System.out.println("------------------------");


        System.out.println("\nApproach was " + ((double)(endExact - startExact)/(endApproach - startApproach)) + " times faster");

    }

    public static Map<Isotope, Double> getIsotopesAtTimeExact(Isotope initialIsotope, double amountIsotope, double time) {
        Map<Isotope, Double> returnIsotopes = new LinkedHashMap<Isotope, Double>();

        if (initialIsotope.getDecayType().isStable()) {
            returnIsotopes.put(initialIsotope, amountIsotope);
            return returnIsotopes;
        } else {

            UnstableIsotope unstableIsotope = null;
            try {
                unstableIsotope = (UnstableIsotope) initialIsotope;
            } catch (ClassCastException e) {
            }

            double amountAfterTime = amountIsotope * Math.pow(unstableIsotope.getDecayFactor(), time);
            returnIsotopes.put(unstableIsotope, amountAfterTime);

            //recursive calculation
            returnIsotopes.putAll(getIsotopesAtTimeExact(unstableIsotope.getEmergingIsotope(), amountIsotope - amountAfterTime, time));
            return returnIsotopes;
        }
    }

    public static Map<Isotope, Double> getIsotopesAtTimeApproach(Isotope initialIsotope, double amountIsotope, double timeLastStep, double timeCurrently){

        Map<Isotope, Double> returnIsotopes = new LinkedHashMap<Isotope, Double>();

        if (initialIsotope.getDecayType().isStable()) {
            returnIsotopes.put(initialIsotope, amountIsotope);
            return returnIsotopes;
        } else {
            UnstableIsotope unstableIsotope = null;
            try {
                unstableIsotope = (UnstableIsotope) initialIsotope;
            } catch (ClassCastException e) {
            }

            double amountAfterTime = amountIsotope - (amountIsotope * unstableIsotope.getApproachValue() * (timeCurrently - timeLastStep));
            //make sure no sub zero value breaks the algorithm
            if(amountAfterTime < 0.0){
                amountAfterTime = 0.0;
            }
            returnIsotopes.put(initialIsotope, amountAfterTime);

            //recursive calculation
            returnIsotopes.putAll(getIsotopesAtTimeApproach(unstableIsotope.getEmergingIsotope(), amountIsotope - amountAfterTime, timeLastStep, timeCurrently));
            return returnIsotopes;
        }
    }
}
