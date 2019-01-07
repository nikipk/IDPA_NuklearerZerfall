package com.amapolis.RadioactiveDecay.model.exception;

/**
 * Exception for when an impossible isotope is detected. (For example; isotope that decays to itself)
 */
public class InvalidIsotopeException extends Exception {
    public InvalidIsotopeException() {
        super("Invalid Isotope detected!");
    }
}
