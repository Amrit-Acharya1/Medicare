package com.acharyaamrit.medicare.model.patientModel;

import java.util.List;

public class RoutineMedicine {

    private List<Medicine> morning;
    private List<Medicine> afternoon;
    private List<Medicine> evening;
    private List<Medicine> night;

    public RoutineMedicine(List<Medicine> morning, List<Medicine> afternoon, List<Medicine> evening, List<Medicine> night) {
        this.morning = morning;
        this.afternoon = afternoon;
        this.evening = evening;
        this.night = night;
    }

    public RoutineMedicine() {
    }

    public List<Medicine> getMorning() {
        return morning;
    }

    public void setMorning(List<Medicine> morning) {
        this.morning = morning;
    }

    public List<Medicine> getAfternoon() {
        return afternoon;
    }

    public void setAfternoon(List<Medicine> afternoon) {
        this.afternoon = afternoon;
    }

    public List<Medicine> getEvening() {
        return evening;
    }

    public void setEvening(List<Medicine> evening) {
        this.evening = evening;
    }

    public List<Medicine> getNight() {
        return night;
    }

    public void setNight(List<Medicine> night) {
        this.night = night;
    }
}
