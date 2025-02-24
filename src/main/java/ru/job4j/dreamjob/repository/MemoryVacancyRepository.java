package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryVacancyRepository implements VacancyRepository {

    private static final MemoryVacancyRepository INSTANCE = new MemoryVacancyRepository();

    private int nextId = 1;

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer",
                "Вакансия стажера для получения практического опыта в разработке программного обеспечения",
                LocalDateTime.of(2025, 1, 9, 12, 30, 59)));
        save(new Vacancy(0, "Junior Java Developer",
                "Вакансия младшего специалиста",
                LocalDateTime.of(2025, 1, 25, 13, 10, 25)));
        save(new Vacancy(0, "Junior+ Java Developer",
                "Вакансия младшего специалист продвинутого уровня",
                LocalDateTime.of(2025, 1, 12, 10, 15, 5)));
        save(new Vacancy(0, "Middle Java Developer",
                "Вакансия специалиста со стажем от 3 лет, который способен самостоятельно и с нуля сделать программу или приложение.",
                LocalDateTime.of(2025, 1, 12, 10, 15, 5)));
        save(new Vacancy(0, "Middle+ Java Developer",
                "Вакансия специалиста уровня Middle с продвинутым опытом",
                LocalDateTime.of(2025, 2, 2, 11, 25, 17)
                ));
        save(new Vacancy(0, "Senior Java Developer",
                "Профессионал с опытом не менее 5 лет, который совмещает обязанности технического руководителя и тимлида в команде программисто",
                LocalDateTime.of(2025, 2, 6, 1, 32, 56)
                ));
    }

    public static MemoryVacancyRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public void deleteById(int id) {
        vacancies.remove(id);
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(oldVacancy.getId(), vacancy.getTitle(),
                        vacancy.getDescription(), vacancy.getCreationDate())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}
