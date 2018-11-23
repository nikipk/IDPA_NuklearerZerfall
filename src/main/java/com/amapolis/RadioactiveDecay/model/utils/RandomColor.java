package com.amapolis.RadioactiveDecay.model.utils;

import java.util.Random;

public class RandomColor {
    //Random as attribute because we don't want to create a new object every time we call the getRandomHexColor method
    private static Random random = new Random();

    /**
     * This method returns a random color hex code
     * @return
     */
    public static String getRandomHexColor(){
        // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
        int nextInt = random.nextInt(0xffffff + 1);

        // format it as hexadecimal string (with hashtag and leading zeros)
        String colorCode = String.format("#%06x", nextInt);

        return colorCode;
    }
}
