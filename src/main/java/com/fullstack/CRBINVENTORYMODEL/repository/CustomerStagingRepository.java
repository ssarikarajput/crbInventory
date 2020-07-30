package com.fullstack.CRBINVENTORYMODEL.repository;

import com.fullstack.CRBINVENTORYMODEL.model.CustomerStaging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerStagingRepository extends JpaRepository<CustomerStaging, Integer> {

   //findByStatus method
    List<CustomerStaging> findByStatus(@Param("status") String status);
}
