package com.fullstack.CRBINVENTORYMODEL.controller;

import com.fullstack.CRBINVENTORYMODEL.message.ResponseMessage;
import com.fullstack.CRBINVENTORYMODEL.model.CustomerStaging;
import com.fullstack.CRBINVENTORYMODEL.repository.CustomerAuditRepository;
import com.fullstack.CRBINVENTORYMODEL.repository.CustomerMasterRepository;
import com.fullstack.CRBINVENTORYMODEL.repository.CustomerStagingRepository;
import com.fullstack.CRBINVENTORYMODEL.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class CRBInventoryController {

    @Autowired
    private CustomerStagingRepository customerStagingRepository;
    @Autowired
    private CustomerAuditRepository customerAuditRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerMasterRepository customerMasterRepository;

    //entering a single record of the customer(REST)
    @PostMapping("/add")
    public ResponseEntity addCustomers(@RequestBody(required = true) CustomerStaging customerStaging){
        customerService.add(customerStaging);
        return new ResponseEntity<CustomerStaging>(HttpStatus.CREATED);
    }

    //displaying all the data of the customer_staging(REST)
    @GetMapping("/getAll")
    public ResponseEntity<List<CustomerStaging>> getAllCustomersStaging(){
       List<CustomerStaging> customerList =customerService.getAllCustomers();
        return new ResponseEntity<>(customerList, HttpStatus.OK);
    }

    //get customer by ID REST
    @GetMapping("{stagingId}")
    public ResponseEntity<CustomerStaging> getCustomerById(@PathVariable(value = "stagingId") Integer stagingId){
        System.out.println("customerId........"+ stagingId);
        CustomerStaging customer= customerService.getCustomerById(stagingId);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    //update by id REST
    @PutMapping("{stagingId}")
    public ResponseEntity<CustomerStaging> updateCustomer(@PathVariable(value = "stagingId") Integer stagingId, @RequestBody(required = true) CustomerStaging customer){
        customer.setStagingId(stagingId);
        customerService.updateCustomerDetails(customer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //uploading Excel sheet data into the database(REST)
    @PostMapping("uploadFile")
    public ResponseEntity uploadExcelFile(@RequestParam("file") MultipartFile file) throws IOException {
        String message = "";
        boolean isFlag = customerService.saveDataFromUploadFile(file);
        if(isFlag){
            try{
                    message = "Uploaded the file successfully: " + file.getOriginalFilename();
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            }catch(Exception e){
                message = "Could not Upload the file: " + file.getOriginalFilename() + "!";
                return  ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));

            }
        }
        message = "Please check file format or excel file Headers!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }

    //to get/display the "In-draft" data(REST)
    @GetMapping("/getDraft")
    public List<CustomerStaging> getDraft(@RequestParam(value = "status" , required = false) String status){
        List<CustomerStaging> customerList = customerService.getInDraft(status);
        return customerList;
    }

    @GetMapping("/{stagingId}/{status}")
    public CustomerStaging updateCustomerStatus(@PathVariable int stagingId, @PathVariable String status){
        CustomerStaging cust = customerService.getCustomerById(stagingId);
        cust.setStatus(status);
        CustomerStaging customer = customerService.updateStatus(cust);
         return customer;
    }

    // 1. inaproval
    // 2. getAllInaproval

//    @GetMapping("{status}")
//    public CustomerStaging updateCustomerStatusToInapproval(@PathVariable int stagingId, @PathVariable String status){
//        CustomerStaging cust = customerService.getCustomerById(stagingId);
//        cust.setStatus(status);
//        CustomerStaging customer = customerService.updateStatusToInapproval(cust);
//        return customer;
//    }

































//    @GetMapping("/")
//    public void updateStatus(){
//        Customer_audit customerAudit = new Customer_audit();
//        Customer customer = new Customer();
//        customerAudit = new Customer_audit(customer.getName(), customer.getLocation(),customer.getEmail(),
//                            customer.getStatus(), customer.getDate());
//        customerAuditRepository.saveAndFlush(customerAudit);
//
//     System.out.println("success");
//    }















    //RESTAPI
//    @PostMapping("uploadfile")
//    public ResponseEntity uploadFile(@RequestParam("file")MultipartFile file) throws IOException{
//        customerService.readFile(file);
//        return new ResponseEntity(HttpStatus.OK);
//    }
}
