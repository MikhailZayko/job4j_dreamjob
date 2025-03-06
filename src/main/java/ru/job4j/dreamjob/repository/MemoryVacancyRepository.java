package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class MemoryVacancyRepository implements VacancyRepository {

    private final AtomicInteger id = new AtomicInteger(1);

    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    public MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer",
                "Вакансия стажера для получения практического опыта в разработке программного обеспечения",
                LocalDateTime.of(2025, 1, 9, 12, 30, 59), true));
        save(new Vacancy(0, "Junior Java Developer",
                "Вакансия младшего специалиста",
                LocalDateTime.of(2025, 1, 25, 13, 10, 25), false));
        save(new Vacancy(0, "Junior+ Java Developer",
                "Вакансия младшего специалист продвинутого уровня",
                LocalDateTime.of(2025, 1, 12, 10, 15, 5), true));
        save(new Vacancy(0, "Middle Java Developer",
                "Вакансия специалиста со стажем от 3 лет, который способен самостоятельно и с нуля сделать программу или приложение.",
                LocalDateTime.of(2025, 1, 12, 10, 15, 5), false));
        save(new Vacancy(0, "Middle+ Java Developer",
                "Вакансия специалиста уровня Middle с продвинутым опытом",
                LocalDateTime.of(2025, 2, 2, 11, 25, 17), true
        ));
        save(new Vacancy(0, "Senior Java Developer",
                "Профессионал с опытом не менее 5 лет, который совмещает обязанности технического руководителя и тимлида в команде программисто",
                LocalDateTime.of(2025, 2, 6, 1, 32, 56), true
        ));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(id.getAndIncrement());
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id, vacancies.get(id));
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(
                vacancy.getId(), (id, oldVacancy) -> new Vacancy(
                        oldVacancy.getId(),
                        vacancy.getTitle(),
                        vacancy.getDescription(),
                        vacancy.getCreationDate(),
                        vacancy.getVisible())
        ) != null;
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
