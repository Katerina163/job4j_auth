package ru.job4j.persons.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.persons.model.Person;
import ru.job4j.persons.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService service;
    private final BCryptPasswordEncoder encoder;

    public PersonController(PersonService simplePersonService, BCryptPasswordEncoder encoder) {
        service = simplePersonService;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> signUp(@RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        var register = service.save(person);
        if (register.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/")
    public List<Person> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = service.findById(id);
        return new ResponseEntity<>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        var result = service.save(person);
        return result.map(value -> new ResponseEntity<>(
                value,
                HttpStatus.CREATED
        )).orElseGet(() -> new ResponseEntity<>(
                person,
                HttpStatus.CONFLICT
        ));
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        var update = service.findById(person.getId());
        if (update.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        service.save(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        var delete = service.findById(id);
        if (delete.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
