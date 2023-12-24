package com.example.socialnetworkapp.repository;

import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.repository.paging.Page;
import com.example.socialnetworkapp.repository.paging.PageImplementation;
import com.example.socialnetworkapp.repository.paging.Pageable;
import java.sql.*;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * UserPageableRepository : class
 *  - extension for the UserRepository class
 *  - adds 1 new method that returns a Page with certain users, as dictated by page number and size
 */
public class UserPageableRepository extends UserRepository{

    public UserPageableRepository(String url, String username, String password) throws SQLException, ClassNotFoundException {
        super(url, username, password);
    }

    public Page<User> findAll(Pageable pageable) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("select * from users limit ? offset ?");
        statement.setInt(1,pageable.getPageSize());
        statement.setInt(2,(pageable.getPageNumber()-1)*pageable.getPageSize());
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next())
        {
            Long id= resultSet.getLong("id");
            String first_name=resultSet.getString("first_name");
            String last_name=resultSet.getString("last_name");
            String username=resultSet.getString("username");
            String email=resultSet.getString("email");
            String password=resultSet.getString("password");
            User user=new User(first_name,last_name,username,email,password);
            user.setId(id);
            users.add(user);
        }
        Stream<User> userStream = users.stream();
        return new PageImplementation(pageable,userStream);
    }
}
