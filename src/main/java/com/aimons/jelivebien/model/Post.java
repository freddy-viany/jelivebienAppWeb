package com.aimons.jelivebien.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postID;

    @Column(name = "post_title", nullable = false)
    private  String postTitle;

    @Column(name = "post_description", nullable = false,length = 1000)
    private  String postDescription;

    @Column(name = "post_phone_number")
    private int postPhoneNumber;

    @Column(name="post_view_count")
    private int postViewCount=7;


    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationPostDateTime;

    private LocalDateTime expirePostDateTime;

    private LocalDateTime expireSponsoringPostDateTime;

    private Boolean postCanBeDelete = false;

    @Column(name = "post_published")
    private boolean postPublished;

    @Column(name = "post_city")
    private String postCity;

    @Column(name = "post_category")
    private String postCategory;

    @Column(name = "post_certifie")
    private boolean postCertifie;

    @Column(name = "post_move_situation")
    private String postMoveSituation;  //the user can receive, can move or the both for service

    //here is to know if the post is sponsoring
    @Enumerated(EnumType.ORDINAL)
    private PostStatus status;
    public enum PostStatus{
        VIP,
        PREMIUM,
        STANDARD,
        GRATUIT;
    }

    //image that will be use when all posts are request

    @Column(name = "image_post_front",length = 1000)
    private String imageType;


    @Lob
    @Column(name = "image_content_post_front")
    private byte[] imageContent;



    @Lob
    @Column(name = "image_base64")
    private String imageBase64;
    // make in another class

   @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<FilePost> attachments;

   //@ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE})
   @ManyToOne
   private User user;


    public Post() {
    }


    public Post(String postTitle, String postDescription, int postPhoneNumber,
                LocalDateTime creationPostDateTime, LocalDateTime expirePostDateTime,
                LocalDateTime expireSponsoringPostDateTime, boolean postPublished,
                String postCity, String postCategory, PostStatus status, String imageType,
                byte[] imageContent, String imageBase64, List<FilePost> attachments,User user) {
        this.postTitle = postTitle;
        this.postDescription = postDescription;
        this.postPhoneNumber = postPhoneNumber;
        this.creationPostDateTime = creationPostDateTime;
        this.expirePostDateTime = expirePostDateTime;
        this.expireSponsoringPostDateTime = expireSponsoringPostDateTime;
        this.postPublished = postPublished;
        this.postCity = postCity;
        this.postCategory = postCategory;
        this.status = status;
        this.imageType = imageType;
        this.imageContent = imageContent;
        this.imageBase64 = imageBase64;
        this.attachments = attachments;
        this.user=user;
    }

    public long getPostID() {
        return postID;
    }

    public void setPostID(Long postID) {
        this.postID = postID;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public int getPostPhoneNumber() {
        return postPhoneNumber;
    }

    public void setPostPhoneNumber(int postPhoneNumber) {
        this.postPhoneNumber = postPhoneNumber;
    }

    public LocalDateTime getCreationPostDateTime() {
        return creationPostDateTime;
    }

    public void setCreationPostDateTime(LocalDateTime creationPostDateTime) {
        this.creationPostDateTime = creationPostDateTime;
    }



    public LocalDateTime getExpirePostDateTime() {
        return expirePostDateTime;
    }

    public void setExpirePostDateTime(LocalDateTime expiryPostDateTime) {
        this.expirePostDateTime = expiryPostDateTime;
    }

    public LocalDateTime getExpireSponsoringPostDateTime() {
        return expireSponsoringPostDateTime;
    }

    public void setExpireSponsoringPostDateTime(LocalDateTime expirySponsoringPostDateTime) {
        this.expireSponsoringPostDateTime = expirySponsoringPostDateTime;
    }

    public Boolean getPostCanBeDelete() {
        return postCanBeDelete;
    }

    public void setPostCanBeDelete(Boolean postCanBeDelete) {
        this.postCanBeDelete = postCanBeDelete;
    }

    public boolean isPostPublished() {
        return postPublished;
    }

    public void setPostPublished(boolean postPublished) {
        this.postPublished = postPublished;
    }

    public String getPostCity() {
        return postCity;
    }

    public void setPostCity(String postCity) {
        this.postCity = postCity;
    }

    public String getPostCategory() {
        return postCategory;
    }

    public void setPostCategory(String postCategory) {
        this.postCategory = postCategory;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public byte[] getImageContent() {
        return imageContent;
    }

    public void setImageContent(byte[] imageContent) {
        this.imageContent = imageContent;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public List<FilePost> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FilePost> attachments) {
        this.attachments = attachments;
    }



    public boolean isVip() {
        if (getStatus() == PostStatus.VIP) {
            return true;
        }
        return false;
    }

    public boolean isPremium() {
        if (getStatus() == PostStatus.PREMIUM) {
            return true;
        }
        return false;
    }

    public boolean isStandard() {
        if (getStatus() == PostStatus.STANDARD) {
            return true;
        }
        return false;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void addFile(FilePost filePost){
        this.attachments.add(filePost);
    }

    public String getPostMoveSituation() {
        return postMoveSituation;
    }

    public void setPostMoveSituation(String postMoveSituation) {
        this.postMoveSituation = postMoveSituation;
    }

    public boolean isPostCertifie() {
        return postCertifie;
    }

    public void setPostCertifie(boolean postCertifie) {
        this.postCertifie = postCertifie;
    }

    public int getPostViewCount() {
        return postViewCount;
    }

    public void setPostViewCount(int postViewCount) {
        this.postViewCount = postViewCount;
    }
}
