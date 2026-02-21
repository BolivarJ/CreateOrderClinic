package app.domain.ports;

import app.domain.models.Patient;

public interface PatientPort {

    public boolean existsByDocument(String cedula);
    public Patient findById(long id);
    public void save(Patient patient);
} 
