package com.nano.clinicbooking.service.appointment;

import com.nano.clinicbooking.enums.AppointmentStatus;
import com.nano.clinicbooking.enums.AppointmentType;
import com.nano.clinicbooking.enums.SlotStatus;
import com.nano.clinicbooking.exception.ResourceNotFoundException;
import com.nano.clinicbooking.mapper.AppointmentMapper;
import com.nano.clinicbooking.model.*;
import com.nano.clinicbooking.repository.shiftSlot.ShiftSlotRepository;
import com.nano.clinicbooking.repository.SpecialtyRepository;
import com.nano.clinicbooking.repository.appointment.*;
import com.nano.clinicbooking.repository.doctor.DoctorRepository;
import com.nano.clinicbooking.repository.doctor.DoctorShiftRepository;
import com.nano.clinicbooking.repository.patient_info.PatientInformationRepository;
import com.nano.clinicbooking.repository.search_user.UserRepository;
import com.nano.clinicbooking.dto.request.BookAppointmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentCreationService {

    private final AppointmentRepository appointmentRepo;
    private final UserRepository userRepo;
    private final DoctorRepository doctorRepo;
    private final SpecialtyRepository specialtyRepo;
    private final DoctorShiftRepository shiftRepo;
    private final ShiftSlotRepository slotRepo;
    private final PatientInformationRepository patientInfoRepo;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public Appointment createAppointment(BookAppointmentRequest request, Long patientId, Long doctorId, Long specialtyId) {

        // 🔹 1. Lấy thông tin bệnh nhân & chuyên khoa
        User patient = userRepo.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        Specialty specialty = specialtyRepo.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found"));

        // 🔹 2. Tạo entity Appointment mới
        Appointment reqApp = request.getAppointment();
        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setSpecialty(specialty);
        appointment.setStatus(AppointmentStatus.PENDING_CONFIRMATION);
        appointment.generateAppointmentNo();

        // 🔹 3. Gán reason và type
        if (reqApp != null) {
            appointment.setReason(reqApp.getReason());
            appointment.setType(reqApp.getType() != null ? reqApp.getType() : AppointmentType.OFFLINE);
        } else {
            appointment.setType(AppointmentType.OFFLINE);
        }

        // 🔹 4. Nếu là ONLINE → tạo link Jitsi
        if (appointment.getType() == AppointmentType.ONLINE) {
            String roomName = "clinicroom-" + appointment.getAppointmentNo();
            appointment.setMeetingUrl("https://meet.jit.si/" + roomName);
            appointment.setJoinCode(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }

        // 🔹 5. Gán bác sĩ
        if (doctorId != null) {
            Doctor doctor = doctorRepo.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            appointment.setDoctor(doctor);
        }

        // 🔹 6. Gán ca khám và slot (nếu có)
        if (request.getShiftId() != null) {
            DoctorShift shift = shiftRepo.findById(request.getShiftId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));

            ShiftSlot slot = slotRepo.findFirstByShiftIdAndStatusOrderBySlotNumberAsc(shift.getId(), SlotStatus.AVAILABLE)
                    .orElseThrow(() -> new IllegalStateException("No available slot"));

            slot.setStatus(SlotStatus.BOOKED);
            slot.setAppointment(appointment);
            slotRepo.save(slot);

            appointment.setShift(shift);
            appointment.setSlot(slot);
            appointment.setAppointmentDate(shift.getDate());
            appointment.setAppointmentTime(slot.getStartTime());
        }

        // 🔹 7. Gắn danh sách bệnh nhân
        if (request.getPatients() != null && !request.getPatients().isEmpty()) {
            List<PatientInformation> infos = request.getPatients().stream()
                    .map(i -> mapPatientInfo(i, appointment, patient))
                    .collect(Collectors.toList());
            appointment.setPatientInfos(infos);
        }

        // 🔹 8. Lưu cuộc hẹn
        return appointmentRepo.save(appointment);
    }


    private PatientInformation mapPatientInfo(PatientInformation info, Appointment appointment, User owner) {
        PatientInformation entity;

        if (info.getId() != null) {
            entity = patientInfoRepo.findById(info.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient info not found"));
        } else {
            entity = new PatientInformation();
            entity.setFullName(info.getFullName());
            entity.setGender(info.getGender());
            entity.setPhoneNumber(info.getPhoneNumber());
            entity.setAddress(info.getAddress());
            entity.setDob(info.getDob());
            entity.setRelationship(info.getRelationship());
            entity.setOwner(owner);
        }

        entity.setAppointment(appointment);
        return entity;
    }
}
