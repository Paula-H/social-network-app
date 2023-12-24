package com.example.socialnetworkapp.repository;

import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.validators.Strategy;
import com.example.socialnetworkapp.validators.ValidatorFactory;
import com.example.socialnetworkapp.validators.ValidatorInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserRepository extends AbstractRepository<User> implements Repository<Long,User>{

    public UserRepository( String url, String username, String password) throws SQLException, ClassNotFoundException {
        super(Strategy.user,url,username,password);
    }


    @Override
    public Optional<User> findOne(Long id) throws SQLException {
            PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");

            statement.setInt(1,Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username_user = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User u = new User(firstName,lastName,username_user,email,password);
                u.setId(id);
                return Optional.of(u);
            }
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() throws SQLException {
        Set<User> users = new HashSet<>();
            PreparedStatement statement = connection.prepareStatement("select * from users");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username_user = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User u = new User(firstName,lastName,username_user,email,password);
                u.setId(id);
                users.add(u);
            }
        return users;
    }

    @Override
    public Optional<User> save(User entity) throws SQLException {
        if (entity == null){
            throw new RepositoryException("Entity cannot be null!");
        }
        validator.validate(entity);
        String insertSQL = "insert into users(first_name,last_name,username,email, password) values (?,?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setString(1,entity.getFirst_name());
            statement.setString(2,entity.getLast_name());
            statement.setString(3,entity.getUsername());
            statement.setString(4,entity.getEmail());
            statement.setString(5,entity.getPassword());
            int answer = statement.executeUpdate();
            return answer==0 ? Optional.empty(): Optional.of(entity);
    }

    @Override
    public Optional<User> delete(Long id) throws SQLException {
        if (id == null) {
            throw new RepositoryException("id must not be null");
        }
        String deleteSQL = "delete from users where id=?";

            PreparedStatement statement = connection.prepareStatement(deleteSQL);
            statement.setLong(1,id);
            Optional<User> foundUser = findOne(id);
            int answer=0;
            if(foundUser.isPresent()){
                answer = statement.executeUpdate();
            }
            return answer == 0 ? foundUser : Optional.empty();

    }

    @Override
    public Optional<User> update(User entity) throws SQLException {
        if(entity == null){
            throw new IllegalArgumentException("Entity cannot be null!");
        }
        validator.validate(entity);
        String updateSQL = "update users set first_name=?,last_name=?,username=?,email=?,password=? where id=?";
            PreparedStatement statement = connection.prepareStatement(updateSQL);
            statement.setString(1,entity.getFirst_name());
            statement.setString(2,entity.getLast_name());
            statement.setString(3,entity.getUsername());
            statement.setString(4,entity.getEmail());
            statement.setString(5,entity.getPassword());
            statement.setLong(6,entity.getId());
            int answer = statement.executeUpdate();
            return answer == 0 ? Optional.empty() : Optional.of(entity);

    }
}
