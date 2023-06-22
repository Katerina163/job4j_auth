package ru.job4j.persons.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.persons.model.Person;
import ru.job4j.persons.repository.PersonRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
@AllArgsConstructor
public class SimplePersonService implements PersonService, UserDetailsService {
    private final PersonRepository repository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        var user = repository.findByLogin(login);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(login);
        }
        return new User(user.get().getLogin(), user.get().getPassword(), emptyList());
    }

    @Override
    public List<Person> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Person> findById(int id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Person> save(Person person) {
        return Optional.of(repository.save(person));
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Person> modify(Person person) throws InvocationTargetException, IllegalAccessException {
        Person current = repository.findById(person.getId()).orElseThrow(() -> new NullPointerException("Invalid id"));
        var methods = current.getClass().getDeclaredMethods();
        var namePerMethod = new HashMap<String, Method>();
        for (var method: methods) {
            var name = method.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                namePerMethod.put(name, method);
            }
        }
        for (var name : namePerMethod.keySet()) {
            if (name.startsWith("get")) {
                var getMethod = namePerMethod.get(name);
                var setMethod = namePerMethod.get(name.replace("get", "set"));
                if (setMethod == null) {
                    throw new IllegalArgumentException(
                            "Impossible invoke set method from object : " + current + ", Check set and get pairs.");
                }
                var newValue = getMethod.invoke(person);
                if (newValue != null) {
                    setMethod.invoke(current, newValue);
                }
            }
        }
        return Optional.of(repository.save(current));
    }
}
