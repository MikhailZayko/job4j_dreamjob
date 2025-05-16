package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateControllerTest {

    @Mock
    private CandidateService candidateService;

    @Mock
    private CityService cityService;

    @InjectMocks
    private CandidateController candidateController;

    private final MultipartFile testFile = new MockMultipartFile("test.img", new byte[]{1, 2, 3});

    @Test
    void whenGetAllThenReturnCandidatesListPage() {
        var candidates = List.of(
                new Candidate(1, "Ivan", "desc1", now(), 1, 1),
                new Candidate(2, "Petr", "desc2", now(), 2, 2)
        );
        when(candidateService.findAll()).thenReturn(candidates);
        var model = new ConcurrentModel();
        var view = candidateController.getAll(model);
        assertThat(view).isEqualTo("candidates/list");
        assertThat(model.getAttribute("candidates")).isEqualTo(candidates);
    }

    @Test
    void whenGetCreatePageThenReturnCreateFormWithCities() {
        var cities = List.of(new City(1, "Москва"), new City(2, "СПб"));
        when(cityService.findAll()).thenReturn(cities);
        var model = new ConcurrentModel();
        var view = candidateController.getCreationPage(model);
        assertThat(view).isEqualTo("candidates/create");
        assertThat(model.getAttribute("cities")).isEqualTo(cities);
    }

    @Test
    void whenCreateCandidateThenRedirect() throws Exception {
        var candidate = new Candidate(0, "Ivan", "desc", now(), 1, 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.save(candidateCaptor.capture(), fileCaptor.capture())).thenReturn(candidate);
        var model = new ConcurrentModel();
        var view = candidateController.create(candidate, testFile, model);
        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(candidateCaptor.getValue()).isEqualTo(candidate);
        assertThat(fileCaptor.getValue()).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    void whenCreateCandidateThrowsExceptionThenReturnErrorPage() throws Exception {
        when(candidateService.save(any(), any())).thenThrow(new RuntimeException("Ошибка загрузки файла"));
        var model = new ConcurrentModel();
        var view = candidateController.create(new Candidate(), testFile, model);
        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Ошибка загрузки файла");
    }

    @Test
    void whenGetByIdThenReturnCandidatePage() {
        var candidate = new Candidate(1, "Ivan", "desc", now(), 1, 1);
        var cities = List.of(new City(1, "Москва"));
        when(candidateService.findById(1)).thenReturn(Optional.of(candidate));
        when(cityService.findAll()).thenReturn(cities);
        var model = new ConcurrentModel();
        var view = candidateController.getById(model, 1);
        assertThat(view).isEqualTo("candidates/one");
        assertThat(model.getAttribute("candidate")).isEqualTo(candidate);
        assertThat(model.getAttribute("cities")).isEqualTo(cities);
    }

    @Test
    void whenGetByIdNotFoundThenReturnErrorPage() {
        when(candidateService.findById(anyInt())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();
        var view = candidateController.getById(model, 100);
        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Кандидат с указанным идентификатором не найден");
    }

    @Test
    void whenUpdateCandidateThenRedirect() throws Exception {
        var candidate = new Candidate(1, "Ivan", "desc", now(), 1, 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateCaptor.capture(), fileCaptor.capture())).thenReturn(true);
        var model = new ConcurrentModel();
        var view = candidateController.update(candidate, testFile, model);
        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(candidateCaptor.getValue()).isEqualTo(candidate);
        assertThat(fileCaptor.getValue()).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    void whenUpdateCandidateFailsThenReturnErrorPage() {
        when(candidateService.update(any(), any())).thenReturn(false);
        var model = new ConcurrentModel();
        var view = candidateController.update(new Candidate(), testFile, model);
        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Кандидат с указанным идентификатором не найден");
    }

    @Test
    void whenUpdateThrowsExceptionThenReturnErrorPage() {
        when(candidateService.update(any(), any())).thenThrow(new RuntimeException("Ошибка обновления"));
        var model = new ConcurrentModel();
        var view = candidateController.update(new Candidate(), testFile, model);
        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Ошибка обновления");
    }

    @Test
    void whenDeleteCandidateSuccessThenRedirect() {
        when(candidateService.deleteById(1)).thenReturn(true);
        var model = new ConcurrentModel();
        var view = candidateController.delete(model, 1);
        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    void whenDeleteCandidateFailsThenReturnErrorPage() {
        when(candidateService.deleteById(1)).thenReturn(false);
        var model = new ConcurrentModel();
        var view = candidateController.delete(model, 1);
        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Кандидат с указанным идентификатором не найден");
    }
}
