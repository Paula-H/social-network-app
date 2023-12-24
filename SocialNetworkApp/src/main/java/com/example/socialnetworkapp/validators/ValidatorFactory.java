package com.example.socialnetworkapp.validators;
public class ValidatorFactory{

    private ValidatorFactory(){};


    public static ValidatorInterface createValidator(Strategy strategy) {
        switch (strategy){
            case user -> { return new UserValidator(); }

            case friendship -> { return new FriendshipValidator(); }

            case message -> {return new MessageValidator();}

            default -> { return null; }
        }
    }
}
