package ru.job4j.persons.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.persons.model.Person;
import ru.job4j.persons.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final PersonService service;
    private final BCryptPasswordEncoder encoder;

    public UserController(PersonService simplePersonService, BCryptPasswordEncoder encoder) {
        service = simplePersonService;
        this.encoder = encoder;
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody Person person) {
        System.out.println(person);
        person.setPassword(encoder.encode(person.getPassword()));
        service.save(person);
    }

    @GetMapping("/all")
    public List<Person> findAll() {
        return service.findAll();
    }
}