package com.aws.demo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.aws.demo.dto.EmployeeAddVO;
import com.aws.demo.model.Employee;
import com.aws.demo.model.EmployeeRowMapper;
import com.zaxxer.hikari.HikariDataSource;

@Repository
public class EmployeeAddressDaoImpl implements EmployeeAddressDao{

	//private HikariDataSource dataSource = null;
	@Autowired
	HikariDataSource dataSource;
	JdbcTemplate jdbcTemplate = null;
	
	@Override
	public List<Employee> findAll() {
		jdbcTemplate = getJdbcTemplate(dataSource);
		String sql = "SELECT * FROM EMPLYEE";
		List<Employee> employees = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Employee>(Employee.class));

		return employees;
	}

	@Override
	public List<EmployeeAddVO> getEmpoyeeAddressDetails() {		
		jdbcTemplate = getJdbcTemplate(dataSource);
		
		String sql = "SELECT e.id, e.name, e.email, a.aid, a.city, a.zipcode FROM emplyee e INNER JOIN address a ON e.id = a.aid WHERE a.city=\"tumkur\"";
		List<EmployeeAddVO> employeeAddVO = null;
		
		try{
			employeeAddVO = jdbcTemplate.query(sql, new EmployeeRowMapper());

	    }catch(Exception e){
	        e.printStackTrace();
	    }
		
		return employeeAddVO;
	}
	
	private JdbcTemplate getJdbcTemplate(HikariDataSource dataSource) {
		 return new JdbcTemplate(dataSource);
	}

}
