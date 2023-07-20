package com.aimons.jelivebien.model;



import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(nullable = false, unique = true, length = 45)
    private String email;

    @Column(nullable = false, unique = true)
    private int phoneNumber;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;


    @Column(name = "verification_code", length = 64)
    private String verificationCode;


    @Column(nullable = false, unique = false, length = 45, name = "civility")
    private String civility;


    private boolean certified = false;;



    @Column(name = "type_account")
    @Enumerated(EnumType.STRING)
    private TypeAccount typeAccount;

    public enum TypeAccount{
        VIP,
        PREMIUM,
        STANDARD,
        GRATUIT;

    }

    @Column(name="create_user_date")
    private LocalDateTime createUserDate;

    @Column(name="expire_subscribe_date")
    private LocalDateTime expireSubscribeDate;

    @Column(name="expire_certified_date")
    private LocalDateTime expireCertifiedDate;



    @Column(name="number_post_permits",nullable = true)
    private int numberPostPermits = 0;

    private  boolean enabled; // Ce ceci permettra de disable un user


    private boolean subscribe = false;  //Account should be true before post anything of ads

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Post> posts = null;

    @OneToMany(mappedBy = "user_paiement",cascade = CascadeType.ALL)
    private List<UserPaiement> userPaiements;




    public User() {
    }


    public User(String firstName, String lastName, String email, int phoneNumber, String password, String civility,
                TypeAccount typeAccount, List<Role> roles, boolean enabled, boolean subscribe,
                LocalDateTime createUserDate, LocalDateTime expireSubscribeDate
                ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.civility = civility;
        this.typeAccount=typeAccount;
        this.roles = roles;
        this.enabled = enabled;
        this.subscribe=subscribe;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verification_code) {
        this.verificationCode = verification_code;
    }

    public String getCivility() {
        return civility;
    }

    public void setCivility(String pseudo) {
        this.civility = pseudo;
    }

    public TypeAccount getTypeAccount() {
        return typeAccount;
    }

    public void setTypeAccount(TypeAccount typeAccount) {
        this.typeAccount = typeAccount;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getNumberPostPermits() {
        return numberPostPermits;
    }

    public void setNumberPostPermits(int numberPostPermits) {
        this.numberPostPermits = numberPostPermits;
    }

    public LocalDateTime getCreateUserDate() {
        return createUserDate;
    }

    public void setCreateUserDate(LocalDateTime createUserDate) {
        this.createUserDate = createUserDate;
    }

    public LocalDateTime getExpireSubscribeDate() {
        return expireSubscribeDate;
    }

    public void setExpireSubscribeDate(LocalDateTime expireSubscribeDate) {
        this.expireSubscribeDate = expireSubscribeDate;
    }

    public LocalDateTime getExpireCertifiedDate() {
        return expireCertifiedDate;
    }

    public void setExpireCertifiedDate(LocalDateTime expireCertifiedDate) {
        this.expireCertifiedDate = expireCertifiedDate;
    }

    public List<UserPaiement> getUserPaiements() {
        return userPaiements;
    }

    public void setUserPaiements(List<UserPaiement> userPaiements) {
        this.userPaiements = userPaiements;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;


    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void addPost(Post post){
        this.posts.add(post);
    }

    public boolean isCertified() {
        return certified;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }
}
