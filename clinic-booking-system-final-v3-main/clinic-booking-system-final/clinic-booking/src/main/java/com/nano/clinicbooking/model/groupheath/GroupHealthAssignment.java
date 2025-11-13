package com.nano.clinicbooking.model.groupheath;


import com.nano.clinicbooking.enums.ResultStatus;
import com.nano.clinicbooking.model.*;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "group_health_assignments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"request_id", "doctor_id", "shift_id", "specialty_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupHealthAssignment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "request_id")
    private GroupHealthRequest request;


    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "specialty_id")
    private Specialty specialty;


    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "doctor_id")
    private Doctor doctor;


    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "shift_id")
    private DoctorShift shift;


    private Integer capacityAllocated;
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "result_status")
    private ResultStatus resultStatus; // NONE, UPLOADED, APPROVED, REJECTED

    @Column(name = "result_file_path")
    private String resultFilePath;
}