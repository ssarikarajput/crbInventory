package com.fullstack.CRBINVENTORYMODEL.services;

import com.fullstack.CRBINVENTORYMODEL.model.CustomerAudit;
import com.fullstack.CRBINVENTORYMODEL.model.CustomerMaster;
import com.fullstack.CRBINVENTORYMODEL.model.CustomerStaging;
import com.fullstack.CRBINVENTORYMODEL.repository.CustomerAuditRepository;
import com.fullstack.CRBINVENTORYMODEL.repository.CustomerMasterRepository;
import com.fullstack.CRBINVENTORYMODEL.repository.CustomerStagingRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class CustomerServiceImple implements CustomerService {

    @Autowired
    private CustomerStagingRepository customerStagingRepository;
    @Autowired
    private CustomerAuditRepository customerAuditRepository;
    @Autowired
    private CustomerMasterRepository customerMasterRepository;


    @Override
    public List<CustomerStaging> getInDraft(String status) {
        return customerStagingRepository.findByStatus("InDraft");
    }

    @Override
    public CustomerStaging updateStatus(CustomerStaging cust) {
            if(cust.getStatus().equalsIgnoreCase("approved")) {
                //customer = customerService.updateStatus(cust.get());

                //Update data in master table
                CustomerMaster customerMaster = new CustomerMaster();
                customerMaster.setMasterId(cust.getStagingId());
                customerMaster.setSource(cust.getSource());
                customerMaster.setDateOfItem(cust.getDateOfItem());
                customerMaster.setECOOCompanyName(cust.getECOOCompanyName());
                customerMaster.setIsin(cust.getIsin());
                customerMaster.setCuspin(cust.getCuspin());
                customerMaster.setSedol(cust.getSedol());
                customerMaster.setPrivateComapanyName(cust.getPrivateComapanyName());
                customerMaster.setNonPermisibleExpectedDate(cust.getNonPermisibleExpectedDate());
                customerMaster.setDate(new Date());
                customerMasterRepository.save(customerMaster);

            } else if(cust.getStatus().equalsIgnoreCase("inaprooval")){
                customerStagingRepository.save(cust);
                //updateCustomerDetails(cust);
            }
            updateCustomerDetails(cust);
            return cust;
    }

//    @Override
//    public CustomerStaging updateStatusToInapproval(CustomerStaging cust) {
//        if(cust.getStatus().equalsIgnoreCase("inaprooval")) {
//            //customer = customerService.updateStatus(cust.get());
//
//            CustomerStaging customer = new CustomerStaging();
//            customer.setStagingId(cust.getStagingId());
//            customer.setSource(cust.getSource());
//            customer.setDateOfItem(cust.getDateOfItem());
//            customer.setECOOCompanyName(cust.getECOOCompanyName());
//            customer.setIsin(cust.getIsin());
//            customer.setCuspin(cust.getCuspin());
//            customer.setSedol(cust.getSedol());
//            customer.setPrivateComapanyName(cust.getPrivateComapanyName());
//            customer.setNonPermisibleExpectedDate(cust.getNonPermisibleExpectedDate());
//            customer.setDate(new Date());
//            customerStagingRepository.save(customer);
//
//        }
//        updateCustomerDetails(cust);
//        return cust;
//    }

    //REST Method
    @Override
    public void add(CustomerStaging customer) {
        //customerInventoryRepository.save(customer);
        updateCustomerDetails(customer);
    }

    //REST METHOD
    @Override
    public List<CustomerStaging> getAllCustomers(){
        return customerStagingRepository.findAll();
    }

    //REST METHOD
    @Override
    public CustomerStaging getCustomerById(Integer stagingId) {
        Optional<CustomerStaging> optionalCustomer= customerStagingRepository.findById(stagingId);
        return optionalCustomer.get();
    }

    //REST METHOD
    @Override
    public void updateCustomerDetails(CustomerStaging customer) {
        //customer.setId(id);
        customer.setDate(new Date());
        customerStagingRepository.save(customer);

        CustomerAudit customerAudit = new CustomerAudit();
        customerAudit.setAuditId(customer.getStagingId());
        customerAudit.setSource(customer.getSource());
        customerAudit.setDateOfItem(customer.getDateOfItem());
        customerAudit.setECOOCompanyName(customer.getECOOCompanyName());
        customerAudit.setIsin(customer.getIsin());
        customerAudit.setCuspin(customer.getCuspin());
        customerAudit.setSedol(customer.getSedol());
        customerAudit.setPrivateComapanyName(customer.getPrivateComapanyName());
        customerAudit.setNonPermisibleExpectedDate(customer.getNonPermisibleExpectedDate());
        customerAudit.setDate(new Date());
        customerAudit.setStatus(customer.getStatus());
        customerAudit.setStagingId(customer.getStagingId());
        customerAuditRepository.save(customerAudit);
    }

    @Override
    public boolean saveDataFromUploadFile(MultipartFile file) {
        boolean isFlag = false;
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extension.equalsIgnoreCase("csv")) {
            isFlag = readDataFromCsv(file);
        }else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")){
            isFlag = readDataFromExcelFile(file);

        }
        return isFlag;
    }

    //reading CSV file
    private boolean readDataFromCsv(MultipartFile file) {
        try {
            InputStreamReader reader = new InputStreamReader(file.getInputStream());
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            List<String[]> rows = csvReader.readAll();
            for (String[] row : rows) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                customerStagingRepository.save(new CustomerStaging(row[0], row[1] , row[2], row[3], row[4], row[5], row[6], row[7], "In-Draft", date));
            }
            return true;
        } catch (Exception e) {

            return false;

        }

    }

    //reading xls or xlsx file
    private boolean readDataFromExcelFile(MultipartFile file) {
        Workbook workbook = getWorkBook(file);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();
        //skipHeader
        rows.next();
        //Row row = rows.next();
        //if(isValidated(row)) {

            List<CustomerStaging> customerList = new ArrayList<>();

            while (rows.hasNext()) {
              Row row = rows.next();
                CustomerStaging customer = new CustomerStaging();
                Iterator<Cell> cellIterator = row.cellIterator();

                //Current Date Formatter
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int cellIndex = cell.getColumnIndex();
                    switch (cellIndex) {
                        case 0:
                            customer.setSource(getCellValue(cell));
                            break;
                        case 1:
                            customer.setDateOfItem(getCellValue(cell));
                            break;
                        case 2:
                            customer.setECOOCompanyName(getCellValue(cell));
                            break;
                        case 3:
                            customer.setIsin(getCellValue(cell));
                            break;
                        case 4:
                            customer.setCuspin(getCellValue(cell));
                            break;
                        case 5:
                            customer.setSedol(getCellValue(cell));
                            break;
                        case 6:
                            customer.setPrivateComapanyName(getCellValue(cell));
                            break;
                        case 7:
                            customer.setNonPermisibleExpectedDate(getCellValue(cell));
                            break;

                    }
                }

                customerList.add(customer);
            }

            if (!CollectionUtils.isEmpty(customerList)) {
                customerStagingRepository.saveAll(customerList);
            }

            return true;
        //}
        //return false;
    }

//    private boolean isValidated(Row row) {
//        CustomerStaging customer = new CustomerStaging();
//        Iterator<Cell> cellIterator = row.cellIterator();
//
//        while(cellIterator.hasNext()) {
//            Cell cell = cellIterator.next();
//            int cellIndex = cell.getColumnIndex();
//            switch (cellIndex) {
//                case 0:
//                    if(cell.getStringCellValue().equals("source")){break;}
//                    else{return false;}
//                case 1:
//                    if(cell.getStringCellValue().equals("Date of Item")){break;}
//                    else{return false;}
//                case 2:
//                    if(cell.getStringCellValue().equals("ECOO Company name")){break;}
//                    else{return false;}
//                case 3:
//                    if(cell.getStringCellValue().equals("ISIN")){break;}
//                    else{return false;}
//                case 4:
//                    if(cell.getStringCellValue().equals("CUSIP")){break;}
//                    else{return false;}
//                case 5:
//                    if(cell.getStringCellValue().equals("SEDOL")){break;}
//                    else{return false;}
//                case 6:
//                    if(cell.getStringCellValue().equals("Private Company Name")){break;}
//                    else{return false;}
//                case 7:
//                    if(cell.getStringCellValue().equals("Non Permissible expected date")){break;}
//                    else{return false;}
//
//            }
//        }
//
//        return true;
//    }


    private static String getCellValue(Cell cell){
        if(cell.getCellType() == CellType.STRING){
            return cell.getStringCellValue();
        }
        else if(cell.getCellType() == CellType.NUMERIC){
            return cell.getNumericCellValue()+"";
        }
        return null;
    }

    //method to check whether it is xls or xlsx
    private Workbook getWorkBook(MultipartFile file) {
        Workbook workbook = null;
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        try {
            if (extension.equalsIgnoreCase("xls")) {
               workbook = new HSSFWorkbook(file.getInputStream());

            } else if (extension.equalsIgnoreCase("xlsx")) {
               workbook = new XSSFWorkbook(file.getInputStream());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return workbook;
    }
}


