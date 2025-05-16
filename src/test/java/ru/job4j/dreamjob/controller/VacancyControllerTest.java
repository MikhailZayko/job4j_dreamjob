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
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {

    @Mock
    private VacancyService vacancyService;

    @Mock
    private CityService cityService;

    @InjectMocks
    private VacancyController vacancyController;

    private final MultipartFile testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});

    @Test
    public void whenRequestVacancyListPageThenGetPageWithVacancies() {
        var vacancy1 = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var vacancy2 = new Vacancy(2, "test2", "desc2", now(), false, 3, 4);
        var expectedVacancies = List.of(vacancy1, vacancy2);
        when(vacancyService.findAll()).thenReturn(expectedVacancies);
        var model = new ConcurrentModel();
        var view = vacancyController.getAll(model);
        var actualVacancies = model.getAttribute("vacancies");
        assertThat(view).isEqualTo("vacancies/list");
        assertThat(actualVacancies).isEqualTo(expectedVacancies);
    }

    @Test
    public void whenRequestVacancyCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);
        var model = new ConcurrentModel();
        var view = vacancyController.getCreationPage(model);
        var actualVacancies = model.getAttribute("cities");
        assertThat(view).isEqualTo("vacancies/create");
        assertThat(actualVacancies).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.save(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(vacancy);
        var model = new ConcurrentModel();
        var view = vacancyController.create(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();
        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);

    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(vacancyService.save(any(), any())).thenThrow(expectedException);
        var model = new ConcurrentModel();
        var view = vacancyController.create(new Vacancy(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");
        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenRequestVacancyPageByIdThenGetVacancyPageWithCities() {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var expectedCities = List.of(new City(1, "Москва"), new City(2, "Санкт-Петербург"));
        when(vacancyService.findById(1)).thenReturn(Optional.of(vacancy));
        when(cityService.findAll()).thenReturn(expectedCities);
        var model = new ConcurrentModel();
        var view = vacancyController.getById(model, 1);
        assertThat(view).isEqualTo("vacancies/one");
        assertThat(model.getAttribute("vacancy")).usingRecursiveComparison().isEqualTo(vacancy);
        assertThat(model.getAttribute("cities")).isEqualTo(expectedCities);
    }

    @Test
    public void whenVacancyNotFoundThenGetErrorPageWithMessage() {
        when(vacancyService.findById(anyInt())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();
        var view = vacancyController.getById(model, 1);
        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Вакансия с указанным идентификатором не найдена");
    }

    @Test
    public void whenPostUpdateVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.update(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);
        var model = new ConcurrentModel();
        var view = vacancyController.update(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();
        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenPostVacancyUpdateAndNotFoundThenGetErrorPage() {
        var vacancy = new Vacancy(1, "test1", "desc", now(), true, 1, 1);
        when(vacancyService.update(any(), any())).thenReturn(false);
        var model = new ConcurrentModel();
        var view = vacancyController.update(vacancy, testFile, model);
        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Вакансия с указанным идентификатором не найдена");
    }

    @Test
    public void whenSomeExceptionThrownAfterUpdateThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to update file");
        when(vacancyService.update(any(), any())).thenThrow(expectedException);
        var model = new ConcurrentModel();
        var view = vacancyController.update(new Vacancy(), testFile, model);
        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenRequestVacancyDeleteAndSuccessThenRedirectToVacanciesPage() {
        when(vacancyService.deleteById(anyInt())).thenReturn(true);
        var model = new ConcurrentModel();
        var view = vacancyController.delete(model, 1);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenRequestVacancyDeleteAndNotFoundThenGetErrorPage() {
        when(vacancyService.deleteById(anyInt())).thenReturn(false);
        var model = new ConcurrentModel();
        var view = vacancyController.delete(model, 1);
        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Вакансия с указанным идентификатором не найдена");
    }
}