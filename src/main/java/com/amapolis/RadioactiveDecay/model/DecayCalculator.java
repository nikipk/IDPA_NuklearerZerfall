package com.amapolis.RadioactiveDecay.model;

import com.amapolis.RadioactiveDecay.model.exception.InvalidIsotopeException;
import com.amapolis.RadioactiveDecay.model.isotope.*;

import java.util.*;

public class DecayCalculator {
    public static void main(String[] args) {
        StableIsotope as75 = new StableIsotope("As75", 23452, 2332, DecayType.STABLE);
        UnstableIsotope ge75 = new UnstableIsotope("Ge75", 23452, 2332, DecayType.BETA_MINUS, 4966.8, as75);
        UnstableIsotope ga75 = new UnstableIsotope("Ga75", 23452, 2332, DecayType.BETA_MINUS, 126, ge75);

        try {
            Map<Isotope, Double> test = null;
            long timeStart = System.currentTimeMillis();
            for(int i = 0; i < 1000000; i++) {
                test = getIsotopeAtTimeExact(ga75, 1000000, 170);
            }
            long timeStop = System.currentTimeMillis();
            System.out.println("Used Time: " + (timeStop - timeStart) + "ms");
            for(Isotope i:test.keySet()){
                System.out.println(i.getId() + ": " + test.get(i));
            }
        } catch (InvalidIsotopeException e) {
            e.printStackTrace();
        }
    }


    /**
     * Maps are in fact slower than Container classes in most cases, but not by far. Maps though have a a lot faster containsKey function which we might need if want multiple initial isotopes.
     * For performance tests please refer to the performance test in the test project.
     * @param initialIsotope
     * @param amountIsotope
     * @param time
     * @return
     * @throws InvalidIsotopeException
     */
    public static Map<Isotope, Double> getIsotopeAtTimeExact(Isotope initialIsotope, double amountIsotope, double time) throws InvalidIsotopeException {
        /* Good looking implementation
        //LinkedHashMap is faster than HashMap
        Map<Isotope, Double> returnIsotopes = new LinkedHashMap<Isotope, Double>();

        if(initialIsotope.getDecayType().isStable()){
            returnIsotopes.put(initialIsotope, amountIsotope);
            return returnIsotopes;
        } else if (initialIsotope instanceof UnstableIsotope){
            UnstableIsotope isotope = (UnstableIsotope) initialIsotope;
            // N = Anzahl Atome nach t Zeit
            // a = Anfangsbestand
            // m = Zerfallsfaktor
            // t = Zeit
            // h = Halbwertszeit

            // N = a * m^t

            // 0.5 = m^h
            // => m = 0.5^(1/h)

            double halftime = isotope.getHalfTimeInS();
            double decayFactor = Math.pow(0.5, 1/halftime);

            double amountAfterTime = amountIsotope * Math.pow(decayFactor, time);

            returnIsotopes.put(isotope, amountAfterTime);

            Isotope nextIsotope = isotope.getEmergingIsotope();

            //Rekursiv für weitere Isotope
            returnIsotopes.putAll(getIsotopeAtTimeExact(nextIsotope, amountIsotope - amountAfterTime, time));
            return returnIsotopes;
        } else {
            throw new InvalidIsotopeException();
        } */

        //Fast implementation
        //LinkedHashMap is faster than HashMap
        Map<Isotope, Double> returnIsotopes = new LinkedHashMap<Isotope, Double>();

        if(initialIsotope.getDecayType().isStable()){
            returnIsotopes.put(initialIsotope, amountIsotope);
            return returnIsotopes;
        } else {

            UnstableIsotope unstableIsotope;
            try {
                unstableIsotope = (UnstableIsotope) initialIsotope;
            } catch (ClassCastException e){
                throw new InvalidIsotopeException();
            }

            double amountAfterTime = amountIsotope * Math.pow(unstableIsotope.getDecayFactor(), time);

            returnIsotopes.put(unstableIsotope, amountAfterTime);

            //Rekursiv für weitere Isotope
            returnIsotopes.putAll(getIsotopeAtTimeExact(unstableIsotope.getEmergingIsotope(), amountIsotope - amountAfterTime, time));
            return returnIsotopes;
        }
    }
}
