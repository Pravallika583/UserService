package com.example.user_service.users;


import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserServiceImpl implements UserService {

  // In-memory data store: id -> User
  private final ConcurrentHashMap<Long, User> users;

  // Generates unique ids in a thread-safe way
  private final AtomicLong idGenerator;

  // Default constructor
  public UserServiceImpl() {
    this(new ConcurrentHashMap<>(), new AtomicLong(0L));
  }

  // Constructor with injected store/idGenerator
  public UserServiceImpl(ConcurrentHashMap<Long, User> users,
                         AtomicLong idGenerator) {
    this.users = Objects.requireNonNull(users, "users map must not be null");
    this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
  }

  @Override
  public User createUser(String name, String email) {
    Objects.requireNonNull(name, "name must not be null");
    Objects.requireNonNull(email, "email must not be null");

    long id;
    User user;

    // synchronize writes to keep this whole operation atomic
    synchronized (this) {
      id = idGenerator.incrementAndGet();
      user = new User(id, name, email);
      users.put(id, user);
    }

    return user;
  }

  @Override
  public User getUser(long id) throws UserNotFoundException {
    User user = users.get(id);
    if (user == null) {
      throw new UserNotFoundException(id);
    }
    return user;
  }

  @Override
  public User updateUserEmail(long id, String newEmail) throws UserNotFoundException {
    Objects.requireNonNull(newEmail, "newEmail must not be null");

    synchronized (this) {
      User existing = users.get(id);        // read inside synchronized block
      if (existing == null) {
        throw new UserNotFoundException(id);
      }

      // User is immutable, so we create a new instance
      User updated = existing.withEmail(newEmail);
      users.put(id, updated);
      return updated;
    }
  }

  @Override
  public void deleteUser(long id) throws UserNotFoundException {
    synchronized (this) {
      User removed = users.remove(id);
      if (removed == null) {
        throw new UserNotFoundException(id);
      }
    }
  }

}
