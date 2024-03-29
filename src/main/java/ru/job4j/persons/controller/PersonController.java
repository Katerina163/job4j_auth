package ru.job4j.persons.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.persons.dto.PersonDto;
import ru.job4j.persons.model.Person;
import ru.job4j.persons.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService service;
    private final BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper;

    public PersonController(PersonService simplePersonService, BCryptPasswordEncoder encoder, ObjectMapper objectMapper) {
        service = simplePersonService;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> signUp(@Valid @RequestBody Person person) {
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
    public ResponseEntity<Person> findById(@Min(value = 0) @PathVariable int id) {
        var person = service.findById(id);
        return new ResponseEntity<>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
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
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        var update = service.findById(person.getId());
        if (update.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        service.save(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Min(value = 0) @PathVariable int id) {
        var delete = service.findById(id);
        if (delete.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Person> modify(@Valid @RequestBody PersonDto person)
            throws InvocationTargetException, IllegalAccessException {
        person.setPassword(encoder.encode(person.getPassword()));
        var personOpt = service.modify(person);
        return new ResponseEntity<>(
                personOpt.orElse(new Person()),
                personOpt.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(value = {SQLException.class})
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {
            {
                put("message", "Some fields are incorrect");
                put("details", "This login already exists");
            }
        }));
        log.error(e.getLocalizedMessage());
    }
}
