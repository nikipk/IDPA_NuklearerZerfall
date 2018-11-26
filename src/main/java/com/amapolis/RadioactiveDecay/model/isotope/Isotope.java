package com.amapolis.RadioactiveDecay.model.isotope;

import com.amapolis.RadioactiveDecay.model.utils.RandomColor;
import javafx.scene.paint.Color;

/**
 * This Class represents the base information of one Isotope
 */
public abstract class Isotope {
    
    private String id, elementName, shortElementName;
    private int numberNeutrons, numberProtons;     
    private DecayType decayType;

    public Isotope(String id, String elementName, int numberNeutrons, int numberProtons, DecayType decayType) {
        this.id = id+(numberProtons+numberNeutrons);
        this.shortElementName = id;
        this.elementName = elementName;
        this.numberNeutrons = numberNeutrons;
        this.numberProtons = numberProtons;
        this.decayType = decayType;
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

    public int getAtomicMass(){
        return numberNeutrons + numberProtons;
    }

    public String getShortElementName() {
        return shortElementName;
    }

    public DecayType getDecayType() {
        return decayType;
    }
}
