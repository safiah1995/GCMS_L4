package com.example.lenovo.maps;

public class Person {
    String email;
    String Name;
    String Password;
    String phone;


    public Person(String email, String name, String password, String phone) {
        this.email = email;
        Name = name;
        Password = password;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
