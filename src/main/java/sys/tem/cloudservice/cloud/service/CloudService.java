package sys.tem.cloudservice.cloud.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sys.tem.cloudservice.cloud.model.dto.FileName;
import sys.tem.cloudservice.cloud.model.entity.FileData;
import sys.tem.cloudservice.cloud.repository.CloudRepository;
import sys.tem.cloudservice.exception.FileNotSaveException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static sys.tem.cloudservice.CloudserviceApplication.MI;

@Log4j2
@Service
@RequiredArgsConstructor
public class CloudService {
    private final CloudRepository cloudRepository;

    public void saveFile(MultipartFile file) throws IOException {
        if (!cloudRepository.existsByFilename(file.getOriginalFilename())) {
            cloudRepository.save(FileData.builder()
                    .filename(file.getOriginalFilename())
                    .file(file.getBytes())
                    .size(file.getSize())
                    .build());
        }
        if (!cloudRepository.existsByFilename(file.getOriginalFilename())) {
            logNot("Обнавление", file.getOriginalFilename());
            throw new FileNotSaveException("Error input data");
        }
    }

    @Transactional
    public FileData downloadFile(FileName filename) throws FileNotFoundException {
        Optional<FileData> optionalFile = cloudRepository.findFileDataByFilename(filename.filename());
        if (optionalFile.isEmpty()) {
            logNot("Отправка", filename.filename());
            throw new FileNotFoundException("Error input data");
        }
        return optionalFile.get();

    }

    @Transactional
    public void deleteFile(FileName filename) throws FileNotFoundException {
        if (cloudRepository.existsByFilename(filename.filename())) {
            cloudRepository.deleteFileDataByFilename(filename.filename());
        } else {
            logNot("Удаление", filename.filename());
            throw new FileNotFoundException("Error input data");
        }
    }

    public List<FileData> listFiles(int limit) {
        PageRequest page = PageRequest.of(0, limit);
        return cloudRepository.findAll(page).getContent();
    }

    @Transactional
    public void updateFile(FileName filename, FileName newFileName) throws FileNotFoundException {
        Optional<FileData> file = cloudRepository.findFileDataByFilename(filename.filename());
        if (file.isEmpty() || newFileName.filename().isEmpty()) {
            logNot("Обнавление", filename.filename());
            throw new FileNotFoundException("Error input data");
        }
        FileData fileData = file.get();
        fileData.setFilename(newFileName.filename());
        cloudRepository.save(fileData);
    }

    public void logNot(String begin, String filename) {
        log.info(MI, "{} файла {} закончилась неудачей", begin, filename);

    }
}
