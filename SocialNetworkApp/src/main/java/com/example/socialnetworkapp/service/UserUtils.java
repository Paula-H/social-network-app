package com.example.socialnetworkapp.service;

import com.example.socialnetworkapp.domain.User;

import java.sql.SQLException;
import java.util.Optional;

/**
 * UserUtils : interface
 *      - contains all the methods necessary for the logical aspect of the program regarding users
 *      - it is implemented int the Service class
 */
public interface UserUtils {

    /**
     * Adding a new user to the DB.
     * @param user : User
     * @return null if entity was successfully added,
     *         user otherwise
     * * @throws SQLException
     */
    public Optional<User> addUser(User user) throws SQLException;

    /**
     * Modifying an already-existing user(the ID of the User shall not be modified in any way)
     * @param user : User
     * @return null if entity was successfully updated,
     *         user otherwise
     * @throws SQLException
     */
    public Optional<User> modifyUser(User user) throws SQLException;

    /**
     * Deleting a user from the DB.
     * @param user : User
     * @return the removed user, if the entity was successfully deleted,
     *         null, otherwise
     * @throws SQLException
     */
    public Optional<User> deleteUser(User user) throws SQLException;

    /**
     * Returning all users present in the DB.
     * @return Iterable<User> -> all the users in the DB
     * @throws SQLException
     */
    public Iterable<User> findAllUsers() throws SQLException;

    /**
     * Find a certain user in the DB.
     * @param id : Long
     * @return the said user, if it was found in the DB
     *         null, otherwise
     * @throws SQLException
     */
    public Optional<User> findOneUser(Long id) throws SQLException;

    /**
     * Checking credentials for log-in by searching a user with the given email and password.
     * @param email : String
     * @param password : String
     * @return the said user, if it was found
     *         null, otherwise
     * @throws SQLException
     */
    public Optional<User> checkCredentials(String email,String password) throws SQLException;

}
