package com.example.socialnetworkapp.repository;

import com.example.socialnetworkapp.domain.FriendshipRequest;
import com.example.socialnetworkapp.domain.Message;
import com.example.socialnetworkapp.domain.ReplyMessage;
import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.validators.Strategy;
import com.example.socialnetworkapp.validators.ValidatorInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class MessageRepository extends AbstractRepository<Message> implements Repository<Long, Message> {


    public MessageRepository(String url, String username, String password) throws SQLException {
        super(Strategy.message, url, username, password);
    }

    private Boolean insertMessageReply(Long msgID, Long from, Long to) throws SQLException {
        String insertSQL = "insert into message_receivers(id,from_id,to_id) values(?,?,?)";
        PreparedStatement statement = connection.prepareStatement(insertSQL);
        statement.setLong(1, msgID);
        statement.setLong(2, from);
        statement.setLong(3, to);
        int answer = statement.executeUpdate();
        if (answer == 0)
            return true;
        return false;
    }

    private ArrayList<User> getUsersSentTo(Long id) throws SQLException {

        ArrayList<User> users_sent_to = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement("select * from message_receivers where id = ?");
        statement.setInt(1, Math.toIntExact(id));
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Long user_id = resultSet.getLong("to_id");
            PreparedStatement searchUser = connection.prepareStatement("select * from users where id = ?");
            searchUser.setLong(1, user_id);
            ResultSet resultedUser = searchUser.executeQuery();
            if (resultedUser.next()) {
                Long id_user = resultedUser.getLong("id");
                String first_name = resultedUser.getString("first_name");
                String last_name = resultedUser.getString("last_name");
                String username_user = resultedUser.getString("username");
                User u = new User(first_name, last_name, username_user, "", "");
                u.setId(id_user);
                users_sent_to.add(u);
            }
        }

        return users_sent_to;
    }

    private User getUserFrom(Long id) throws SQLException {

        PreparedStatement statement = connection.prepareStatement("select * from message_receivers where id = ?");
        statement.setInt(1, Math.toIntExact(id));
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            Long user_id = resultSet.getLong("from_id");
            PreparedStatement searchUser = connection.prepareStatement("select * from users where id = ?");
            searchUser.setLong(1, user_id);
            ResultSet resultedUser = searchUser.executeQuery();
            if (resultedUser.next()) {
                Long id_user = resultedUser.getLong("id");
                String first_name = resultedUser.getString("first_name");
                String last_name = resultedUser.getString("last_name");
                String username_user = resultedUser.getString("username");
                User u = new User(first_name, last_name, username_user, "", "");
                u.setId(id_user);
                return u;
            }
        }
        return null;
    }


    @Override
    public Optional<Message> findOne(Long id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("select * from messages where id = ?");

        statement.setInt(1, Math.toIntExact(id));
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            String message = resultSet.getString("message");
            Timestamp localDateTime = resultSet.getTimestamp("date");
            Long reply_to = resultSet.getLong("reply_to");
            User from = getUserFrom(id);
            ArrayList<User> to = getUsersSentTo(id);
            if (reply_to.equals(0L)) {
                Message msg = new Message(from, message);
                msg.setDate(localDateTime.toLocalDateTime());
                msg.setId(id);
                msg.setTo(to);
                return Optional.of(msg);
            } else {
                ReplyMessage reply = new ReplyMessage(from, message, reply_to);
                reply.setDate(localDateTime.toLocalDateTime());
                reply.setId(id);
                reply.setTo(to);
                return Optional.of(reply);
            }
        }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        return Optional.empty();
    }

    @Override
    public Iterable<Message> findAll() throws SQLException {
        ArrayList<Message> messages = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("select * from messages");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            String message = resultSet.getString("message");
            Timestamp localDateTime = resultSet.getTimestamp("date");
            Long reply_to = resultSet.getLong("reply_to");
            User from = getUserFrom(id);
            ArrayList<User> to = getUsersSentTo(id);
            if (reply_to.equals(0L)) {
                Message msg = new Message(from, message);
                msg.setDate(localDateTime.toLocalDateTime());
                msg.setId(id);
                msg.setTo(to);
                messages.add(msg);
            } else {
                ReplyMessage reply = new ReplyMessage(from, message, reply_to);
                reply.setDate(localDateTime.toLocalDateTime());
                reply.setId(id);
                reply.setTo(to);
                messages.add(reply);
            }
        }
        return messages;
    }


    public Long getMaximumId() throws SQLException {

        PreparedStatement statement = connection.prepareStatement("select max(id) FROM messages");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next())
            return resultSet.getLong("max");
        return null;

    }

    @Override
    public Optional<Message> save(Message entity) throws SQLException {
        if (entity == null) {
            throw new RepositoryException("id must not be null");
        }
        validator.validate(entity);
        String saveSQL = "insert into messages(message) values (?)";
        String saveReplySQL = "insert into messages(message,reply_to) values (?,?)";

        if (entity instanceof ReplyMessage) {
            PreparedStatement statement = connection.prepareStatement(saveReplySQL);
            statement.setString(1, entity.getMessage());
            statement.setLong(2, ((ReplyMessage) entity).getReplyToID());
            int answer = statement.executeUpdate();
            Long newID = getMaximumId();
            entity.getTo().forEach(x ->
                    {
                        try {
                            insertMessageReply(newID, entity.getFrom().getId(), x.getId());
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
            );
            Optional<Message> mess = findOne(newID);

            return answer == 0 ? Optional.empty() : mess;
        } else {
            PreparedStatement statement = connection.prepareStatement(saveSQL);
            statement.setString(1, entity.getMessage());
            int answer = statement.executeUpdate();
            Long newID = getMaximumId();
            entity.getTo().forEach(x ->
                    {
                        try {
                            insertMessageReply(newID, entity.getFrom().getId(), x.getId());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            Optional<Message> mess = findOne(newID);
            return answer == 0 ? Optional.empty() : mess;
        }
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }
}
