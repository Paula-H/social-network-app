package com.example.socialnetworkapp.repository;

import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.repository.paging.Page;
import com.example.socialnetworkapp.repository.paging.PageImplementation;
import com.example.socialnetworkapp.repository.paging.Pageable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class FriendshipRequestPageableRepository extends FriendshipRequestRepository {

    public FriendshipRequestPageableRepository(String url, String username, String password) throws SQLException {
        super(url, username, password);
    }

    public Page<User> allPossibleFriends(Pageable pageable,Long id) throws SQLException {
        if(id == null || id <= 0 ){
            throw new IllegalArgumentException("The ID cannot be null!");
        }

        Set<User> possibleFriends = new HashSet<>();

        String SQLcommand = "select u.id FROM users u WHERE u.id <> ? AND u.id NOT IN " +
                "(SELECT id_user1 AS user_id FROM friendships WHERE id_user2 = ? UNION SELECT id_user2 AS user_id " +
                "FROM friendships WHERE id_user1 = ? UNION SELECT from_id AS user_id FROM friendship_requests WHERE " +
                "to_id = ? AND status = 'pending' UNION SELECT to_id AS user_id FROM friendship_requests WHERE " +
                "from_id = ? AND status = 'pending' ) OR u.id IN ( SELECT from_id AS user_id FROM friendship_requests " +
                "WHERE to_id = ? AND status = 'declined' UNION SELECT to_id AS user_id FROM friendship_requests WHERE " +
                "from_id = ? AND status = 'declined') limit ? offset ?";
        PreparedStatement statement = connection.prepareStatement(SQLcommand);
        statement.setLong(1, id);
        statement.setLong(2, id);
        statement.setLong(3, id);
        statement.setLong(4, id);
        statement.setLong(5, id);
        statement.setLong(6, id);
        statement.setLong(7, id);
        statement.setInt(8,pageable.getPageSize());
        statement.setInt(9,(pageable.getPageNumber()-1)*pageable.getPageSize());
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Long user_id = resultSet.getLong("id");
            User u = findUser(user_id);
            possibleFriends.add(u);
        }
        return new PageImplementation<>(pageable,possibleFriends.stream());
    }

}
