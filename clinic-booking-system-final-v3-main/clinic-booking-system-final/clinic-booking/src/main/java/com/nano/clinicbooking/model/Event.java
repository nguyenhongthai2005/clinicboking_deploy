package com.nano.clinicbooking.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    private String location;

    @Column(nullable = false)
    private boolean isEnabled = true;

    // Nếu event có tặng voucher thì liên kết đến voucher mẫu
    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    // Danh sách người tham gia
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<UserEvent> userEvents;


}
