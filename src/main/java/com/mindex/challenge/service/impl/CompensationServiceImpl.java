package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);


    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation for [{}]", compensation);

        compensation.setEffectiveDate(Date.from(Instant.now()));
        compensationRepository.save(compensation);

        return compensation;
    }

    @Override
    public Compensation read(String employeeId) {
        LOG.debug("Reading compensation for [{}]", employeeId);
        Compensation compensation = compensationRepository.findByEmployee_EmployeeId(employeeId);
        if (compensation == null) {
            throw new RuntimeException("Invalid compensation data for employee: " + employeeId);
        }
        // Allows the full stored employee info to be returned if it wasn't stored in the create query.
        // This code could be removed later if we set up references between the collections but now leaving as is.
        if (compensation.getEmployee().getFirstName() == null) {
            Employee employee = employeeRepository.findByEmployeeId(employeeId);
            compensation.setEmployee(employee);
        }
        return compensation;
    }
}
