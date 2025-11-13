package com.nano.clinicbooking.service.appointment;

import com.nano.clinicbooking.enums.AppointmentStatus;
import com.nano.clinicbooking.enums.AppointmentType;
import com.nano.clinicbooking.model.*;
import com.nano.clinicbooking.repository.SpecialtyRepository;
import com.nano.clinicbooking.repository.appointment.AppointmentRepository;
import com.nano.clinicbooking.repository.doctor.DoctorRepository;
import com.nano.clinicbooking.repository.search_user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class AppointmentStatusServiceTest {

    @Autowired private AppointmentStatusService appointmentStatusService;
    @Autowired private AppointmentRepository appointmentRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private SpecialtyRepository specialtyRepo;
    @Autowired private DoctorRepository doctorRepo;

    private User patient;
    private Doctor doctor;
    private Specialty specialty;

    @BeforeEach
    void setup() {
        specialty = new Specialty();
        specialty.setName("Tai Mũi Họng");
        specialty = specialtyRepo.save(specialty);

        patient = new User();
        patient.setFullName("Test Patient");
        patient.setEmail("patient@test.com");
        patient.setPassword("123");
        patient.setUserType("Patient");
        patient.setIsEnable(true);
        patient = userRepo.save(patient);

        doctor = new Doctor();
        doctor.setFullName("Dr. Mock");
        doctor.setEmail("doctor@test.com");
        doctor.setPassword("123");
        doctor.setUserType("Doctor");
        doctor.setIsVip(false);
        doctor.setConsultationFee(0.0);
        doctor.setSpecialty(specialty);
        doctor = doctorRepo.save(doctor);
    }

    /** ❌ Huỷ trong vòng < 48h => bị chặn */
    @Test
    void testCancelWithin48Hours_ShouldThrow() {
        Appointment a = new Appointment();
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setSpecialty(specialty);
        a.setStatus(AppointmentStatus.CONFIRMED);
        a.setType(AppointmentType.OFFLINE);
        a.setAppointmentDate(LocalDate.now().plusDays(1)); // <48h
        a.setAppointmentTime(LocalTime.NOON);

        Appointment saved = appointmentRepo.save(a);
        final Long appointmentId = saved.getId();
        final Long patientId = patient.getId();

        assertThatThrownBy(() ->
                appointmentStatusService.changeStatus(appointmentId, AppointmentStatus.CANCELLED, patientId)
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel within 48 hours");
    }

    /** ✅ Huỷ thành công khi còn > 48h */
    @Test
    void testCancelSuccessfully_Before48Hours() {
        Appointment a = new Appointment();
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setSpecialty(specialty);
        a.setStatus(AppointmentStatus.CONFIRMED);
        a.setType(AppointmentType.OFFLINE);
        a.setAppointmentDate(LocalDate.now().plusDays(5)); // >48h
        a.setAppointmentTime(LocalTime.NOON);
        a = appointmentRepo.save(a);

        appointmentStatusService.changeStatus(a.getId(), AppointmentStatus.CANCELLED, patient.getId());
        Appointment updated = appointmentRepo.findById(a.getId()).orElseThrow();

        assertThat(updated.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        // Sau 1 lần hủy thì chưa bị khóa
        User refreshed = userRepo.findById(patient.getId()).orElseThrow();
        assertThat(refreshed.getIsEnable()).isTrue();
    }

    /** ⚠️ Huỷ 2 lần => chưa bị ban */
    @Test
    void testAutoBanAfterTwoCancels_NotYetBanned() {
        List<Appointment> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Appointment a = new Appointment();
            a.setPatient(patient);
            a.setDoctor(doctor);
            a.setSpecialty(specialty);
            a.setStatus(AppointmentStatus.CONFIRMED);
            a.setType(AppointmentType.OFFLINE);
            a.setAppointmentDate(LocalDate.now().plusDays(5 + i)); // >48h
            a.setAppointmentTime(LocalTime.NOON);
            list.add(appointmentRepo.save(a));
        }

        list.forEach(a ->
                appointmentStatusService.changeStatus(a.getId(), AppointmentStatus.CANCELLED, patient.getId())
        );

        User updated = userRepo.findById(patient.getId()).orElseThrow();
        assertThat(updated.getIsEnable()).isTrue(); // chưa bị khoá sau 2 lần
    }

    /** 🚫 Huỷ 3 lần => bị ban tài khoản */
    @Test
    void testAutoBanAfterThreeCancels_Banned() {
        List<Appointment> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Appointment a = new Appointment();
            a.setPatient(patient);
            a.setDoctor(doctor);
            a.setSpecialty(specialty);
            a.setStatus(AppointmentStatus.CONFIRMED);
            a.setType(AppointmentType.OFFLINE);
            a.setAppointmentDate(LocalDate.now().plusDays(5 + i)); // >48h
            a.setAppointmentTime(LocalTime.NOON);
            list.add(appointmentRepo.save(a));
        }

        list.forEach(a ->
                appointmentStatusService.changeStatus(a.getId(), AppointmentStatus.CANCELLED, patient.getId())
        );

        User updated = userRepo.findById(patient.getId()).orElseThrow();
        assertThat(updated.getIsEnable()).isFalse(); // bị khóa sau lần thứ 3
    }
}
