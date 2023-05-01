package com.mindex.challenge.dao;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompensationRepository extends MongoRepository<Compensation, String> {
    Compensation findByEmployee_EmployeeId(String employeeId);
}
