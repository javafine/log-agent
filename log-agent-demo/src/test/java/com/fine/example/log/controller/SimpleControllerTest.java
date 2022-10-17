package com.fine.example.log.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @Author: javafine
 * @Description:
 * @Date: Created in 14:57 2022/10/17
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @org.junit.Test
    public void hello() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity("/hello?name=Anna", String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(
                objectMapper.writeValueAsString(response.getBody()),
                objectMapper.writeValueAsString("Hello Anna! I am the guy with a song on my lips and love in my heart")
        );
    }

    @org.junit.Test
    public void hi() throws JsonProcessingException {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "Anna");
        ResponseEntity<String> response = restTemplate.postForEntity("/hi", map, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(
                objectMapper.writeValueAsString(response.getBody()),
                objectMapper.writeValueAsString("Hi Anna! I am the guy with a song on my lips and love in my heart")
        );
    }

    @org.junit.Test
    public void morning() throws JsonProcessingException {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "Anna");
        ResponseEntity<String> response = restTemplate.postForEntity("/morning", map, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(
                objectMapper.writeValueAsString(response.getBody()),
                objectMapper.writeValueAsString("Morning Anna! I am the guy with a song on my lips and love in my heart")
        );
    }

    @org.junit.Test
    public void remote() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity("/remote", String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        System.out.println(objectMapper.writeValueAsString(response.getBody()));
    }
}