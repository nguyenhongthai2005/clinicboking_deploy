package com.nano.clinicbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGrowthStatsDto {
    private long newUsersThisWeek;
    private long newUsersLastWeek;
    private long totalVisitsThisWeek;
    private long totalVisitsLastWeek;
    private double growthPercentage;
}
