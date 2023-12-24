package com.example.socialnetworkapp.repository;

import com.example.socialnetworkapp.validators.Strategy;
import com.example.socialnetworkapp.validators.ValidatorFactory;
import com.example.socialnetworkapp.validators.ValidatorInterface;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AbstractRepository<Entity> implements Serializable {
    protected ValidatorInterface<Entity> validator;

    protected String url;

    protected String username;

    protected String password;

    protected Connection connection;

    protected AbstractRepository(Strategy strategy,String url, String username, String password) throws SQLException {

        this.validator = ValidatorFactory.createValidator(strategy);

        this.url = url;

        this.username = username;

        this.password = password;

        this.connection = DriverManager.getConnection(url, username, password);

    }


}
