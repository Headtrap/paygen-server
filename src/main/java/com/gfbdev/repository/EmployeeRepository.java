package com.gfbdev.repository;

import com.gfbdev.entity.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {

    Employee findByEmail(String email);
}
