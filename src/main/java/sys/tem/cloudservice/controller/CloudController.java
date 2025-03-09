package sys.tem.cloudservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sys.tem.cloudservice.cloud.model.dto.FileName;
import sys.tem.cloudservice.cloud.service.CloudService;

import java.io.FileNotFoundException;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class CloudController {

    private final CloudService cloudService;

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return cloudService.saveFile(file);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam("filename") FileName filename) throws FileNotFoundException {
        return cloudService.deleteFile(filename);
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@RequestParam("filename") FileName filename) throws FileNotFoundException {
        return cloudService.downloadFile(filename);
    }

    @PutMapping("/file")
    public ResponseEntity<?> updateFile(@RequestParam("filename") FileName filename,
                                        @RequestBody() FileName newFileName) throws FileNotFoundException {
        return cloudService.updateFile(filename, newFileName);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllFiles(@RequestParam int limit) {
        return cloudService.listFiles(limit);
    }
}
