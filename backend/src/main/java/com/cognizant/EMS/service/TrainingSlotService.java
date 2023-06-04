package com.cognizant.EMS.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import com.cognizant.EMS.entity.Employee;
import com.cognizant.EMS.entity.TrainingSlot;
import com.cognizant.EMS.repository.TrainingSlotRepository;

@Service
public class TrainingSlotService {
	
	@Autowired
	private TrainingSlotRepository trainingslotRepository;
	
	
	public List<TrainingSlot> getAllTrainingSlot(){
		return trainingslotRepository.findAll();
		}
	
	public Optional<TrainingSlot> getTrainingSlotById(long id){
		return trainingslotRepository.findById(id);
	}
	
	
	public TrainingSlot createTrainingSlot(TrainingSlot trainingslot) {
		
		return trainingslotRepository.save(trainingslot);
	}
	
	public TrainingSlot updateTrainingSlot(Long id,TrainingSlot updatedSlot) {
		TrainingSlot slot = trainingslotRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Slot is not found"));
		if(updatedSlot.hascoursename()) {
			slot.setcourseName(updatedSlot.getcourseName());
		}
		if(updatedSlot.hasStatus()) {
			slot.setStatus(updatedSlot.getStatus());
		}
		if(updatedSlot.hasempid()) {
			slot.setEmpid(updatedSlot.getEmpid());
		}
		return trainingslotRepository.save(slot); 
	}
	
	public boolean deleteTrainingSlot(Long id) {
		trainingslotRepository.deleteById(id);
		
		return false;
	}

}
