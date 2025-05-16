package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileController fileController;

    @Test
    void whenGetByIdFoundThenReturnContent() {
        byte[] expectedContent = {1, 2, 3};
        var fileDto = new FileDto("file.jpg", expectedContent);
        when(fileService.getFileById(1)).thenReturn(Optional.of(fileDto));
        ResponseEntity<?> response = fileController.getById(1);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedContent);
    }

    @Test
    void whenGetByIdNotFoundThenReturn404() {
        when(fileService.getFileById(1)).thenReturn(Optional.empty());
        ResponseEntity<?> response = fileController.getById(1);
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }
}
