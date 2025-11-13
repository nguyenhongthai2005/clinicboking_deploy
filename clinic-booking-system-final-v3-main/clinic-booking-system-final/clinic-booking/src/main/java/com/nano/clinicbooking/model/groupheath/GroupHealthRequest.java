package com.nano.clinicbooking.model.groupheath;

import com.nano.clinicbooking.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_health_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupHealthRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupName;

    private String phoneNumber;

    private String departments; // VD: "Khoa Nội, Khoa Mắt"

    private LocalDate preferredDate;

    private String excelFilePath;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy; // có thể null nếu khách ngoài

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum RequestStatus {
        PENDING, APPROVED, REJECTED, COMPLETED
    }
}
