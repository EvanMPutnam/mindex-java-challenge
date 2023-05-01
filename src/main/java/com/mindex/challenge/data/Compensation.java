package com.mindex.challenge.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "compensation")
public class Compensation {
    @Id
    private Employee employee;
    private int salary; // Assumption that salary, like most places, is a whole number and does not include cents.
    private Date effectiveDate;

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
