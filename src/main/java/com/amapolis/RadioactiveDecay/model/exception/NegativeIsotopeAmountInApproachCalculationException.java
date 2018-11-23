package com.amapolis.RadioactiveDecay.model.exception;

public class NegativeIsotopeAmountInApproachCalculationException extends Exception{
    public NegativeIsotopeAmountInApproachCalculationException() {
        super("The amount of isotopes in the approach calculation was under 0. Please make sure not to use too big time steps.");
    }
    public NegativeIsotopeAmountInApproachCalculationException(double amount) {
        super("The amount of isotopes (" + amount + ") in the approach calculation was under 0. Please make sure not to use too big time steps.");
    }
}
