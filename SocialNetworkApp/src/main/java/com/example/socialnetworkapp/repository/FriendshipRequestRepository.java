package com.example.socialnetworkapp.repository;

import com.example.socialnetworkapp.domain.FriendshipRequest;
import com.example.socialnetworkapp.domain.Tuple;
import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.validators.Strategy;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipRequestRepository extends AbstractRepository<FriendshipRequest> implements Repository<Tuple<Long, Long>, FriendshipRequest> {

    public FriendshipRequestRepository(String url, String username, String password) throws SQLException {
        super(Strategy.friendship, url, username, password);
    }

    User findUser(Long id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");

        statement.setInt(1, Math.toIntExact(id));
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String username_user = resultSet.getString("username");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            User u = new User(firstName, lastName, username_user, email, password);
            u.setId(id);
            return u;
        }
        return null;
    }

    @Override
    public Optional<FriendshipRequest> findOne(Tuple<Long, Long> id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("select * from friendship_requests where (from_id,to_id) = (?,?)");

        statement.setInt(1, Math.toIntExact(id.getLeft()));
        statement.setInt(2, Math.toIntExact(id.getRight()));
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            String status = resultSet.getString("status");
            User u1 = findUser(id.getLeft());
            User u2 = findUser(id.getRight());
            FriendshipRequest fr = new FriendshipRequest(u1, u2);
            fr.setStatus(status);
            fr.setId(id);
            return Optional.of(fr);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<FriendshipRequest> findAll() throws SQLException {
        Set<FriendshipRequest> friendshipRequests = new HashSet<>();
        PreparedStatement statement = connection.prepareStatement("select * from friendship_requests");

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Long from_id = resultSet.getLong("from_id");
            Long to_id = resultSet.getLong("to_id");
            String status = resultSet.getString("status");
            User u1 = findUser(from_id);
            User u2 = findUser(to_id);
            FriendshipRequest fr = new FriendshipRequest(u1, u2);
            fr.setStatus(status);
            fr.setId(new Tuple<>(from_id, to_id));
            friendshipRequests.add(fr);
        }
        return friendshipRequests;
    }

    @Override
    public Optional<FriendshipRequest> save(FriendshipRequest entity) throws SQLException {
        if (entity == null) {
            throw new RepositoryException("id must not be null");
        }
        String deleteSQL = "insert into friendship_requests(from_id,to_id) values (?,?)";

        PreparedStatement statement = connection.prepareStatement(deleteSQL);
        statement.setLong(1, entity.getId().getLeft());
        statement.setLong(2, entity.getId().getRight());
        Optional<FriendshipRequest> fr = findOne(entity.getId());
        int answer = 0;
        if (fr.isEmpty()) {
            answer = statement.executeUpdate();
        }
        return answer == 0 ? Optional.empty() : fr;

    }

    @Override
    public Optional<FriendshipRequest> delete(Tuple<Long, Long> id) throws SQLException {
        if (id == null) {
            throw new RepositoryException("id must not be null");
        }
        String deleteSQL = "delete from friendship_requests where (from_id,to_id)=(?,?)";


        PreparedStatement statement = connection.prepareStatement(deleteSQL);
        statement.setLong(1, id.getLeft());
        statement.setLong(2, id.getRight());
        Optional<FriendshipRequest> fr = findOne(id);
        int answer = 0;
        if (fr.isPresent()) {
            answer = statement.executeUpdate();
        }
        return answer == 0 ? fr : Optional.empty();

    }

    @Override
    public Optional<FriendshipRequest> update(FriendshipRequest entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null!");
        }

        String updateSQL = "update friendship_requests set status=? where (from_id,to_id)=(?,?)";
        PreparedStatement statement = connection.prepareStatement(updateSQL);
        statement.setString(1, entity.getStatus());
        statement.setLong(2, entity.getFrom().getId());
        statement.setLong(3, entity.getTo().getId());
        int answer = statement.executeUpdate();
        return answer == 0 ? Optional.empty() : Optional.of(entity);
    }


}
