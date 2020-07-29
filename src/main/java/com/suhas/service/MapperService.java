package com.suhas.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suhas.dto.MessageDTO;
import com.suhas.dto.TelephoneDTO;
import com.suhas.mongodb.model.Message;
import com.suhas.mongodb.model.Telephone;

@Service
public class MapperService {

	@Autowired
	private ObjectMapper objectMapper;
	
	public List<TelephoneDTO> convertToRequestDTO(List<Telephone> requests){
		List<TelephoneDTO> result = new ArrayList<TelephoneDTO>();
		if (null!=requests) {
			requests.forEach(request -> {result.add(objectMapper.convertValue(request, TelephoneDTO.class));});
		}
		
		return result;
	}
	
	public List<MessageDTO> convertToMessageDTO(List<Message> messages){
		List<MessageDTO> result = new ArrayList<MessageDTO>();
		if (null!=messages) {
			messages.forEach(request -> {result.add(objectMapper.convertValue(request, MessageDTO.class));});
		}
		return result;
	}
}
