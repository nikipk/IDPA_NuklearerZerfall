package com.amapolis.RadioactiveDecay.model;

import com.amapolis.RadioactiveDecay.model.exception.InvalidIsotopeException;
import com.amapolis.RadioactiveDecay.model.exception.NegativeIsotopeAmountInApproachCalculationException;
import com.amapolis.RadioactiveDecay.model.isotope.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This Class calculates the radioactive decay
 */
public class DecayCalculator {
    //Defines what is considered nothing (lower more precise but slower)
    private double zeroTolerance;
    //Callback for each time step
    private IsotopeProgressListener isotopeProgressListener;

    public DecayCalculator() {
        //default values
        zeroTolerance = 0.5;
        isotopeProgressListener = (time, isotopes) -> {
        };
    }

    /**
     * This Method calculates the radioactive decay and the resulting Isotopes of a single Isotope
     *
     * @param initialIsotope
     * @param amountIsotope
     * @param time
     * @return
     * @throws InvalidIsotopeException
     */
    public Map<Isotope, Double> getIsotopesAtTimeExact(Isotope initialIsotope, double amountIsotope, double time) throws InvalidIsotopeException {
        //Maps are in fact slower than Container classes in most cases, but not by far. Maps though have a a lot faster containsKey function which we might need if want multiple initial isotopes.
        //For performance tests please refer to the performance test in the test project.
        /* better looking implementation
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

            //Recursive for further isotopes
            returnIsotopes.putAll(getIsotopesAtTimeExact(time, amountIsotope - amountAfterTime, nextIsotope));
            return returnIsotopes;
        } else {
            throw new InvalidIsotopeException();
        } */

        //Faster implementation
        //LinkedHashMap is faster than HashMap and doesn't mix entries
        Map<Isotope, Double> returnIsotopes = new LinkedHashMap<Isotope, Double>();

        if (initialIsotope.getDecayType().isStable()) {
            returnIsotopes.put(initialIsotope, amountIsotope);
            return returnIsotopes;
        } else {

            UnstableIsotope unstableIsotope;
            try {
                unstableIsotope = (UnstableIsotope) initialIsotope;
            } catch (ClassCastException e) {
                throw new InvalidIsotopeException();
            }

            double amountAfterTime = amountIsotope * Math.pow(unstableIsotope.getDecayFactor(), time);
            returnIsotopes.put(unstableIsotope, amountAfterTime);

            returnIsotopes.putAll(getIsotopesAtTimeExact(unstableIsotope.getEmergingIsotope(), amountIsotope - amountAfterTime, time));
            return returnIsotopes;
        }
    }

    /**
     * This Method calculates the radioactive decay and the resulting Isotopes of multiple Isotopes
     *
     * @param initialIsotopes
     * @param time
     * @param allOccurringIsotopes
     * @return
     * @throws InvalidIsotopeException
     */
    public Map<Isotope, Double> getIsotopesAtTimeExact(Map<Isotope, Double> initialIsotopes, double time, Set<Isotope> allOccurringIsotopes) throws InvalidIsotopeException {
        Map<Isotope, Double> returnIsotopes = allOccurringIsotopes.stream().collect(Collectors.toMap(Function.identity(), p -> 0.0));

        for (Isotope initialIsotope : initialIsotopes.keySet()) {
            Map<Isotope, Double> tmpIsotopes = getIsotopesAtTimeExact(initialIsotope, initialIsotopes.get(initialIsotope), time);

            for (Isotope i : tmpIsotopes.keySet()) {
                //add values
                double newAmount = returnIsotopes.get(i) + tmpIsotopes.get(i);
                returnIsotopes.put(i, newAmount);
            }
        }

        return returnIsotopes;
    }

    /**
     * This Method calculates the approaching radioactive decay and the resulting Isotopes of a single Isotope
     * Please make sure that precision level is enough high or this function will return a bad value
     *
     * @param initialIsotope
     * @param amountIsotope
     * @param timeLastStep
     * @param timeCurrently
     * @return
     * @throws InvalidIsotopeException
     * @throws NegativeIsotopeAmountInApproachCalculationException
     */
    public Map<Isotope, Double> getIsotopesAtTimeApproach(Isotope initialIsotope, double amountIsotope, double timeLastStep, double timeCurrently) throws InvalidIsotopeException, NegativeIsotopeAmountInApproachCalculationException {
        if(amountIsotope < 0){
            throw new NegativeIsotopeAmountInApproachCalculationException(amountIsotope);
        }
        Map<Isotope, Double> returnIsotopes = new LinkedHashMap<Isotope, Double>();

        if (initialIsotope.getDecayType().isStable()) {
            returnIsotopes.put(initialIsotope, amountIsotope);
            return returnIsotopes;
        } else {
            UnstableIsotope unstableIsotope;
            try {
                unstableIsotope = (UnstableIsotope) initialIsotope;
            } catch (ClassCastException e) {
                throw new InvalidIsotopeException();
            }
            double amountAfterTime = amountIsotope - (amountIsotope * unstableIsotope.getApproachValue() * (timeCurrently - timeLastStep));
            returnIsotopes.put(initialIsotope, amountAfterTime);

            returnIsotopes.putAll(getIsotopesAtTimeApproach(unstableIsotope.getEmergingIsotope(), amountIsotope - amountAfterTime, timeLastStep, timeCurrently));
            return returnIsotopes;
        }
    }

    /**
     * This Method calculates the approaching radioactive decay and the resulting Isotopes of multiple Isotopes
     *
     * @param initialIsotopes
     * @param timeLastStep
     * @param timeCurrently
     * @param allOccurringIsotopes
     * @return
     * @throws InvalidIsotopeException
     * @throws NegativeIsotopeAmountInApproachCalculationException
     */
    public Map<Isotope, Double> getIsotopesAtTimeApproach(Map<Isotope, Double> initialIsotopes, double timeLastStep, double timeCurrently, Set<Isotope> allOccurringIsotopes) throws InvalidIsotopeException, NegativeIsotopeAmountInApproachCalculationException {
        Map<Isotope, Double> returnIsotopes = allOccurringIsotopes.stream().collect(Collectors.toMap(Function.identity(), p -> 0.0));

        for (Isotope initialIsotope : initialIsotopes.keySet()) {
            Map<Isotope, Double> tmpIsotopes = getIsotopesAtTimeApproach(initialIsotope, initialIsotopes.get(initialIsotope), timeLastStep, timeCurrently);

            for (Isotope i : tmpIsotopes.keySet()) {
                //add values
                double newAmount = returnIsotopes.get(i) + tmpIsotopes.get(i);
                returnIsotopes.put(i, newAmount);
            }
        }

        return returnIsotopes;
    }


    /**
     * Get all emerging isotopes that will result from the initial isotope.
     *
     * @param initialIsotope
     * @return
     */
    public Set<Isotope> getAllOccurringIsotopes(Isotope initialIsotope) throws InvalidIsotopeException {
        //LinkedHaspSet doesn't mix entries and is faster
        Set<Isotope> returnIsotopes = new LinkedHashSet<Isotope>();

        returnIsotopes.add(initialIsotope);

        if (initialIsotope.getDecayType().isStable()) {
            return returnIsotopes;
        } else {
            try {
                returnIsotopes.addAll(getAllOccurringIsotopes(((UnstableIsotope) initialIsotope).getEmergingIsotope()));
                return returnIsotopes;
            } catch (ClassCastException e) {
                throw new InvalidIsotopeException();
            }
        }
    }

    /**
     * Get all emerging isotopes that will result from the initial isotopes.
     *
     * @param initialIsotopes
     * @return
     */
    public Set<Isotope> getAllOccurringIsotopes(Collection<Isotope> initialIsotopes) throws InvalidIsotopeException {
        Set<Isotope> allOccurringIsotopes = new LinkedHashSet<>();
        //Set ignores add command if the isotope is already in the set
        for (Isotope i : initialIsotopes) {
            allOccurringIsotopes.addAll(getAllOccurringIsotopes(i));
        }
        return allOccurringIsotopes;
    }


    /**
     * This method create a timeline of the isotope decay.
     *
     * @param precisionLevel  (fraction of halftime => higher more precise, lower faster)
     * @param initialIsotopes
     * @return timeLine
     * @throws InvalidIsotopeException
     */
    public Map<Double, Map<Isotope, Double>> getIsotopeTimeLineExact(double precisionLevel, Map<Isotope, Double> initialIsotopes) throws InvalidIsotopeException {
        Map<Double, Map<Isotope, Double>> timeLine = new LinkedHashMap<>();

        //find Isotope with longest decay time
        //todo can maybe be shortend
        Set<Isotope> allOccurringIsotopes = getAllOccurringIsotopes(initialIsotopes.keySet());
        UnstableIsotope longestDecayingIsotope = findLongestDecayingIsotope(allOccurringIsotopes);
        //calculate until "nothing" is left for progress
        //if all isotopes are stable
        if (longestDecayingIsotope == null) {
            timeLine.put(0.0, initialIsotopes);
            isotopeProgressListener.onProgress(0.0, initialIsotopes);
            return timeLine;
        } else {
            double time = 0;
            boolean calculating = true;
            //until nothing is left / everything stable
            while (calculating) {
                Map<Isotope, Double> tmpIsotopes = getIsotopesAtTimeExact(initialIsotopes, time, allOccurringIsotopes);
                timeLine.put(time, tmpIsotopes);
                //update progress
                isotopeProgressListener.onProgress(time, tmpIsotopes);

                UnstableIsotope fastestDecayingIsotope = findFastestDecayingIsotopeOverToleranceValue(tmpIsotopes);
                if (fastestDecayingIsotope != null) {
                    time = time + (fastestDecayingIsotope.getHalfTimeInS() / precisionLevel);
                } else {
                    calculating = false;
                }
            }
        }

        return timeLine;
    }

    /**
     * This method create a timeline of the isotope decay.
     *
     * @param precisionLevel  (fraction of halftime => higher more precise, lower faster)
     * @param initialIsotopes
     * @return timeLine
     * @throws InvalidIsotopeException
     * @throws NegativeIsotopeAmountInApproachCalculationException
     */
    public Map<Double, Map<Isotope, Double>> getIsotopeTimeLineApproach(double precisionLevel, Map<Isotope, Double> initialIsotopes) throws InvalidIsotopeException, NegativeIsotopeAmountInApproachCalculationException {
        Map<Double, Map<Isotope, Double>> timeLine = new LinkedHashMap<>();

        //find Isotope with longest decay time
        //todo can maybe be shortend
        Set<Isotope> allOccurringIsotopes = getAllOccurringIsotopes(initialIsotopes.keySet());
        UnstableIsotope longestDecayingIsotope = findLongestDecayingIsotope(allOccurringIsotopes);
        //calculate until "nothing" is left for progress
        //if all isotopes are stable
        if (longestDecayingIsotope == null) {
            timeLine.put(0.0, initialIsotopes);
            isotopeProgressListener.onProgress(0.0, initialIsotopes);
            return timeLine;
        } else {
            double timeLastStep = 0;
            double time = 0;
            //initial isotope calculation
            Map<Isotope, Double> tmpIsotopes = getIsotopesAtTimeApproach(initialIsotopes, timeLastStep, time, allOccurringIsotopes);
            timeLine.put(time, tmpIsotopes);
            isotopeProgressListener.onProgress(time, tmpIsotopes);

            boolean calculating = true;
            //until nothing is left / everything stable
            while (calculating) {
                timeLastStep = time;

                UnstableIsotope fastestDecayingIsotope = findFastestDecayingIsotopeOverToleranceValue(tmpIsotopes);
                if (fastestDecayingIsotope != null) {
                    time = time + (fastestDecayingIsotope.getHalfTimeInS() / precisionLevel);
                } else {
                    calculating = false;
                }

                tmpIsotopes = getIsotopesAtTimeApproach(tmpIsotopes, timeLastStep, time, allOccurringIsotopes);
                timeLine.put(time, tmpIsotopes);
                //update progress
                isotopeProgressListener.onProgress(time, tmpIsotopes);
            }
        }

        return timeLine;
    }


    /**
     * This method searches for the longest decaying isotope
     * returns null if no isotope is decaying
     *
     * @param isotopes
     */
    private UnstableIsotope findLongestDecayingIsotope(Collection<Isotope> isotopes) {
        UnstableIsotope longestDecayingIsotope = null;
        double longestTime = 0;
        for (Isotope i : isotopes) {
            if (i instanceof UnstableIsotope) {
                UnstableIsotope tmpUnstableIsotope = (UnstableIsotope) i;
                double tmpDecayTime = tmpUnstableIsotope.getHalfTimeInS();
                if (longestTime < tmpDecayTime) {
                    longestTime = tmpDecayTime;
                    longestDecayingIsotope = tmpUnstableIsotope;
                }
            }
        }
        return longestDecayingIsotope;
    }

    /**
     * This method searches for the fastest decaying isotope
     * returns null if no isotope is decaying
     *
     * @param isotopes
     */
    private UnstableIsotope findFastestDecayingIsotope(Collection<Isotope> isotopes) {
        UnstableIsotope fastestDecayingIsotope = null;
        double shortestTime = Double.MAX_VALUE;
        for (Isotope i : isotopes) {
            if (i instanceof UnstableIsotope) {
                UnstableIsotope tmpUnstableIsotope = (UnstableIsotope) i;
                double tmpDecayTime = tmpUnstableIsotope.getHalfTimeInS();
                if (shortestTime > tmpDecayTime) {
                    shortestTime = tmpDecayTime;
                    fastestDecayingIsotope = tmpUnstableIsotope;
                }
            }
        }
        return fastestDecayingIsotope;
    }

    /**
     * This method searches for the fastest decaying isotope with more than the tolerance value
     * returns null if no isotope is decaying
     *
     * @param isotopes
     */
    private UnstableIsotope findFastestDecayingIsotopeOverToleranceValue(Map<Isotope, Double> isotopes) {
        UnstableIsotope fastestDecayingIsotope = null;
        double shortestTime = Double.MAX_VALUE;
        for (Isotope i : isotopes.keySet()) {
            if (i instanceof UnstableIsotope && isotopes.get(i) >= zeroTolerance) {
                UnstableIsotope tmpUnstableIsotope = (UnstableIsotope) i;
                double tmpDecayTime = tmpUnstableIsotope.getHalfTimeInS();
                if (shortestTime > tmpDecayTime) {
                    shortestTime = tmpDecayTime;
                    fastestDecayingIsotope = tmpUnstableIsotope;
                }
            }
        }
        return fastestDecayingIsotope;
    }

    /**
     * This method calculates the time until (or almost) "nothing" is left
     *
     * @param isotope
     * @return
     */
    private double timeUntilNothingLeft(UnstableIsotope isotope) {
        //todo doesnt work
        // N = Anzahl Atome nach t Zeit
        // a = Anfangsbestand
        // m = Zerfallsfaktor
        // t = Zeit
        // h = Halbwertszeit

        // N = a * m^t
        // => ≈0 = m^t
        // => t = Log(≈0)/Log(m)
        return Math.log(zeroTolerance) / Math.log(isotope.getDecayFactor());
    }

    public void setIsotopeProgressListener(IsotopeProgressListener isotopeProgressListener) {
        this.isotopeProgressListener = isotopeProgressListener;
    }

    public double getZeroTolerance() {
        return zeroTolerance;
    }

    public void setZeroTolerance(double zeroTolerance) {
        this.zeroTolerance = zeroTolerance;
    }

    public IsotopeProgressListener getIsotopeProgressListener() {
        return isotopeProgressListener;
    }
}
