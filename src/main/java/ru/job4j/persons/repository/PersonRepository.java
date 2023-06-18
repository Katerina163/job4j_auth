package ru.job4j.persons.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.job4j.persons.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person, Integer> {
    @NonNull
    List<Person> findAll();

    Optional<Person> findById(int id);

    @NonNull
    Person save(@NonNull Person person);

    Optional<Person> findByLogin(String login);
}
