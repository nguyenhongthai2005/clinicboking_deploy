package com.nano.clinicbooking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "patient_id")
public class Patient extends User {

}
