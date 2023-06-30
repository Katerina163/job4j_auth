package ru.job4j.persons.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class PersonDto {
    @NotNull(message = "id не должен отсутствовать")
    private int id;

    @Length(min = 3, max = 20, message = "Пароль должен быть больше 3 и меньше 20 символов")
    private String password;
}
