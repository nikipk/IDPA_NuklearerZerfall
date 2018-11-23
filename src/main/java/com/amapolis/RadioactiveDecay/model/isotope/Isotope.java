package com.amapolis.RadioactiveDecay.model.isotope;

import com.amapolis.RadioactiveDecay.model.utils.RandomColor;
import javafx.scene.paint.Color;

/**
 * This Class represents the base information of one Isotope
 */
public abstract class Isotope {
    
    private String id, elementName, colorCode;
    private int numberNeutrons, numberProtons;     
    private DecayType decayType;

    public Isotope(String id, String elementName, int numberNeutrons, int numberProtons, DecayType decayType) {
        this.id = id;
        this.elementName = elementName;
        this.numberNeutrons = numberNeutrons;
        this.numberProtons = numberProtons;
        this.decayType = decayType;
        //default random value
        this.colorCode = RandomColor.getRandomHexColor();
    }

    public String getId() {
        return id;
    }

    public String getElementName() {
        return elementName;
    }

    public int getNumberNeutrons() {
        return numberNeutrons;
    }

    public int getNumberProtons() {
        return numberProtons;
    }

    public DecayType getDecayType() {
        return decayType;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
