package com.aws.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import com.aws.demo.config.DBContextHolder;
import com.aws.demo.constant.DBTypeEnum;
import com.aws.demo.dto.EmployeeAddVO;
import com.aws.demo.dto.EmployeeAddressReponse;
import com.aws.demo.dto.EmployeeAddressRequest;
import com.aws.demo.model.Address;
import com.aws.demo.model.Employee;
import com.aws.demo.service.EmployeeService;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;

	// Getting all the employees
	@GetMapping("/get")
	@ResponseBody
	public Iterable<Employee> getEmployee(@RequestParam(name = "dbName", defaultValue = "mysql") String dbName) {
		setCurrentDBConnection(dbName);
		return employeeService.getEmployee();
	}

	// Saving Employee and Address
	@PostMapping("/saveEmp")
	public String saveEmployeeAdd(@RequestParam(name = "dbName", defaultValue = "mysql") String dbName,
			@Valid @RequestBody EmployeeAddressRequest employeeAdd) {
		setCurrentDBConnection(dbName);
		return employeeService.saveEmployeeAdd(employeeAdd);
	}

	// Delete Employee
	@DeleteMapping("/delete/{pid}")
	public String deleteEmployee(@RequestParam(name = "dbName", defaultValue = "mysql") String dbName,
			@PathVariable("pid") long id) {
		setCurrentDBConnection(dbName);
		return employeeService.deleteEmployee(id);

	}
	
	
	//Getting employee and address details with join
//	@GetMapping("/getEmpAdd")
//	public List<EmployeeAddVO> getEmpoyeeAddressDetails(@RequestParam(name = "dbName", defaultValue = "mysql") String dbName){
//		setCurrentDBConnection(dbName);
//		
//		System.out.println("Checking db name");
//		System.out.println(dbName);
//		
//		return employeeService.getEmpoyeeAddressDetails();
//	}
	
	
	@GetMapping("/getAddress")
	public Iterable<Address> getAddress(@RequestParam(name = "dbName", defaultValue = "mysql") String dbName) {
		setCurrentDBConnection(dbName);
		return employeeService.getAddress();
	}
	
	@GetMapping("/getJoinInfo/{name}")
	public List<EmployeeAddressReponse> getJoinInformation(@RequestParam(name = "dbName", defaultValue = "mysql") String dbName, 
			@PathVariable("name") String name){
		setCurrentDBConnection(dbName);
		return employeeService.getJoinInformation(name);
	}
	
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
	   Map<String, String> errors = new HashMap<>();
	 
	   ex.getBindingResult().getFieldErrors().forEach(error ->
	           errors.put(error.getField(), error.getDefaultMessage()));
	 
	   return errors;
	}

	// Setting current database based on the queryparam passed through request
	private void setCurrentDBConnection(String dbName) {

		switch (dbName) {
		case "mysql":
			DBContextHolder.setCurrentDb(DBTypeEnum.MYSQLDB);
			break;
		case "postgre":
			DBContextHolder.setCurrentDb(DBTypeEnum.POSTGREDB);
			break;
		case "sqlserver":
			DBContextHolder.setCurrentDb(DBTypeEnum.SQLSERVERDB);
			break;
		}

	}

}
