package com.aws.demo.dao;

import java.util.List;

import com.aws.demo.dto.EmployeeAddVO;
import com.aws.demo.model.Employee;

public interface EmployeeAddressDao {
	
	List<Employee> findAll();
	List<EmployeeAddVO> getEmpoyeeAddressDetails();

}
