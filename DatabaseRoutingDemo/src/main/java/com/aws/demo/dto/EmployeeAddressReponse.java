package com.aws.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmployeeAddressReponse {
	
	private String name;
	private String email;
	private String city;
	private String country;

}
