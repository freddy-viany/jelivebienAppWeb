package com.aimons.jelivebien.model;


import jakarta.persistence.*;

@Entity
@Table(name = "filepost")
public class FilePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "name")
    String fileName;

    @Column(name = "filetype")
    private String fileType;

    @Column(name = "filesize")
    private long fileSize;


   @Lob
   private byte [] content;

    //@ManyToOne(cascade = CascadeType.REFRESH)
   // @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Post owner;




    public FilePost() {
    }

    public FilePost(String fileName, String fileType, long fileSize, byte[] content) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.content = content;
    }

    public FilePost(String fileName, String fileType, long fileSize, byte[] content, Post owner) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.content = content;
        this.owner = owner;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Post getOwner() {
        return owner;
    }

    public void setOwner(Post owner) {
        this.owner = owner;
    }

    public void addPost(Post post){
        this.owner= post;
    }


}
