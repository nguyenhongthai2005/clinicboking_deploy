package com.nano.clinicbooking.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEventResponse {
    private Long id;
    private Long userId;
    private Long eventId;
    private String eventName;
    private LocalDateTime registeredAt;
    private String status;
}
