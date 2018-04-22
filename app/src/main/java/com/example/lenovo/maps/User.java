package com.example.lenovo.maps;

import java.util.ArrayList;


public class User extends Person {

    ArrayList<Cylinder> cylinders;
    Provider provider;

    String email;
    String name;
    String phone;
    String password;



    public User(String email, String name, String phone , String password) {
        super(email, name, phone, password);
    }

    public User(String email, String name, String password, String phone, ArrayList <Cylinder> cylinders, Provider provider) {
        super(email, name, password, phone);
        this.cylinders = cylinders;
        this.provider = provider;
    }

    public ArrayList <Cylinder> getCylinders() {
        return cylinders;
    }

    public void setCylinders(ArrayList <Cylinder> cylinders) {
        this.cylinders = cylinders;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    @Override
    public String getEmail() {
        return email;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }
}
