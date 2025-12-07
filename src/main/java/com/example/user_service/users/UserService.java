package com.example.user_service.users;


public interface UserService {

  User createUser(String name, String email);

  User getUser(long id) throws UserNotFoundException;

  User updateUserEmail(long id, String newEmail) throws UserNotFoundException;

  void deleteUser(long id) throws UserNotFoundException;

}
