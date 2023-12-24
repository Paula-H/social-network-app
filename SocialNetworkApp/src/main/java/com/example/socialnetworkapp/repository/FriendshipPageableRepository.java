package com.example.socialnetworkapp.repository;

import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.repository.paging.Page;
import com.example.socialnetworkapp.repository.paging.PageImplementation;
import com.example.socialnetworkapp.repository.paging.Pageable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendshipPageableRepository extends FriendshipRepository{
    public FriendshipPageableRepository(String url, String username, String password) throws SQLException, ClassNotFoundException {
        super(url, username, password);
    }

    public Page<User> friendsForAnUser(Pageable pageable, Long id) throws SQLException {
        List<User> friends = new ArrayList<>();
        if(id == null || id <= 0 ){
            throw new IllegalArgumentException("The ID cannot be null!");
        }
        String SQLcommand = "SELECT u.* FROM users u JOIN friendships f ON u.id = f.id_user1 OR u.id = f.id_user2 WHERE (f.id_user1 = ? OR f.id_user2 = ?) AND u.id <> ? LIMIT ? OFFSET ?";
        PreparedStatement statement = connection.prepareStatement(SQLcommand);
        statement.setLong(1, id);
        statement.setLong(2, id);
        statement.setLong(3, id);
        statement.setInt(4,pageable.getPageSize());
        statement.setInt(5,(pageable.getPageNumber()-1)*pageable.getPageSize());
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Long user_id = resultSet.getLong("id");
            String first_name = resultSet.getString("first_name");
            String last_name = resultSet.getString("last_name");
            String username = resultSet.getString("username");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            User u = new User(first_name,last_name,username,email,password);
            u.setId(user_id);
            friends.add(u);
        }
        return new PageImplementation<>(pageable,friends.stream());
    }
}
