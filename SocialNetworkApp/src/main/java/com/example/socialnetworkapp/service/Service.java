package com.example.socialnetworkapp.service;

import com.example.socialnetworkapp.domain.*;
import com.example.socialnetworkapp.event.EventImplementation;
import com.example.socialnetworkapp.observer.Observable;
import com.example.socialnetworkapp.observer.Observer;
import com.example.socialnetworkapp.repository.*;
import com.example.socialnetworkapp.repository.paging.Page;
import com.example.socialnetworkapp.repository.paging.PageableImplementation;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Service : class
 *      - contains the implementations of all Utils
 *      - uses Observable Interface
 *      - uses Singleton Design Pattern
 */
public class Service implements Observable<EventImplementation>,UserUtils,FriendshipUtils,FriendshipRequestUtils,PageableUtils,MessageUtils{
    private List<Observer<EventImplementation>> observers=new ArrayList<>();

    private UserPageableRepository userRepo;
    private FriendshipPageableRepository friendshipRepo;
    private FriendshipRequestPageableRepository friendshipRequestRepo;
    private MessageRepository messageRepo;

    private static Service instance = null;
    byte[] key = new byte[16];

    /*
        Singleton Pattern
     */

    private Service() throws SQLException, ClassNotFoundException {
        this.userRepo = (UserPageableRepository) RepositoryFactory.createRepository(RepositoryType.user);
        this.friendshipRepo = (FriendshipPageableRepository) RepositoryFactory.createRepository(RepositoryType.friendship);
        this.friendshipRequestRepo = (FriendshipRequestPageableRepository) RepositoryFactory.createRepository(RepositoryType.friendship_request);
        this.messageRepo = (MessageRepository) RepositoryFactory.createRepository(RepositoryType.message);
        key = "a8v7rvfdVYDg82bf".getBytes();

    }
    public static Service getInstance() throws SQLException, ClassNotFoundException {
        if(instance == null)
            instance = new Service();
        return instance;
    }

    /*
        UserUtils implementation
     */
    private String encript(String strToEncrypt){
        //secureRandom.nextBytes(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        byte[] encryptedBytes = new byte[0];
        try {
            encryptedBytes = cipher.doFinal(strToEncrypt.getBytes());
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private String decript(String strToDecrypt){
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        byte[] decryptedBytes = new byte[0];
        try {
            decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
        return new String(decryptedBytes);
    }

    @Override
    public Optional<User> addUser(User user) throws SQLException {
        user.setPassword(encript(user.getPassword()));
        Optional<User> result = this.userRepo.save(user);
        notifyObservers(new EventImplementation(user));
        return result;

    }

    @Override
    public Optional<User> modifyUser(User user) throws SQLException {
        User oldUser = this.userRepo.findOne(user.getId()).get();
        user.setPassword(encript(user.getPassword()));
        Optional<User> result = this.userRepo.update(user);
        notifyObservers(new EventImplementation(oldUser,user));
        return result;

    }

    @Override
    public Optional<User> deleteUser(User user) throws SQLException {
        Optional<User> result = this.userRepo.delete(user.getId());
        notifyObservers(new EventImplementation(user));
        return result;
    }

    @Override
    public Iterable<User> findAllUsers() throws SQLException {
        return this.userRepo.findAll();
    }

    @Override
    public Optional<User> findOneUser(Long id) throws SQLException {
        return this.userRepo.findOne(id);

    }

    @Override
    public Optional<User> checkCredentials(String email, String password) throws SQLException {
        System.out.println(encript(password));
        for(User u : this.userRepo.findAll()){
            if(u.getEmail().equals(email) && u.getPassword().equals(encript(password)))
                return Optional.of(u);
        }
        return Optional.empty();
    }

    /*
        FriendshipUtils implementation
     */
    @Override
    public Optional<Friendship> deleteFriendship(Tuple<Long, Long> id) throws SQLException {
        Optional<Friendship> result = Optional.empty();
        Tuple<Long,Long> reverse_id = new Tuple<>(id.getRight(), id.getLeft());
       if(this.friendshipRepo.findOne(id).isPresent()){
           result = this.friendshipRepo.findOne(id);
           this.friendshipRepo.delete(id);

       }
       else if(this.friendshipRepo.findOne(reverse_id).isPresent()){
           result = this.friendshipRepo.findOne(reverse_id);
           this.friendshipRepo.delete(reverse_id);
       }
       notifyObservers(new EventImplementation(result.get()));
       return result;
    }

    @Override
    public Optional<Friendship> addFriendship(Tuple<Long, Long> id) throws SQLException {
        Tuple<Long,Long> reverse_id = new Tuple<>(id.getRight(), id.getLeft());
        if(this.friendshipRepo.findOne(id).isPresent() || this.friendshipRepo.findOne(reverse_id).isPresent())
            return Optional.empty();
        this.friendshipRepo.save(new Friendship(id.getLeft(),id.getRight()));
        return this.friendshipRepo.findOne(id);
    }

    /*
        FriendshipRequestUtils implementation
     */
    @Override
    public Iterable<FriendshipRequest> filterFriendshipRequests(Long id, RequestStatus requestStatus) throws SQLException {
        ArrayList<FriendshipRequest> friendshipRequests = new ArrayList<>();
        this.friendshipRequestRepo.findAll().forEach(friendshipRequests::add);
        switch (requestStatus){
            case pending -> { return friendshipRequests.stream().filter(f-> f.getStatus().equals("pending") && f.getTo().getId().equals(id)).toList(); }
            case accepted -> { return friendshipRequests.stream().filter(f-> f.getStatus().equals("accepted")&& f.getTo().getId().equals(id)).toList(); }
            case declined -> { return friendshipRequests.stream().filter(f-> f.getStatus().equals("declined")&& f.getTo().getId().equals(id)).toList(); }
            default -> { return null; }
        }
    }

    @Override
    public Optional<FriendshipRequest> proceedWithFriendRequest(Tuple<Long, Long> id, RequestStatus requestStatus) throws SQLException {
        FriendshipRequest friendshipRequest = this.friendshipRequestRepo.findOne(id).get();
        switch (requestStatus){
            case accepted -> {
                friendshipRequest.setStatus("accepted");
                User from_user = friendshipRequest.getFrom();
                User to_user = friendshipRequest.getTo();
                this.friendshipRepo.save(new Friendship(from_user.getId(),to_user.getId()));

            }
            case declined -> {
                friendshipRequest.setStatus("declined");
            }
            default -> { return Optional.empty();}
        }
        this.friendshipRequestRepo.update(friendshipRequest);
        notifyObservers(new EventImplementation(friendshipRequest));
        return Optional.of(friendshipRequest);
    }

    @Override
    public Optional<FriendshipRequest> sendFriendRequest(Long from, Long to) throws SQLException {
        User fromUser = this.userRepo.findOne(from).get();
        User toUser = this.userRepo.findOne(to).get();
        Optional<FriendshipRequest> optionalFriendshipRequest = this.friendshipRequestRepo.findOne(new Tuple<>(from,to));
        if(optionalFriendshipRequest.isEmpty()){
            FriendshipRequest friendshipRequest = new FriendshipRequest(fromUser,toUser);
            friendshipRequest.setId(new Tuple<>(from,to));
            this.friendshipRequestRepo.save(friendshipRequest);
            notifyObservers(new EventImplementation(friendshipRequest));
            return Optional.of(friendshipRequest);
        }
        else if (optionalFriendshipRequest.get().getStatus().equals("declined")){
            FriendshipRequest friendshipRequest = optionalFriendshipRequest.get();
            friendshipRequest.setStatus("pending");
            this.friendshipRequestRepo.update(friendshipRequest);
            notifyObservers(new EventImplementation(friendshipRequest));
            return Optional.of(friendshipRequest);
        }
        return Optional.empty();
    }

    /*
        PageableUtils implementation
     */
    @Override
    public Page<User> friendsForUser(int pageNumber, int pageSize, Long id) throws SQLException {
        return this.friendshipRepo.friendsForAnUser(new PageableImplementation(pageNumber,pageSize),id);
    }

    @Override
    public Page<User> findAllUsers(int pageNumber, int pageSize) throws SQLException {
        return this.userRepo.findAll(new PageableImplementation(pageNumber,pageSize));
    }

    @Override
    public Page<User> possibleFriendsForUser(int pageNumber, int pageSize, Long id) throws SQLException {
        return this.friendshipRequestRepo.allPossibleFriends(new PageableImplementation(pageNumber,pageSize),id);
    }

    /*
        MessageUtils implementation
     */
    @Override
    public Iterable<Message> conversationBetween(User user1, User user2) throws SQLException {
        ArrayList<Message> messages = new ArrayList<>();
        this.messageRepo.findAll().forEach(messages::add);
        Predicate<Message> predicate = new Predicate<Message>() {
            @Override
            public boolean test(Message message) {
                if(message.getFrom().equals(user1) && message.getTo().contains(user2))
                    return true;
                else if(message.getFrom().equals(user2) && message.getTo().contains(user1))
                    return true;
                return false;

            }
        };
        List<Message> convo = messages.stream().filter(predicate).toList();
        return convo;
    }

    @Override
    public Optional<Message> sendMessage(User from, User to, String message) throws SQLException {
        Message msg = new Message(from,message);
        ArrayList<User> toUser = new ArrayList<>();
        toUser.add(to);
        msg.setTo(toUser);
        this.messageRepo.save(msg);
        notifyObservers(new EventImplementation(msg));
        return Optional.of(msg);
    }

    @Override
    public Optional<Message> sendMessageToUsers(User from, ArrayList<User> to, String message) throws SQLException {
        Message msg = new Message(from,message);
        msg.setTo(to);
        this.messageRepo.save(msg);
        notifyObservers(new EventImplementation(msg));
        return Optional.of(msg);
    }

    /*
        Observer implementation
     */

    @Override
    public void addObserver(Observer<EventImplementation> e) {observers.add(e);}

    @Override
    public void removeObserver(Observer<EventImplementation> e) {observers.remove(e);}

    @Override
    public void notifyObservers(EventImplementation t) {
        observers.stream().forEach(x-> {
            try {
                x.update(t);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }

}
