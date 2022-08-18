package com.coyni.mapp.model.giftcard;

import java.io.Serializable;

public class BrandRequirements implements Serializable {
    private String displayInstructions;
    private String termsAndConditionsInstructions;
    private String disclaimerInstructions;
    private Boolean alwaysShowDisclaimer;

    public String getDisplayInstructions() {
        return displayInstructions;
    }

    public void setDisplayInstructions(String displayInstructions) {
        this.displayInstructions = displayInstructions;
    }

    public String getTermsAndConditionsInstructions() {
        return termsAndConditionsInstructions;
    }

    public void setTermsAndConditionsInstructions(String termsAndConditionsInstructions) {
        this.termsAndConditionsInstructions = termsAndConditionsInstructions;
    }

    public String getDisclaimerInstructions() {
        return disclaimerInstructions;
    }

    public void setDisclaimerInstructions(String disclaimerInstructions) {
        this.disclaimerInstructions = disclaimerInstructions;
    }

    public Boolean getAlwaysShowDisclaimer() {
        return alwaysShowDisclaimer;
    }

    public void setAlwaysShowDisclaimer(Boolean alwaysShowDisclaimer) {
        this.alwaysShowDisclaimer = alwaysShowDisclaimer;
    }
}
