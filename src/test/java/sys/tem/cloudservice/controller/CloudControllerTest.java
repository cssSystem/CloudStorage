package sys.tem.cloudservice.controller;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import sys.tem.cloudservice.cloud.model.dto.FileName;
import sys.tem.cloudservice.cloud.model.entity.FileData;
import sys.tem.cloudservice.cloud.service.CloudService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static sys.tem.cloudservice.CloudserviceApplication.MI;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class CloudControllerTest {
    @Mock
    private CloudService cloudService;
    @InjectMocks
    private CloudController cloudController;
    private FileName fileName = new FileName("test.text");

    @BeforeAll
    public static void initTest() {
        log.info(MI, "---Start {} ---", MethodHandles.lookup().lookupClass().getTypeName());
    }

    @AfterAll
    public static void endTest() {
        log.info(MI, "---End {} ---", MethodHandles.lookup().lookupClass().getTypeName());
    }

    @Test
    @DisplayName("List Files")
    void listFilesTest() {
        int limit = 3;

        List<FileData> mockedFiles = Arrays.asList(
                new FileData(1L, new byte[1], "test1.text", 1L),
                new FileData(2L, new byte[2], "test2.text", 2L),
                new FileData(3L, new byte[3], "test3.text", 3L)
        );

        when(cloudService.listFiles(limit)).thenReturn(mockedFiles);

        // Вызов метода контроллера
        ResponseEntity<List<FileData>> response = cloudController.getAllFiles(limit);

        // Проверка
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test1.text", response.getBody().get(0).getFilename());
        assertEquals("test2.text", response.getBody().get(1).getFilename());


    }

    @Test
    void saveFileTest() throws IOException {
        MultipartFile fileData = new MockMultipartFile("name", "originName", "ContentType", new byte[5]);
        // Вызываем метод контроллера
        ResponseEntity<?> response = cloudController.uploadFile(fileData);

        // Проверяем результаты
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success upload", response.getBody());

    }

    @Test
    void downloadFileTest() throws FileNotFoundException {
        FileData fileData = new FileData(
                1L,
                new byte[5],
                fileName.filename(),
                5L
        );
        when(cloudService.downloadFile(fileName)).thenReturn(fileData);

        // Вызываем метод контроллера
        ResponseEntity<?> response = cloudController.downloadFile(fileName);

        // Проверяем результаты
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("attachment;filename=test.text", response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0));
    }

    @Test
    void deleteFileTest() throws FileNotFoundException {
        // Вызываем метод контроллера
        ResponseEntity<?> response = cloudController.deleteFile(fileName);

        // Проверяем результаты
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success deleted", response.getBody());

    }

    @Test
    void updateFileTest() throws FileNotFoundException {

        // Вызываем метод контроллера
        ResponseEntity<?> response = cloudController.updateFile(fileName, fileName);

        // Проверяем результаты
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success update", response.getBody());

    }

}