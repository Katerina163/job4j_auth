package ru.job4j.persons.service;

import ru.job4j.persons.model.Person;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

public interface PersonService {
    List<Person> findAll();

    Optional<Person> findById(int id);

    Optional<Person> save(Person person);

    void delete(int id);

    Optional<Person> modify(Person person) throws InvocationTargetException, IllegalAccessException;
}
