package com.example.user_service.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

  private UserServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new UserServiceImpl();
  }

  // createUser

  @Test
  void createUser_shouldAssignIdAndKeepNameAndEmail() {
    User user = service.createUser("Alice", "alice@test.com");

    assertNotNull(user);
    assertTrue(user.getId() > 0, "id should be > 0");
    assertEquals("Alice", user.getName());
    assertEquals("alice@test.com", user.getEmail());
  }

  @Test
  void createUser_withNullName_shouldThrowNpe() {
    assertThrows(NullPointerException.class,
      () -> service.createUser(null, "alice@test.com"));
  }

  @Test
  void createUser_withNullEmail_shouldThrowNpe() {
    assertThrows(NullPointerException.class,
      () -> service.createUser("Alice", null));
  }

  // getUser

  @Test
  void getUser_shouldReturnPreviouslyCreatedUser() throws Exception {
    User created = service.createUser("Bob", "bob@test.com");

    User found = service.getUser(created.getId());

    assertEquals(created.getId(), found.getId());
    assertEquals("Bob", found.getName());
    assertEquals("bob@test.com", found.getEmail());
  }

  @Test
  void getUser_nonExistingId_shouldThrowUserNotFound() {
    assertThrows(UserNotFoundException.class,
      () -> service.getUser(999L));
  }

  // updateUserEmail

  @Test
  void updateUserEmail_shouldChangeEmailOnly() throws Exception {
    User created = service.createUser("Carol", "old@test.com");

    User updated = service.updateUserEmail(created.getId(), "new@test.com");

    assertEquals(created.getId(), updated.getId());
    assertEquals("Carol", updated.getName());
    assertEquals("new@test.com", updated.getEmail());

    User loadedAgain = service.getUser(created.getId());
    assertEquals("new@test.com", loadedAgain.getEmail());
  }

  @Test
  void updateUserEmail_nonExistingId_shouldThrowUserNotFound() {
    assertThrows(UserNotFoundException.class,
      () -> service.updateUserEmail(1234L, "x@test.com"));
  }

  @Test
  void updateUserEmail_nullEmail_shouldThrowNpe() throws Exception {
    User created = service.createUser("Dave", "dave@test.com");

    assertThrows(NullPointerException.class,
      () -> service.updateUserEmail(created.getId(), null));
  }

  // deleteUser

  @Test
  void deleteUser_shouldRemoveUser() throws Exception {
    User created = service.createUser("Eve", "eve@test.com");
    long id = created.getId();

    service.deleteUser(id);

    assertThrows(UserNotFoundException.class,
      () -> service.getUser(id));
  }

  @Test
  void deleteUser_nonExistingId_shouldThrowUserNotFound() {
    assertThrows(UserNotFoundException.class,
      () -> service.deleteUser(555L));
  }

  // concurrency check - testing

  @Test
  void createUser_fromMultipleThreads_shouldUseUniqueIds() throws InterruptedException {
    int count = 50;
    Thread[] threads = new Thread[count];
    long[] ids = new long[count];

    for (int i = 0; i < count; i++) {
      final int index = i;
      threads[i] = new Thread(() -> {
        User u = service.createUser("User" + index, "u" + index + "@test.com");
        ids[index] = u.getId();
      });
    }

    for (Thread t : threads) {
      t.start();
    }
    for (Thread t : threads) {
      t.join();
    }

    java.util.Set<Long> set = new java.util.HashSet<>();
    for (long id : ids) {
      assertTrue(set.add(id), "duplicate id found: " + id);
    }
  }
}
