package com.fullstack.CRBINVENTORYMODEL.repository;

import com.fullstack.CRBINVENTORYMODEL.model.CRBInventoryMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CRBInventoryMasterRepository extends JpaRepository<CRBInventoryMaster, Integer> {


}
