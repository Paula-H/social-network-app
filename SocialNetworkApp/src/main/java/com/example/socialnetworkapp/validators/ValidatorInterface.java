package com.example.socialnetworkapp.validators;

public interface ValidatorInterface<T> {
    void validate(T entity) throws ValidationException;
}