package com.fullstack.CRBINVENTORYMODEL.repository;

import com.fullstack.CRBINVENTORYMODEL.model.CRBInventoryAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CRBInventoryAuditRepository extends JpaRepository<CRBInventoryAudit, Integer> {

}
