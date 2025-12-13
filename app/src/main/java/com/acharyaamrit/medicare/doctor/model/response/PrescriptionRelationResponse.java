package com.acharyaamrit.medicare.doctor.model.response;

public class PrescriptionRelationResponse {

    private PrescriptionRelation preciptionRelation;

    public PrescriptionRelation getPreciptionRelation() {
        return preciptionRelation;
    }

    public void setPreciptionRelation(PrescriptionRelation preciptionRelation) {
        this.preciptionRelation = preciptionRelation;
    }

    public static class PrescriptionRelation {
        private int doctor_id;
        private int patient_id;
        private String updated_at;
        private String created_at;
        private int id;

        public int getDoctor_id() {
            return doctor_id;
        }

        public void setDoctor_id(int doctor_id) {
            this.doctor_id = doctor_id;
        }

        public int getPatient_id() {
            return patient_id;
        }

        public void setPatient_id(int patient_id) {
            this.patient_id = patient_id;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
