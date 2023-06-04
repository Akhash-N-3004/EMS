package com.cognizant.EMS.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="training")
public class TrainingSlot {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long Id;

	
	@Column(name = "courseName")
	private String courseName;
	@Transient
	private boolean hascoursename = false;
	
	@Column(name = "status")
	private boolean status=false;
	@Transient
	private boolean hasStatus = false;

	@OneToOne
	@JoinColumn(name = "empid",nullable = false)
	private Employee empid;
	@Transient
	private boolean hasempid=false;
	
	
	
	@Override
	public String toString() {
		return "TrainingSlot [Id=" + Id + ", courseName=" + courseName + ", Status="+status+", empid=" + empid + "]";
	}
	


	public long getId() {
		return Id;
	}


	public void setId(long id) {
		Id = id;
	}


	public String getcourseName() {
		return courseName;
	}


	public void setcourseName(String courseName) {
		this.courseName = courseName;
	}


	public Employee getEmpid() {
		return empid;
	}


	public void setEmpid(Employee emp) {
		this.empid = emp;
	}


	public TrainingSlot(long id, String courseName, Employee emp) {
		super();
		Id = id;
		this.courseName = courseName;
		this.empid = emp;
	}
	
	public boolean hascoursename() {
		return hascoursename;
	}
	
	public boolean hasempid() {
		return hasempid;
	}



	public boolean getStatus() {
		return this.status;
	}



	public void setStatus(boolean status) {
		this.status = status;
	}
	public boolean hasStatus() {
		return this.hasStatus;
	}

}
