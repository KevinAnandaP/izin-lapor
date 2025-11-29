package com.izinlapor.model;

public class User {
    private int id;
    private String nik;
    private String fullName;
    private String username;
    private String password;
    private String role;
    private String phone;
    private String address;
    private String photoProfile;

    public User() {}

    public User(int id, String nik, String fullName, String username, String password, String role, String phone, String address, String photoProfile) {
        this.id = id;
        this.nik = nik;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.address = address;
        this.photoProfile = photoProfile;
    }
    
    public User(int id, String nik, String fullName, String username, String password, String role, String phone, String address) {
        this(id, nik, fullName, username, password, role, phone, address, null);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhotoProfile() { return photoProfile; }
    public void setPhotoProfile(String photoProfile) { this.photoProfile = photoProfile; }
}
