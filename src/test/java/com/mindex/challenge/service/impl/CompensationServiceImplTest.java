package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationCreateUrl;
    private String compensationReadUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationCreateUrl = "http://localhost:" + port + "/compensation";
        compensationReadUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    @Test
    public void testCreateRead() {
        Employee employee = new Employee();
        employee.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        Compensation compensation = new Compensation();
        compensation.setEmployee(employee);
        compensation.setSalary(100_000);

        Compensation createdCompensation = restTemplate.postForEntity(
                compensationCreateUrl, compensation, Compensation.class).getBody();
        Compensation readCompensation =  restTemplate.getForEntity(
                compensationReadUrl, Compensation.class, employee.getEmployeeId()).getBody();
        assertEquals(createdCompensation.getSalary(), readCompensation.getSalary());
        assertEquals(employee.getEmployeeId(), readCompensation.getEmployee().getEmployeeId());
    }


}
