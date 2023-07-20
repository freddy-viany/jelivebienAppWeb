package com.aimons.jelivebien.repository;

import com.aimons.jelivebien.model.FilePost;
import com.aimons.jelivebien.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilePostRepository extends JpaRepository<FilePost,Long> {

    @Query("select i from FilePost i where i.owner=?1")
    List<FilePost> findAllFilleByPostId(Optional<Post> post);
}
