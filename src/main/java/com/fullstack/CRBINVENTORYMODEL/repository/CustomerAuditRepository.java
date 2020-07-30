package com.fullstack.CRBINVENTORYMODEL.repository;

import com.fullstack.CRBINVENTORYMODEL.model.CustomerAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerAuditRepository extends JpaRepository<CustomerAudit, Integer> {

}
