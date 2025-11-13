package com.nano.clinicbooking.service.grouphealth;

import com.nano.clinicbooking.dto.EntityConverter;
import com.nano.clinicbooking.dto.request.groupheath.GroupHealthRequestUploadRequest;
import com.nano.clinicbooking.dto.response.groupheath.GroupHealthRequestResponse;
import com.nano.clinicbooking.model.groupheath.GroupHealthRequest;
import com.nano.clinicbooking.model.Specialty;
import com.nano.clinicbooking.repository.grouphealth.GroupHealthRequestRepository;
import com.nano.clinicbooking.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupHealthRequestService {

    private final GroupHealthRequestRepository repository;
    private final SpecialtyRepository specialtyRepository;
    private final EntityConverter<GroupHealthRequest, GroupHealthRequestResponse> converter;

    @Value("${upload.path:uploads/excel}")
    private String uploadDir;

    public GroupHealthRequestResponse uploadGroupHealthRequest(GroupHealthRequestUploadRequest request) throws IOException {
        MultipartFile file = request.getExcelFile();

        // ✅ Lấy thư mục chạy project
        String rootPath = System.getProperty("user.dir");
        File folder = new File(rootPath, uploadDir);
        if (!folder.exists()) folder.mkdirs();

        // ✅ Sinh tên file an toàn
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destination = new File(folder, fileName);
        file.transferTo(destination);

        // ✅ Lấy danh sách tên chuyên khoa từ ID
        List<Specialty> specialties = specialtyRepository.findAllById(request.getSpecialtyIds());
        String departmentNames = specialties.stream()
                .map(Specialty::getName)
                .collect(Collectors.joining(", "));

        // ✅ Tạo entity và lưu DB
        GroupHealthRequest entity = GroupHealthRequest.builder()
                .groupName(request.getGroupName())
                .phoneNumber(request.getPhoneNumber())
                .departments(departmentNames)
                .preferredDate(request.getPreferredDate() != null ? request.getPreferredDate() : LocalDate.now())
                .excelFilePath(uploadDir + "/" + fileName) // dùng đường dẫn tương đối
                .status(GroupHealthRequest.RequestStatus.PENDING)
                .build();

        repository.save(entity);

        return converter.mapEntityToDto(entity, GroupHealthRequestResponse.class);
    }
}
