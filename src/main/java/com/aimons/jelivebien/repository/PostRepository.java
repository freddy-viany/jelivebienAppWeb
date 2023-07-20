package com.aimons.jelivebien.repository;

import com.aimons.jelivebien.model.Post;
import com.aimons.jelivebien.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {


    Post findByPostID(Long postID);
   //@Query("SELECT e FROM Post e where e.postPublished =?1 ORDER BY (CASE WHEN e.status='VIP' then 3 when e.status='PREMIUM' then 2 when e.status ='STANDARD' then  1 else 1 end)desc ")
   //Page<Post> findAllByOrderByPostIDDesc(Boolean postPublished, Pageable pageable);


  //Page<Post> findByPostPublishedOrderByStatusAscPostIDDesc(Boolean postPublished, Pageable pageable);



   // List<Post> findByPostPublishedTrue();
   //List<Post> findByPostPublished(Boolean postPublished);


    //@Query("SELECT e FROM Post e ORDER BY (CASE WHEN e.status='VIP' then 3 when e.status='PREMIUM' then 2 when e.status ='STANDARD' then  1 else 1 end)desc ")
    //Page<Post> findByPostDescriptionIsContaining(Pageable pageable,String searchSentence);


    //@Query("select p from Post p where p.postDescription = ?1  ORDER BY (CASE WHEN p.status='VIP' then 3 when p.status='PREMIUM' then 2 when p.status ='STANDARD' then  1 else 1 end)desc")
    //Page<Post> findByPostDescriptionContaining(String searchSentence, Pageable pageable);

   // @Query("select p from Post p where p.postDescription like %?1%  ")
   // Page<Post> findAllByPostDescriptionContaining(String searchSentence, Pageable pageable);

    //@Query("select p from Post p where p.postCity = ?1  ")
    //Page<Post> findByPostCity(String postCity, Pageable pageable);

    @Query("select p from Post p where p.postDescription like %?1%  ")
    Page<Post> findByPostDescriptionContainingAndPostPublishedOrderByStatusAscPostIDDesc(String postDescription,Boolean postPublished,Pageable pageable);
    @Query("select p from Post p where p.postDescription like %?1%  ")
    List<Post> findByPostDescriptionContainingAndPostPublishedOrderByStatusAscPostIDDesc(String postDescription,Boolean postPublished);


    Page<Post> findByPostCityAndPostPublishedOrderByStatusAscPostIDDesc(String postCity, Boolean postPublished, Pageable pageable);
    List<Post> findByPostCityAndPostPublishedOrderByStatusAscPostIDDesc(String postCity, Boolean postPublished);


    Page<Post> findByPostCategoryAndPostPublishedOrderByStatusAscPostIDDesc(String postCategory,Boolean postPublished,Pageable pageable);
    List<Post> findByPostCategoryAndPostPublishedOrderByStatusAscPostIDDesc(String postCategory,Boolean postPublished);


    Page<Post> findByPostCategoryAndPostCityAndPostPublishedOrderByStatusAscPostIDDesc(String postCategory,String postCity,Boolean postPublished,Pageable pageable);
    List<Post> findByPostCategoryAndPostCityAndPostPublishedOrderByStatusAscPostIDDesc(String postCategory,String postCity,Boolean postPublished);


    @Query("select p from Post p where p.postDescription like %?1%  ")
    Page<Post> findByPostDescriptionContainingAndPostCategoryAndPostPublishedOrderByStatusAscPostIDDesc(String postDescription,String postCategory,Boolean postPublished,Pageable pageable);
    @Query("select p from Post p where p.postDescription like %?1%  ")
    List<Post> findByPostDescriptionContainingAndPostCategoryAndPostPublishedOrderByStatusAscPostIDDesc(String postDescription,String postCategory,Boolean postPublished);


    @Query("select p from Post p where p.postDescription like %?1%  ")
    Page<Post> findByPostDescriptionContainingAndPostCityAndPostPublishedOrderByStatusAscPostIDDesc(String postDescription,String postCity,Boolean postPublished, Pageable pageable);
    @Query("select p from Post p where p.postDescription like %?1%  ")
    List<Post> findByPostDescriptionContainingAndPostCityAndPostPublishedOrderByStatusAscPostIDDesc(String postDescription,String postCity,Boolean postPublished);


    Page<Post> findByPostPublishedOrderByStatusAscPostIDDesc(Boolean postPublished,Pageable pageable);
    List<Post> findByPostPublishedOrderByStatusAscPostIDDesc(Boolean postPublished);


    @Query("select p from Post p where p.postDescription like %?1%  ")
    Page<Post> findByPostDescriptionIsContainingAndPostCityAndPostCategoryAndPostPublishedOrderByStatusAscPostIDDesc(String postDescription,String postCity, String postCategory,Boolean postPublished,Pageable pageable);
    @Query("select p from Post p where p.postDescription like %?1%  ")
    List<Post> findByPostDescriptionIsContainingAndPostCityAndPostCategoryAndPostPublishedOrderByStatusAscPostIDDesc(String postDescription,String postCity, String postCategory,Boolean postPublished);


    //*********************************************user section
    List<Post> findByUserOrderByPostIDDesc(Optional<User> user);

    List<Post> findByPostCanBeDeleteTrue();


    //  @Query("select p from Post p where p.postCategory = ?1  ")
   // Page<Post> findAllByPostCategory(String postCategory, Pageable pageable);

    /*
   Page<Post> findAllByOrderByCreationPostDateTimeAsc(Pageable pageable);

   List<Article> findAllByPublicationTimeBetween(
      Date publicationTimeStart,
      Date publicationTimeEnd);

      List<User> findByNameOrBirthDate(String name, ZonedDateTime birthDate);

   @Query("select a from Article a where a.creationDateTime <= :creationDateTime")
    List<Article> findAllWithCreationDateTimeBefore(
      @Param("creationDateTime") Date creationDateTime);

        @Query("""
        select c from Cidade c
        where (c.estado is null or (c.estado = :estado))
        and (c.nome is null or (c.nome like :nome))
     """
    )
    Page<Cidade> listarEntries(
        @Param("estado") estado: String,
        @Param("nome") nome: String,
        pageable: Pageable
    )
    */
}
