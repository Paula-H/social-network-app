package com.example.socialnetworkapp.repository;

import com.example.socialnetworkapp.domain.Friendship;
import com.example.socialnetworkapp.domain.Tuple;
import com.example.socialnetworkapp.validators.Strategy;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipRepository extends AbstractRepository<Friendship> implements Repository<Tuple<Long, Long>, Friendship> {
    public FriendshipRepository(String url, String username, String password) throws SQLException, ClassNotFoundException {
        super(Strategy.friendship, url, username, password);

    }

    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("select * from friendships where (id_user1,id_user2) = (?,?)");

        statement.setInt(1, Math.toIntExact(id.getLeft()));
        statement.setInt(2, Math.toIntExact(id.getRight()));
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            Timestamp localDateTime = resultSet.getTimestamp("date_friendship");
            Friendship f = new Friendship(id.getLeft(), id.getRight());
            f.setFriendsFrom(localDateTime.toLocalDateTime());
            return Optional.ofNullable(f);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Friendship> findAll() throws SQLException {
        Set<Friendship> friendships = new HashSet<>();
        PreparedStatement statement = connection.prepareStatement("select * from friendships");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Long id_user1 = resultSet.getLong("id_user1");
            Long id_user2 = resultSet.getLong("id_user2");
            Timestamp localDateTime = resultSet.getTimestamp("date_friendship");
            Friendship f = new Friendship(id_user1, id_user2);
            f.setFriendsFrom(localDateTime.toLocalDateTime());
            friendships.add(f);
        }
        return friendships;
    }

    @Override
    public Optional<Friendship> save(Friendship entity) throws SQLException {
        if (entity == null) {
            throw new RepositoryException("Entity cannot be null!");
        }
        validator.validate(entity);
        String insertSQL = "insert into friendships(id_user1,id_user2) values (?,?) ON CONFLICT (id_user1, id_user2) DO NOTHING";

        PreparedStatement statement = connection.prepareStatement(insertSQL);
        statement.setLong(1, entity.getId().getLeft());
        statement.setLong(2, entity.getId().getRight());
        int answer = statement.executeUpdate();
        return answer == 0 ? Optional.of(entity) : Optional.empty();
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) throws SQLException {
        if (id == null) {
            throw new RepositoryException("id must not be null");
        }
        String deleteSQL = "delete from friendships where (id_user1,id_user2) = (?,?)";

        PreparedStatement statement = connection.prepareStatement(deleteSQL);
        statement.setLong(1, id.getLeft());
        statement.setLong(2, id.getRight());
        Optional<Friendship> foundUser = findOne(id);
        int answer = 0;
        answer = statement.executeUpdate();
        return answer == 0 ? Optional.empty() : foundUser;

    }

    @Override
    public Optional<Friendship> update(Friendship entity) throws SQLException {
        if (entity == null) {
            throw new RepositoryException("Entity cannot be null!");
        }
        validator.validate(entity);
        Optional<Friendship> foundFriendship = findOne(entity.getId());
        if (foundFriendship.isEmpty())
            throw new RepositoryException("The friendship you're trying to update does not exist!");
        String updateSQL = "update friendships set date_friendship=? where (id_user1,id_user2)=(?,?)?";
        PreparedStatement statement = connection.prepareStatement(updateSQL);
        statement.setLong(2, entity.getId().getLeft());
        statement.setLong(3, entity.getId().getRight());
        statement.setTimestamp(1, Timestamp.valueOf(entity.getFriendsFrom()));
        int answer = statement.executeUpdate();
        return answer == 0 ? Optional.of(entity) : Optional.empty();
    }
}
