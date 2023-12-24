package com.example.socialnetworkapp.service;

import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.repository.paging.Page;
import com.example.socialnetworkapp.repository.paging.Pageable;

import java.sql.SQLException;
/**
 * PageableUtils : interface
 *      - contains all the methods necessary for the logical aspect of the program regarding paginated
 *      functionalities
 *      - it is implemented int the Service class
 */
public interface PageableUtils {
    /**
     * Returns a page with certain friends for a given user.
     * @param pageNumber : int -> page number
     * @param pageSize : int -> page size
     * @param id : Long -> the ID of the user we search the friends for
     * @return Page<User>
     * @throws SQLException
     */
    public Page<User> friendsForUser(int pageNumber, int pageSize, Long id) throws SQLException;

    /**
     * Returns a page with certain users after pagination.
     * @param pageNumber : int -> page number
     * @param pageSize : int -> page size
     * @return Page<User>
     * @throws SQLException
     */
    public Page<User> findAllUsers(int pageNumber, int pageSize) throws SQLException;

    /**
     * Returns a page with all the new possible friends for a user
     * @param pageNumber : int -> page number
     * @param pageSize : int -> page size
     * @param id : Long -> the ID of the user we search the possible friends for
     * @return Page<User>
     * @throws SQLException
     */
    public Page<User> possibleFriendsForUser(int pageNumber,int pageSize,Long id) throws SQLException;
}
