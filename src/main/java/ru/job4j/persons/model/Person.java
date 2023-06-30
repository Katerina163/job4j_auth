package ru.job4j.persons.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "person")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Person {
    @NotNull(message = "id не должен быть пустым")
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Length(min = 3, max = 20, message = "Логин должен быть больше 3 и меньше 20 символов")
    private String login;

    @Length(min = 3, max = 20, message = "Пароль должен быть больше 3 и меньше 20 символов")
    private String password;
}
