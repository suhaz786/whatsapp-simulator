package com.suhas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suhas.dto.TelephoneDTO;
import com.suhas.mongodb.model.Telephone;
import com.suhas.mongodb.repository.TelephonesRepository;
import com.suhas.service.MapperService;
import com.suhas.service.TelephonesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api(value = "/telephones", description = "REST Controller for handling all telephone services")
@Controller
@RequestMapping(path = "/telephones")
public class TelephonesController {

	private static final Logger logger = LoggerFactory.getLogger(TelephonesController.class);
	
    @Autowired
    private TelephonesService telephoneService;
	
    @Autowired
    private TelephonesRepository telephonesRepository;
    
    @Autowired
    private MapperService mapperService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Get all telephonesTo for a telphone
     */
	@ApiOperation(value = "Load All telephones", notes = "API to retrieve all telephones", nickname = "loadTelephones")
	@GetMapping(value="/{telephone}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<List<TelephoneDTO>> loadTelephones(@PathVariable String telephone) {
		logger.info("get /request/"+telephone);

		List<Telephone> requests = telephonesRepository.getRequestsByTelephone(telephone);
		List<TelephoneDTO> result = mapperService.convertToRequestDTO(requests);
    	return new ResponseEntity<List<TelephoneDTO>>(result, HttpStatus.OK);
    }
    
    /**
     * Get data associated to a communication to show on the header
     */
	@ApiOperation(value = "Load Request Description", notes = "API to get data associated to a communication to show on the header", nickname = "loadRequestDescription")
	@GetMapping(value="/description/{communicationId}/{telephoneTo}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<TelephoneDTO> loadRequestDescription(@PathVariable String communicationId,
			@PathVariable String telephoneTo){
		logger.info("get /request/description/"+communicationId+"/"+telephoneTo);

        Telephone request = telephonesRepository.getTelephoneByCommunicationIdAndTelephoneTo(communicationId, telephoneTo);
        TelephoneDTO result = objectMapper.convertValue(request, TelephoneDTO.class);
        return new ResponseEntity<TelephoneDTO>(objectMapper.convertValue(result, TelephoneDTO.class), 
        		HttpStatus.OK);
    }
	
    /**
     * Send a notification about a message that has been read time after it was sent
     */
	@ApiOperation(value = "Notify Delayed Message Read Event", notes = "API to send a notification about a message that has been read time after it was sent", nickname = "messageReceptionDelayed")
	@PostMapping("/notification/{communicationId}/{telephone}/{telephoneTo}")
    public @ResponseBody ResponseEntity<?> messageReceptionDelayed(@PathVariable String communicationId, 
    		@PathVariable String telephone, 
    		@PathVariable String telephoneTo) {    	
		logger.info("get /request/notification/"+communicationId+"/"+telephone+"/"+telephoneTo);

		telephoneService.receiveMessageDelayed(communicationId, telephone, telephoneTo);
        return new ResponseEntity<>(HttpStatus.OK);
    }
	
    /**
     * Add a telephone or update description and send a notification to telephoneTo 
     */
	@PostMapping("/telephone")
	@ApiOperation(value = "Notify Add Telephone", notes = "API to add telephone", nickname = "addTelephone")
    public @ResponseBody ResponseEntity<TelephoneDTO> addTelephone(@RequestBody @Validated @NotNull TelephoneDTO telephoneDTO) {    	
		logger.info("get /telephone/"+telephoneDTO);

		if (telephoneDTO.getTelephone().equals(telephoneDTO.getTelephoneTo()))
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		TelephoneDTO result = telephoneService.addModifyTelephone(telephoneDTO);
		return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
