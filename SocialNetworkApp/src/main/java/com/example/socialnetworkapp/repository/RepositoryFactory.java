package com.example.socialnetworkapp.repository;

import java.sql.SQLException;

public class RepositoryFactory {

    static String url = "jdbc:postgresql://localhost:5432/socialnetwork";
    static String username = "postgres";
    static String password = "1";
    private RepositoryFactory(){}

    public static Repository createRepository(RepositoryType type) throws SQLException, ClassNotFoundException {
        switch (type){

            case user -> { return new UserPageableRepository(url,username,password);}

            case friendship -> { return new FriendshipPageableRepository(url,username,password); }

            case friendship_request -> { return new FriendshipRequestPageableRepository(url,username,password); }

            case message -> { return new MessageRepository(url,username,password); }
            default -> { return null; }
        }
    }
}
