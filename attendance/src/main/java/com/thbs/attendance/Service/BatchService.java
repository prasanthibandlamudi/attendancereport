package com.thbs.attendance.Service;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.thbs.attendance.DTO.BatchCourseDTO;
import com.thbs.attendance.DTO.BatchesDTO;
import com.thbs.attendance.DTO.EmployeeDTO;
import com.thbs.attendance.Exception.BatchIdNotFoundException;
import com.thbs.attendance.Exception.UserNotFoundException;

@Service
public class BatchService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${learninPlanService.uri}")
    private String learningPlanServiceUri;

    @Value("${batchService.uri}")
    private String batchServiceUri;

    @Value("${batchEmployees.uri}")
    private String batchEmployeesUri;

    public BatchCourseDTO getCourses(long batchId) {
        String uri = UriComponentsBuilder.fromUriString(learningPlanServiceUri)
                .buildAndExpand(batchId)
                .toUriString();

        try {
            ResponseEntity<BatchCourseDTO> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET, 
                    null,
                    new ParameterizedTypeReference<BatchCourseDTO>() {});
                    if (response == null || response.getBody() == null) {
                        throw new BatchIdNotFoundException("Batch with ID " + batchId + " not found.");
                    }

            return response.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new BatchIdNotFoundException("Batch with ID " + batchId + " not found.");
        }
    }

    public List<BatchesDTO> getBatches() {
        String uri = UriComponentsBuilder.fromUriString(batchServiceUri).toUriString();
        BatchesDTO[] response = restTemplate.getForObject(uri, BatchesDTO[].class);
        return Arrays.asList(response);
    }

    public List<EmployeeDTO> getEmployeesByBatchId(long batchId) {
        String url = UriComponentsBuilder.fromUriString(batchEmployeesUri)
                .buildAndExpand(batchId)
                .toUriString();
        try {
            EmployeeDTO[] employeeArray = restTemplate.getForObject(url, EmployeeDTO[].class);
            if (employeeArray == null || employeeArray.length == 0) {
                throw new UserNotFoundException("No employees found for batch with ID " + batchId);
            }
            return Arrays.asList(employeeArray);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new BatchIdNotFoundException("Batch with ID " + batchId + " not found.");
        }
    }
}