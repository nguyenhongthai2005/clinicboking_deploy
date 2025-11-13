package com.nano.clinicbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "doctor_id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Doctor extends User {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialty_id", nullable = false)
    @JsonIgnoreProperties({ "doctors"})
    private Specialty specialty;

    private String degree;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    private String experience;
    private Boolean isVip = false;
    private Double consultationFee = 0.0;
}
