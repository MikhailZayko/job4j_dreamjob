package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Петров Иван Васильевич",
                "Имеет опыт разработки в C++ более 10 лет, Java 2 года",
                LocalDateTime.of(2025, 1, 9, 12, 30, 59)));
        save(new Candidate(0, "Быкова Мария Игоревна",
                "Java-разработчик, 3 года в биллинговой компании",
                LocalDateTime.of(2025, 1, 25, 13, 10, 25)));
        save(new Candidate(0, "Васильчиков Тарас Сергеевич",
                "Начинающий разработки на Java",
                LocalDateTime.of(2025, 1, 12, 10, 15, 5)));
        save(new Candidate(0, "Огонькова Любовь Ивановна",
                "Опыта работы в банке Java-разработчиком более 4 лет",
                LocalDateTime.of(2025, 1, 12, 10, 15, 5)));
        save(new Candidate(0, "Кукушкин Николай Константинович",
                "Java-разработчик более 7 лет",
                LocalDateTime.of(2025, 2, 2, 11, 25, 17)
        ));
        save(new Candidate(0, "Круглова Анна Алексеевна",
                "Разработчик на Java более 2 лет, Pyhton более 3 лет",
                LocalDateTime.of(2025, 2, 6, 1, 32, 56)
        ));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id, candidates.get(id));
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> new Candidate(oldCandidate.getId(), candidate.getName(),
                        candidate.getDescription(), candidate.getCreationDate())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
