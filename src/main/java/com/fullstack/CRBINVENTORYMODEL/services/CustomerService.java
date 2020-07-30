package com.fullstack.CRBINVENTORYMODEL.services;

import com.fullstack.CRBINVENTORYMODEL.model.CustomerStaging;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface CustomerService {

    void add(CustomerStaging customer); //REST

    List<CustomerStaging> getAllCustomers(); //REST

    CustomerStaging getCustomerById(Integer id); //REST

    void updateCustomerDetails(CustomerStaging customer); //REST

    boolean saveDataFromUploadFile(MultipartFile file);

    List<CustomerStaging> getInDraft(String status);

    CustomerStaging updateStatus(CustomerStaging cust);

    //CustomerStaging updateStatusToInapproval(CustomerStaging cust);
}
