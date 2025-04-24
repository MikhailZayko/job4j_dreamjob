package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepository() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");
        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (Connection connection = sql2o.open()) {
            connection.createQuery("DELETE FROM users").executeUpdate();
        }
    }

    @Test
    public void whenSaveOneUser() {
        User user = new User(1, "123@ya.ru", "Name", "123");
        User savedUser = sql2oUserRepository.save(user).get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenSaveTwoUsersWithDifferentEmails() {
        Optional<User> firstUser = sql2oUserRepository.save(
                new User(1, "123@ya.ru", "Name1", "123"));
        Optional<User> secondUser = sql2oUserRepository.save(
                new User(2, "1234@ya.ru", "Name2", "1234")
        );
        assertThat(firstUser).isNotEmpty();
        assertThat(secondUser).isNotEmpty();
    }

    @Test
    public void whenSaveTwoUsersWithTheSameEmails() {
        Optional<User> firstUser = sql2oUserRepository.save(
                new User(1, "123@ya.ru", "Name1", "123"));
        Optional<User> secondUser = sql2oUserRepository.save(
                new User(2, "123@ya.ru", "Name2", "1234")
        );
        assertThat(firstUser).isNotEmpty();
        assertThat(secondUser).isEmpty();
    }

    @Test
    public void whenFindByEmailAndPasswordThenReturnUser() {
        User user = sql2oUserRepository.save(
                new User(1, "123@ya.ru", "Name1", "123")).get();
        User foundUser = sql2oUserRepository.findByEmailAndPassword("123@ya.ru", "123").get();
        assertThat(foundUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenFindByEmailAndPasswordThenReturnEmpty() {
        sql2oUserRepository.save(new User(1, "123@ya.ru", "Name1", "123"));
        assertThat(sql2oUserRepository.findByEmailAndPassword("222", "222")).isEmpty();
    }
}