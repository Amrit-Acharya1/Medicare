package com.acharyaamrit.medicare.patient.model.response;

import com.acharyaamrit.medicare.patient.model.patientModel.PatientDocument;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PatientDocumentResponse {
    @SerializedName("documents")
    private List<PatientDocument> documents;

    public PatientDocumentResponse(List<PatientDocument> documents) {
        this.documents = documents;
    }

    public List<PatientDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<PatientDocument> documents) {
        this.documents = documents;
    }
}
