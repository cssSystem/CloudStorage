package sys.tem.cloudservice.cloud.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import sys.tem.cloudservice.cloud.model.dto.FileName;
import sys.tem.cloudservice.cloud.model.entity.FileData;
import sys.tem.cloudservice.cloud.repository.CloudRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CloudServiceTest {
    @Mock
    private CloudRepository cloudRepository;
    @InjectMocks
    private CloudService cloudService;

    @Test
    @DisplayName("List Files")
    void listFilesTest() {
        int limit = 3;

        List<FileData> mockedFiles = Arrays.asList(
                new FileData(1L, new byte[1], "ast1.text", 1L),
                new FileData(2L, new byte[2], "ast2.text", 2L),
                new FileData(3L, new byte[3], "ast3.text", 3L)
        );

        when(cloudRepository.findAll(PageRequest.of(0, limit))).thenReturn(new PageImpl<>(mockedFiles));

        // Вызов метода сервиса
        ResponseEntity<List<FileData>> response = (ResponseEntity<List<FileData>>) cloudService.listFiles(limit);

        // Проверка
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ast1.text", response.getBody().get(0).getFilename());
        assertEquals("ast2.text", response.getBody().get(1).getFilename());


    }

    @Test
    void saveFileTest() throws IOException {
        MultipartFile fileData = new MockMultipartFile("name", "originName", "ContentType", new byte[5]);

        when(cloudRepository.existsByFilename(any())).thenReturn(true);

        // Вызываем метод сервиса
        ResponseEntity<?> response = cloudService.saveFile(fileData);

        // Проверяем результаты
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success upload", response.getBody());

        // Проверка при отсутствии файла
        when(cloudRepository.existsByFilename(any())).thenReturn(false);
        assertThrows(FileNotFoundException.class, () -> cloudService.saveFile(fileData));

    }

    @Test
    void downloadFileTest() throws FileNotFoundException {
        FileName fileName = new FileName("test.text");
        Optional<FileData> fileData = Optional.of(new FileData(
                1L,
                new byte[5],
                fileName.filename(),
                5L
        ));
        when(cloudRepository.findFileDataByFilename(any())).thenReturn(fileData);

        // Вызываем метод сервиса
        ResponseEntity<?> response = cloudService.downloadFile(fileName);

        // Проверяем результаты
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("attachment;filename=test.text", response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0));

        // Проверка при отсутствии файла
        when(cloudRepository.findFileDataByFilename(any())).thenReturn(Optional.ofNullable(null));
        assertThrows(FileNotFoundException.class, () -> cloudService.downloadFile(fileName));
    }

    @Test
    void deleteFileTest() throws FileNotFoundException {
        FileName fileName = new FileName("test.text");

        when(cloudRepository.existsByFilename(any())).thenReturn(true);

        // Вызываем метод сервиса
        ResponseEntity<?> response = cloudService.deleteFile(fileName);

        // Проверяем результаты
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success deleted", response.getBody());

        // Проверка при ошибки удаления
        when(cloudRepository.existsByFilename(any())).thenReturn(false);
        assertThrows(FileNotFoundException.class, () -> cloudService.deleteFile(fileName));

    }

    @Test
    void updateFileTest() throws FileNotFoundException {
        FileData fileData = new FileData(1L, new byte[1], "test.text", 1L);
        FileName fileName = new FileName("test.text");
        FileName newFileName = new FileName("newTest.text");

        when(cloudRepository.findFileDataByFilename(any())).thenReturn(Optional.of(fileData));

        // Вызываем метод сервиса
        ResponseEntity<?> response = cloudService.updateFile(fileName, newFileName);

        // Проверяем результаты
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success upload", response.getBody());

        // Проверка при отсутствии файла
        when(cloudRepository.findFileDataByFilename(any())).thenReturn(Optional.ofNullable(null));
        assertThrows(FileNotFoundException.class, () -> cloudService.updateFile(fileName, newFileName));

    }

}