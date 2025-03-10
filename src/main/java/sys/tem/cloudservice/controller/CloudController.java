package sys.tem.cloudservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sys.tem.cloudservice.cloud.model.dto.FileName;
import sys.tem.cloudservice.cloud.model.entity.FileData;
import sys.tem.cloudservice.cloud.service.CloudService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static sys.tem.cloudservice.CloudserviceApplication.MI;

@Log4j2
@RequiredArgsConstructor
@RestController
public class CloudController {

    private final CloudService cloudService;

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        log.info(MI, "Загрузка файла {}", file.getOriginalFilename());
        cloudService.saveFile(file);
        log.info(MI, "Файл {} успешно загружен", file.getOriginalFilename());
        return ResponseEntity.ok()
                .body("Success upload");
    }

    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestParam("filename") FileName filename) throws FileNotFoundException {
        log.info(MI, "Удаление файла {}", filename.filename());
        cloudService.deleteFile(filename);
        log.info(MI, "Файл {} успешно удален", filename.filename());
        return ResponseEntity.ok()
                .body("Success deleted");
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("filename") FileName filename) throws FileNotFoundException {
        log.info(MI, "Отправка файла {}", filename.filename());
        FileData fileData = cloudService.downloadFile(filename);
        log.info(MI, "Файл {} успешно отправлен", filename.filename());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileData.getFilename())
                .body(fileData.getFile());
    }

    @PutMapping("/file")
    public ResponseEntity<String> updateFile(@RequestParam("filename") FileName filename,
                                             @RequestBody() FileName newFileName) throws FileNotFoundException {
        log.info(MI, "Переименование файла {}", filename.filename());
        cloudService.updateFile(filename, newFileName);
        log.info(MI, "Файл {} успешно переименован", filename.filename());
        return ResponseEntity.ok()
                .body("Success update");
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileData>> getAllFiles(@RequestParam int limit) {
        log.info(MI, "Выгрузка списка файлов");
        return ResponseEntity.ok()
                .body(cloudService.listFiles(limit));

    }
}
