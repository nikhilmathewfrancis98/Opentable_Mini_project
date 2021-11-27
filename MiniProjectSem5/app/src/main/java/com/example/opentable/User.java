package com.example.opentable;

public class User {

    public String Username, Name, Password, Bio;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String Username, String Name, String Password, String Bio)
    {
        this.Username = Username;
        this.Name = Name;
        this.Password = Password;
        this.Bio = Bio;
    }
}
