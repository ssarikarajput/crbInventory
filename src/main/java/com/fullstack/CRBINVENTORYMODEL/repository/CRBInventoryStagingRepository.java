package com.fullstack.CRBINVENTORYMODEL.repository;

import com.fullstack.CRBINVENTORYMODEL.model.CRBInventoryStaging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CRBInventoryStagingRepository extends JpaRepository<CRBInventoryStaging, Integer> {

   //findByStatus method
    List<CRBInventoryStaging> findByStatus(@Param("status") String status);
}
