package com.cognizant.EMS.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.EMS.Exception.ResourceNotFoundException;
import com.cognizant.EMS.entity.JobDepartment;
import com.cognizant.EMS.service.JobDepartmentService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/department")
@CrossOrigin("*")
@Slf4j
public class JobDepartmentController {

  @Autowired
  private JobDepartmentService jobDepartmentService;

  @GetMapping
  public List<JobDepartment> getAllJobDepartment() {
	  log.info("Successfully fetched all the departments");
    return jobDepartmentService.getAllJobDepartment();
  }

  @GetMapping("/{id}")
  public Optional<JobDepartment> getJobDepartmentById(@PathVariable Long id) {
	  log.info("Successfully fetched the department");
    return jobDepartmentService.getJobDepartmentById(id);
  }

  @PostMapping
  public JobDepartment createJobDepartment(@RequestBody JobDepartment jobDepartment) {
	  log.info("Successfully created new department");
    return jobDepartmentService.createJobDepartment(jobDepartment);
  }
  
  @PutMapping("/{id}")
  public JobDepartment updateJobDepartment(@PathVariable Long id, @RequestBody JobDepartment jobDepartment) throws ResourceNotFoundException{
	  Optional<JobDepartment> obj=jobDepartmentService.getJobDepartmentById(id);
	  	if(obj != null) {
	  		log.info("Successfully updated details of the department");
			return jobDepartmentService.updateJobDepartment(id, jobDepartment);
		}else {
			throw new ResourceNotFoundException("The Department detail is not found ");
		}
	
	}

  @DeleteMapping("/{id}")
  public boolean deleteJobDepartmentById(@PathVariable Long id) throws ResourceNotFoundException {
    Optional<JobDepartment> obj = jobDepartmentService.getJobDepartmentById(id);
    if (obj != null) {
      return jobDepartmentService.deleteJobDepartment(id);
    } else {
      throw new ResourceNotFoundException("The department doesn't exist");
    }
  }

}
