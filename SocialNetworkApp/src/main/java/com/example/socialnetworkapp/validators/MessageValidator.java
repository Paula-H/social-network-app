package com.example.socialnetworkapp.validators;

import com.example.socialnetworkapp.domain.Friendship;
import com.example.socialnetworkapp.domain.Message;

public class MessageValidator implements ValidatorInterface<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        /*Initialize an empty String to collect the errors.*/
        String errors="";

        if( entity.getFrom().getId() < 0 )
            errors += "The ID of the sender cannot be below 0!\n";

        if( entity.getTo().isEmpty())
            errors += "The message must be sent to at least 1 user!\n";

        if( entity.getMessage().isEmpty() )
            errors += "The message cannot be empty!\n";

        if(!errors.isEmpty()) throw new ValidationException(errors);
    }
}
