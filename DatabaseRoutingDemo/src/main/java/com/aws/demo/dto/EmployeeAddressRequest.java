package com.aws.demo.dto;

import com.aws.demo.model.Address;
import com.aws.demo.model.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAddressRequest {
	
	private Employee employee;
	private Address address;

}
