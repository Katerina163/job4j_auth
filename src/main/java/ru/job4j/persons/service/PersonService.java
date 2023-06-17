package ru.job4j.persons.service;

import ru.job4j.persons.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    List<Person> findAll();

    Optional<Person> findById(int id);

    Person save(Person person);

    void delete(int id);
}
