package com.nano.clinicbooking.dto.request.groupheath;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
public class GroupHealthRequestUploadRequest {
    private String groupName;
    private String phoneNumber;
    private List<Long> specialtyIds; // ID chuyên khoa chọn
    private LocalDate preferredDate;
    private MultipartFile excelFile; // file danh sách khám
}
