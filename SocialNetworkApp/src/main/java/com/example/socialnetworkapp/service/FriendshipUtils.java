package com.example.socialnetworkapp.service;

import com.example.socialnetworkapp.domain.Friendship;
import com.example.socialnetworkapp.domain.Tuple;

import java.sql.SQLException;
import java.util.Optional;

/**
 * FriendshipUtils : interface
 *      - contains all the methods necessary for the logical aspect of the program regarding friendships
 *      - it is implemented int the Service class
 */
public interface FriendshipUtils {

    /**
     * Deleting an already existing friendship.
     * @param id : Tuple<Long,Long>
     * @return the removed friendship, if it was successfully deleted,
     *         null, otherwise
     * @throws SQLException
     */
    public Optional<Friendship> deleteFriendship(Tuple<Long,Long> id) throws SQLException;

    /**
     * Adding a new friendship to the DB.
     * @param id : Tuple<Long,Long>
     * @return null, if the entity was successfully added,
     *         the friendship created, otherwise
     * @throws SQLException
     */
    public Optional<Friendship> addFriendship(Tuple<Long,Long> id) throws SQLException;
}
