package com.aws.demo.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.aws.demo.dto.EmployeeAddVO;

public class EmployeeRowMapper implements RowMapper<EmployeeAddVO> {

	@Override
	public EmployeeAddVO mapRow(ResultSet rs, int rowNum) throws SQLException {

		EmployeeAddVO employeeAddVO = new EmployeeAddVO();
		
		System.out.println("in row mapper class");
		
		System.out.println(rs);
		
		employeeAddVO.setId(rs.getLong("id"));
		employeeAddVO.setName(rs.getString("name"));
		employeeAddVO.setEmail(rs.getString("email"));
		employeeAddVO.setAid(rs.getInt("aid"));
		employeeAddVO.setCity(rs.getString("city"));
		employeeAddVO.setZipcode(rs.getInt("zipcode"));

		return employeeAddVO;
	}
}
