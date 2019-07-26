package com.infineon.cipurse.app;

public class PrescriptionDetails {
    private String prescriptionName = "";
    private String prescriptionDate = "";
    private String prescriptionRefills = "";
    private String prescriptionDirections = "";
    private String prescriptionDirectionsRaw = "";

    public void setPrescriptionName(String name) {
        this.prescriptionName = name;
    }

    public String getPrescriptionName() {
        return prescriptionName;
    }

    public void setPrescriptionDate(String date) {
        this.prescriptionDate = date;
    }

    public String getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setprescriptionRefills(String refills) {
        this.prescriptionRefills = refills;
    }

    public String getprescriptionRefills() {
        return prescriptionRefills;
    }

    public void setPrescriptionDirections(String directions) {
        this.prescriptionDirections = directions;
    }

    public String getPrescriptionDirections() {
        return prescriptionDirections;
    }

    public void setPrescriptionDirectionsRaw(String directions) {
        this.prescriptionDirectionsRaw = directions;
    }

    public String getPrescriptionDirectionsRaw() {
        return prescriptionDirectionsRaw;
    }
}
