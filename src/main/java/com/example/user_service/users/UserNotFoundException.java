package com.example.user_service.users;

// Throws this exception when user is not found

public class UserNotFoundException extends Exception {

  public UserNotFoundException(long userId) {
    super("User not found with id: " + userId);
  }

}
