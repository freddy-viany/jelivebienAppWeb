package com.aimons.jelivebien.repository;

import com.aimons.jelivebien.model.UserPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPaiementRepository extends JpaRepository<UserPaiement,Long> {
}
