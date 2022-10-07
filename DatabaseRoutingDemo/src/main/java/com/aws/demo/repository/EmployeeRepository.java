package com.aws.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aws.demo.dto.EmployeeAddressReponse;
import com.aws.demo.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	public void deleteById(long id);
	
	@Query("SELECT new com.aws.demo.dto.EmployeeAddressReponse(e.name, e.email, a.city, a.country) FROM Employee e JOIN e.address a WHERE e.name = :name")
	public List<EmployeeAddressReponse> joinInformation(String name);

}
