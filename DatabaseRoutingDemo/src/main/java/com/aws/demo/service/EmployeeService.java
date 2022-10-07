package com.aws.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aws.demo.dao.EmployeeAddressDao;
import com.aws.demo.dto.EmployeeAddVO;
import com.aws.demo.dto.EmployeeAddressReponse;
import com.aws.demo.dto.EmployeeAddressRequest;
import com.aws.demo.model.Address;
import com.aws.demo.model.Employee;
import com.aws.demo.repository.AddressRepository;
import com.aws.demo.repository.EmployeeRepository;
import com.aws.demo.utils.ValidateDates;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private EmployeeAddressDao employeeAddressDao;
	
	public String saveEmployeeAdd(EmployeeAddressRequest employeeAdd) {
		log.info("saving Employee entity in service method!");
		
		// Validate date Of birth is proper format
		ValidateDates.isDateValid(employeeAdd.getEmployee().getDobDate());
		
		// Format created date.
		LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String formattedDate = myDateObj.format(myFormatObj);
		LocalDateTime parsedDate = LocalDateTime.parse(formattedDate, myFormatObj);
		
		// Set and save Employee object
		employeeAdd.getEmployee().setCreatedDate(parsedDate);
		employeeRepository.save(employeeAdd.getEmployee());
		
		return "Employee and Address saved successfully";
	}
	

	public Iterable<Employee> getEmployee() {
		return employeeRepository.findAll();
	}

	public String deleteEmployee(long id) {
		employeeRepository.deleteById(id);
		return "Employee deleted successfully";

	}

	public List<Employee> getAllEmp() {
		System.out.println("Its comingggggg!!!!");
		return employeeAddressDao.findAll();
	}
	
	public List<EmployeeAddVO> getEmpoyeeAddressDetails(){
		return employeeAddressDao.getEmpoyeeAddressDetails();
	}
	
	public List<EmployeeAddressReponse> getJoinInformation(String name){
		return employeeRepository.joinInformation(name);
	}

	// Get address data
	public Iterable<Address> getAddress() {
		return addressRepository.findAll();
	}

}
