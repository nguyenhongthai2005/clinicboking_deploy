package com.nano.clinicbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nano.clinicbooking.enums.ShiftStatus;
import com.nano.clinicbooking.enums.ShiftType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Table(name="doctor_shifts",
        uniqueConstraints=@UniqueConstraint(columnNames={"doctor_id","date","shift"}))
public class DoctorShift {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="doctor_id", nullable=false)
    @JsonIgnoreProperties({"specialty"})
    @JsonIgnore
    private Doctor doctor;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private ShiftType shift; // MORNING/AFTERNOON/EVENING

    private LocalTime startTime;
    private LocalTime endTime;

    // ✅ Số lượng tối đa bệnh nhân/ca
    private Integer maxPatients = 10;

    // ✅ Tiện ích lặp ca (tùy dùng)
    private Boolean repeatWeekly = false;
    private Integer repeatCount = 0; // số tuần lặp

    private String note;

    private Integer slotDurationMinutes = 45;
    private Integer bufferMinutes = 15;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL)
    private List<ShiftSlot> slots = new ArrayList<>();

    //rạng thái của cái shift đó
    @Enumerated(EnumType.STRING)
    private ShiftStatus status = ShiftStatus.ACTIVE;


    @OneToMany(mappedBy="shift", fetch=FetchType.LAZY)
    @JsonIgnoreProperties({"shift"})
    @JsonIgnore
    private List<Appointment> appointments = new ArrayList<>();
}

