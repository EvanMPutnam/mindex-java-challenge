package com.mindex.challenge.dao;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    Employee findByEmployeeId(String employeeId);

    List<Employee> findByEmployeeIdIn(List<String> employeeIds);

}
