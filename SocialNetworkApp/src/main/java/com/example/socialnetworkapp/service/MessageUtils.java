package com.example.socialnetworkapp.service;

import com.example.socialnetworkapp.domain.Message;
import com.example.socialnetworkapp.domain.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * MessageUtils : interface
 *      - contains all the methods necessary for the logical aspect of the program regarding messages
 *      - it is implemented int the Service class
 */
public interface MessageUtils {

    /**
     * Returns all the messages exchanged between 2 users from the DB.
     * @param user1 : User
     * @param user2 : User
     * @return Iterable<Message> -> all the messages between user1 and user2
     * @throws SQLException
     */
    public Iterable<Message> conversationBetween(User user1, User user2) throws SQLException;

    /**
     * Send a new message to one user.
     * @param from : User -> the user that sends the message
     * @param to : User -> the user that receives the message
     * @param message : String -> the message itself
     * @return the message, if it was successfully sent,
     *         null, otherwise
     * @throws SQLException
     */
    public Optional<Message> sendMessage(User from, User to, String message) throws SQLException;

    /**
     * Send a message to multiple users
     * @param from : User -> the user that sends the message
     * @param to : ArrayList<User> -> the users that receive the message
     * @param message : String -> the message itself
     * @return the message, if it was successfully sent,
     *         null, otherwise
     * @throws SQLException
     */
    public Optional<Message> sendMessageToUsers(User from, ArrayList<User> to, String message) throws SQLException;

}
