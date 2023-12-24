package com.example.socialnetworkapp.validators;


import com.example.socialnetworkapp.domain.User;

public class UserValidator implements ValidatorInterface<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        /*Initialize an empty String to collect the errors.*/
        String errors="";

        if(entity.getFirst_name().isEmpty()) errors+="The first name field cannot be empty!\n";

        if(entity.getLast_name().isEmpty()) errors+="The last name field cannot be empty!\n";

        if(entity.getUsername().isEmpty()) errors+="The username field cannot be empty!\n";

        if(entity.getEmail().isEmpty()) errors+="The email field cannot be empty!\n";

        if(!entity.getEmail().matches("\\w+@\\w+\\.com$")) errors+="The input email is not in a correct format!\n";

        if(entity.getPassword().isEmpty()) errors+="The password field cannot be empty!\n";

        if(!errors.isEmpty()) throw new ValidationException(errors);
    }
}