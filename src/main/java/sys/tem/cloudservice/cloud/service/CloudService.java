package sys.tem.cloudservice.cloud.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sys.tem.cloudservice.cloud.model.dto.FileName;
import sys.tem.cloudservice.cloud.model.entity.FileData;
import sys.tem.cloudservice.cloud.repository.CloudRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CloudService {
    private final CloudRepository cloudRepository;

    public ResponseEntity<?> saveFile(MultipartFile file) throws IOException {
        if (!cloudRepository.existsByFilename(file.getOriginalFilename())) {
            cloudRepository.save(FileData.builder()
                    .filename(file.getOriginalFilename())
                    .file(file.getBytes())
                    .size(file.getSize())
                    .build());
        }
        if (!cloudRepository.existsByFilename(file.getOriginalFilename())) {
            throw new FileNotFoundException("Error input data");
        }
        return ResponseEntity.ok().body("Success upload");
    }

    @Transactional
    public ResponseEntity<?> downloadFile(FileName filename) throws FileNotFoundException {
        Optional<FileData> optionalFile = cloudRepository.findFileDataByFilename(filename.filename());
        if (optionalFile.isEmpty()) {
            throw new FileNotFoundException("Error input data");
        }
        FileData fileData = optionalFile.get();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileData.getFilename())
                .body(fileData.getFile());

    }

    @Transactional
    public ResponseEntity<?> deleteFile(FileName filename) throws FileNotFoundException {
        if (cloudRepository.existsByFilename(filename.filename())) {
            cloudRepository.deleteFileDataByFilename(filename.filename());
        } else {
            throw new FileNotFoundException("Error input data");
        }
        return ResponseEntity.ok().body("Success deleted");
    }

    public ResponseEntity<?> listFiles(int limit) {
        PageRequest page = PageRequest.of(0, limit);
        var files = cloudRepository.findAll(page).getContent();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(files);
    }

    @Transactional
    public ResponseEntity<?> updateFile(FileName filename, FileName newFileName) throws FileNotFoundException {
        Optional<FileData> file = cloudRepository.findFileDataByFilename(filename.filename());
        if (file.isEmpty() || newFileName.filename().isEmpty()) {
            throw new FileNotFoundException("Error input data");
        }
        FileData fileData = file.get();
        fileData.setFilename(newFileName.filename());
        cloudRepository.save(fileData);
        return ResponseEntity.ok().body("Success upload");
    }
}
