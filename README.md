# Submission Notes

## Overall Notes
1. I added a bunch of comments that serve as running commentary that we can discuss during the interview.  However, I 
am of the opinion that good code self-documents, in most cases, and in production commenting every line wouldn't make sense.
2. For the first problem I made the assumption, like most org tree structures, there are not circular modeled dependencies.
3. I included a postman collection for convenience that can be used to query.

## Original Approach
The simpler solution I had coded out involved using MongoDB aggregations.  However, it seems this in-memory server
does not support all the standard functionality that mongo allows.  This allowed for everything to be returned in
a single query.  Below is a provided example of an aggregation that could perform all of the logic I wrote out at
the database layer.  The query was tested in a mongo playground and if it were a full mongo db engine it would work.
For example, the version of the in-memory DB that is used does not support graphLookup.
```java
package com.mindex.challenge.dao;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    public static final String AGGREGATION_STEP_MATCH_EMPLOYEE = "  {\n" +
            "    \"$match\": {\n" +
            "      \"employeeId\": ?0\n" +
            "    }\n" +
            "  }";
    public static final String AGGREGATION_STEP_GRAPH_LOOKUP = "  {\n" +
            "    \"$graphLookup\": {\n" +
            "      \"from\": \"employee\",\n" +
            "      \"startWith\": \"$directReports.employeeId\",\n" +
            "      \"connectFromField\": \"directReports.employeeId\",\n" +
            "      \"connectToField\": \"employeeId\",\n" +
            "      \"as\": \"reports\",\n" +
            "      \"maxDepth\": 10,\n" +
            "      \n" +
            "    }\n" +
            "  }";
    public static final String AGGREGATION_STEP_PROJECT_REPORTING_STRUCTURE = "  {\n" +
            "    \"$project\": {\n" +
            "      \"employee\": \"$$ROOT\",\n" +
            "      \"numberOfReports\": {\n" +
            "        \"$size\": \"$reports\"\n" +
            "      }\n" +
            "    }\n" +
            "  }";

    Employee findByEmployeeId(String employeeId);

    /**
     * @param employeeId
     * @return an array of ReportingStructure.  While it should only ever be 1 or 0 results aggregate returns an array.
     */
    @Aggregation(pipeline = {
            AGGREGATION_STEP_MATCH_EMPLOYEE,
            AGGREGATION_STEP_GRAPH_LOOKUP,
            AGGREGATION_STEP_PROJECT_REPORTING_STRUCTURE
    })
    ReportingStructure[] findReportingStructure(String employeeId);
}

```

# Coding Challenge
## What's Provided
A simple [Spring Boot](https://projects.spring.io/spring-boot/) web application has been created and bootstrapped 
with data. The application contains information about all employees at a company. On application start-up, an in-memory 
Mongo database is bootstrapped with a serialized snapshot of the database. While the application runs, the data may be
accessed and mutated in the database without impacting the snapshot.

### How to Run
The application may be executed by running `gradlew bootRun`.

### How to Use
The following endpoints are available to use:
```
* CREATE
    * HTTP Method: POST 
    * URL: localhost:8080/employee
    * PAYLOAD: Employee
    * RESPONSE: Employee
* READ
    * HTTP Method: GET 
    * URL: localhost:8080/employee/{id}
    * RESPONSE: Employee
* UPDATE
    * HTTP Method: PUT 
    * URL: localhost:8080/employee/{id}
    * PAYLOAD: Employee
    * RESPONSE: Employee
```
The Employee has a JSON schema of:
```json
{
  "type":"Employee",
  "properties": {
    "employeeId": {
      "type": "string"
    },
    "firstName": {
      "type": "string"
    },
    "lastName": {
          "type": "string"
    },
    "position": {
          "type": "string"
    },
    "department": {
          "type": "string"
    },
    "directReports": {
      "type": "array",
      "items" : "string"
    }
  }
}
```
For all endpoints that require an "id" in the URL, this is the "employeeId" field.

## What to Implement
Clone or download the repository, do not fork it.

### Task 1
Create a new type, ReportingStructure, that has two properties: employee and numberOfReports.

For the field "numberOfReports", this should equal the total number of reports under a given employee. The number of 
reports is determined to be the number of directReports for an employee and all of their distinct reports. For example, 
given the following employee structure:
```
                    John Lennon
                /               \
         Paul McCartney         Ringo Starr
                               /        \
                          Pete Best     George Harrison
```
The numberOfReports for employee John Lennon (employeeId: 16a596ae-edd3-4847-99fe-c4518e82c86f) would be equal to 4. 

This new type should have a new REST endpoint created for it. This new endpoint should accept an employeeId and return 
the fully filled out ReportingStructure for the specified employeeId. The values should be computed on the fly and will 
not be persisted.

### Task 2
Create a new type, Compensation. A Compensation has the following fields: employee, salary, and effectiveDate. Create 
two new Compensation REST endpoints. One to create and one to read by employeeId. These should persist and query the 
Compensation from the persistence layer.

## Delivery
Please upload your results to a publicly accessible Git repo. Free ones are provided by Github and Bitbucket.
