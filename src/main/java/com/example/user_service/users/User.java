package com.example.user_service.users;
import java.util.Objects;

public final class User {

  private final long id;
  private final String name;
  private final String email;

  public User(long id, String name, String email) {
    this.id = id;
    this.name = Objects.requireNonNull(name, "name must not be null");
    this.email = Objects.requireNonNull(email, "email must not be null");
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

 // Returns as new user with different email id
  public User withEmail(String newEmail) {
    return new User(this.id, this.name, newEmail);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;
    User user = (User) o;
    return id == user.id &&
      name.equals(user.name) &&
      email.equals(user.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, email);
  }

  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", email='" + email + '\'' +
      '}';
  }
}
