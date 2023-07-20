package com.aimons.jelivebien.repository;

import com.aimons.jelivebien.model.CountPaiementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountPaiementStatusRepository extends JpaRepository<CountPaiementStatus,Long> {
}
