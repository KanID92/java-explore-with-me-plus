package ru.practicum.ewm.dto.event.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<FutureAfterTwoHours, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext constraintValidatorContext) {
        return localDateTime.isAfter(LocalDateTime.now().plusHours(2));
    }

}
