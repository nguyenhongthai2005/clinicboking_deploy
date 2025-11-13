package com.nano.clinicbooking.model;

import com.nano.clinicbooking.enums.UserEventStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_events")
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User nào đăng ký
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Event nào
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private LocalDateTime registeredAt = LocalDateTime.now();

    // Trạng thái tham gia: PENDING (chưa đến), CONFIRMED (đã tham gia)
    @Enumerated(EnumType.STRING)
    private UserEventStatus status = UserEventStatus.PENDING;
}
