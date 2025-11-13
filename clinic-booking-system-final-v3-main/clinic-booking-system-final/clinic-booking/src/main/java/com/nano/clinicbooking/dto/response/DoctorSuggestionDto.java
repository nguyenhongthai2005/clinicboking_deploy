package com.nano.clinicbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

//dùng riêng cho logic chọn bác sĩ trong khoa đc chọn cơ bản thôi
//DoctorSuggestionDto sẽ gọn hơn, chỉ chứa thông tin cần thiết để hiển thị danh sách đề xuất,
//không kéo theo dữ liệu nặng như slots.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorSuggestionDto {
    private Long doctorId;
    private String doctorName;
    private String specialtyName;
    private Long shiftId;
    private LocalDate shiftDate;
    private String shiftType;
    private Integer maxPatients;
    private Integer slotsAvailable;
}
