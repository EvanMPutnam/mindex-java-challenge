package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    /**
     * Note that this method assumes that there are not any circular reporting structures as I did not set a depth limit.
     * @param id
     * @return
     */
    @Override
    public ReportingStructure findReportingStructure(String id) {
        LOG.debug("Fetching reporting structure for employee id [{}]", id);

        // The root employee is our very first employee in the tree we are looking for.
        Employee rootEmployee = employeeRepository.findByEmployeeId(id);
        if (rootEmployee == null) {
            return null;
        }

        // Store a set of all reports.
        Set<String> reportIds = new HashSet<>();

        // Use a stack for our descending of the reporting tree.
        Stack<List<Employee>> reportsToCheck = new Stack<>();

        // Push the first set of reports onto the stack.
        reportsToCheck.push(rootEmployee.getDirectReports());

        // Pop the stack until you can't anymore.
        while (reportsToCheck.size() != 0) {
            // Convert employee list into employee id list for ease of use.
            List<Employee> reportsList = reportsToCheck.pop();
            if (reportsList == null || reportsList.size() == 0) {
                continue;
            }
            List<String> employeeReportIds = reportsList.stream().map(Employee::getEmployeeId).collect(Collectors.toList());

            // Query all sub reports at one time.  Allows less individual queries to the database.
            List<Employee> reports = employeeRepository.findByEmployeeIdIn(employeeReportIds);
            reports.forEach(report -> {
                // We cant to skip adding to the stack if the employee has already been processed.
                if (reportIds.contains(report.getEmployeeId())) { return; }

                // Add the id of the report to the final set.
                reportIds.add(report.getEmployeeId());

                // Get the next group of reports to process.
                reportsToCheck.push(report.getDirectReports());
            });
        }

        // Set the reporting structure.
        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setNumberOfReports(reportIds.size());
        reportingStructure.setEmployee(rootEmployee);
        return reportingStructure;
    }
}
