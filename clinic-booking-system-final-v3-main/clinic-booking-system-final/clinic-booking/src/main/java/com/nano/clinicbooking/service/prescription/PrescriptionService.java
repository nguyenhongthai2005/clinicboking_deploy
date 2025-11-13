package com.nano.clinicbooking.service.prescription;

import com.nano.clinicbooking.exception.ResourceNotFoundException;
import com.nano.clinicbooking.model.Appointment;
import com.nano.clinicbooking.model.Prescription;
import com.nano.clinicbooking.repository.appointment.AppointmentRepository;
import com.nano.clinicbooking.repository.prescription.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepo;
    private final AppointmentRepository appointmentRepo;

    @Transactional
    public Prescription createPrescription(Long appointmentId, Prescription body) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        body.setAppointment(appointment);
        return prescriptionRepo.save(body);
    }

    @Transactional(readOnly = true)
    public List<Prescription> getPrescriptionsByAppointment(Long appointmentId) {
        return prescriptionRepo.findByAppointmentId(appointmentId);
    }

    @Transactional(readOnly = true)
    public boolean hasPrescription(Long appointmentId) {
        return prescriptionRepo.existsByAppointmentId(appointmentId);
    }

    @Transactional
    public List<Prescription> createPrescriptions(Long appointmentId, List<Prescription> prescriptions) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
        prescriptions.forEach(prescription -> prescription.setAppointment(appointment));
        return prescriptionRepo.saveAll(prescriptions);
    }

    @Transactional
    public List<Prescription> updatePrescriptions(Long appointmentId, List<Prescription> prescriptions) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
        // Check if appointment is completed
        if (appointment.getStatus() == com.nano.clinicbooking.enums.AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update prescriptions for completed appointment");
        }
        
        // Delete existing prescriptions for this appointment
        List<Prescription> existing = prescriptionRepo.findByAppointmentId(appointmentId);
        prescriptionRepo.deleteAll(existing);
        
        // Create new prescriptions
        prescriptions.forEach(prescription -> prescription.setAppointment(appointment));
        return prescriptionRepo.saveAll(prescriptions);
    }
}
