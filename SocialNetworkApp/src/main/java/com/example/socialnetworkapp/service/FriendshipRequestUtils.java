package com.example.socialnetworkapp.service;

import com.example.socialnetworkapp.domain.FriendshipRequest;
import com.example.socialnetworkapp.domain.Tuple;
import com.example.socialnetworkapp.domain.User;

import java.sql.SQLException;
import java.util.Optional;

/**
 * FriendshipRequestUtils : interface
 *      - contains all the methods necessary for the logical aspect of the program regarding friendship requests
 *      - it is implemented int the Service class
 */
public interface FriendshipRequestUtils {
    /**
     * Filtering friendship requests in the DB by the status field.
     * @param id : Long -> the ID of the user the friendship requests are sent to
     * @param requestStatus : RequestStatus -> the status wanted for the filtering
     * @return Iterable<FriendshipRequest>
     * @throws SQLException
     */
    public Iterable<FriendshipRequest> filterFriendshipRequests(Long id,RequestStatus requestStatus) throws SQLException;

    /**
     * Choose how to proceed with a pending friend request: accept or decline
     * @param id : Tuple<Long,Long> -> the ID of the friendship request
     * @param requestStatus : RequestStatus -> how to proceed with the friendship request
     * @return the updated friendship request, if the update was successful
     *         null, otherwise
     * @throws SQLException
     */
    public Optional<FriendshipRequest> proceedWithFriendRequest(Tuple<Long,Long> id, RequestStatus requestStatus) throws SQLException;


    /**
     * Send a Friendship Request(adding a new FriendshipRequest to the DB).
     * @param from : Long -> the ID of the User that will send the friendship request
     * @param to : Long -> the ID of the User that will receive the friendship request
     * @return the new friendship request, if the friendship has been added successfully,
     *         null, otherwise
     * @throws SQLException
     */
    public Optional<FriendshipRequest> sendFriendRequest(Long from,Long to) throws SQLException;

}
