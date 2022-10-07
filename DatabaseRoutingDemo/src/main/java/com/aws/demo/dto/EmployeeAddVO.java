package com.aws.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAddVO {
	
	private long id;
	private String name;
	private String email;
	private int aid;
	private String city;
	private int zipcode;

}
