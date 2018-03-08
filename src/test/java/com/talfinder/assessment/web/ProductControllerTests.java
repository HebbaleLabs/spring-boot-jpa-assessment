package com.talfinder.assessment.web;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTests {

  @Autowired
  TestRestTemplate restTemplate;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Before
  public void setup() {
    jdbcTemplate.update("DELETE FROM Products");
  }

  @Test
  public void createProduct() throws JSONException {
    String requestJson = "{"
        + "\"name\":\"Toy Bricks\","
        + "\"sku\":\"PRD-10001\","
        + "\"description\":\"Toy bricks for children 6 years+\""
        + "}";
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type","application/json");
    HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
    ResponseEntity<String> responseEntity =
        restTemplate.postForEntity("/products", request, String.class);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    String expected = "{"
        + "\"id\":1,"
        + "\"name\":\"Toy Bricks\","
        + "\"sku\":\"PRD-10001\","
        + "\"description\":\"Toy bricks for children 6 years+\""
        + "}";
    JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
  }

  @Test
  public void listAllProducts() throws JSONException {
    ResponseEntity<String> responseEntity =
        restTemplate.getForEntity("/products", String.class);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    String expected = "[]";
    JSONAssert.assertEquals(expected, responseEntity.getBody(), false);

    String requestJson = "{"
        + "\"name\":\"Toy Bricks\","
        + "\"sku\":\"PRD-10001\","
        + "\"description\":\"Toy bricks for children 6 years+\""
        + "}";
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type","application/json");
    HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
    restTemplate.postForEntity("/products", request, String.class);

    ResponseEntity<String> secondResponseEntity =
        restTemplate.getForEntity("/products", String.class);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    String expectedEntity = "[{"
        + "\"id\":1,"
        + "\"name\":\"Toy Bricks\","
        + "\"sku\":\"PRD-10001\","
        + "\"description\":\"Toy bricks for children 6 years+\""
        + "}]";
    JSONAssert.assertEquals(expectedEntity, secondResponseEntity.getBody(), false);
  }

  @Test
  public void getOneProduct() throws JSONException {
    ResponseEntity<String> responseEntity =
        restTemplate.getForEntity("/products", String.class);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    String expected = "[]";
    JSONAssert.assertEquals(expected, responseEntity.getBody(), false);

    String requestJson = "{"
        + "\"name\":\"Toy Bricks\","
        + "\"sku\":\"PRD-10001\","
        + "\"description\":\"Toy bricks for children 6 years+\""
        + "}";
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type","application/json");
    HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
    restTemplate.postForEntity("/products", request, String.class);

    Map<String, String> urlVariables = new HashMap<>();
    urlVariables.put("id","1");
    ResponseEntity<String> secondResponseEntity =
        restTemplate.getForEntity("/products/{id}", String.class, urlVariables);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    String expectedEntity = "{"
        + "\"id\":1,"
        + "\"name\":\"Toy Bricks\","
        + "\"sku\":\"PRD-10001\","
        + "\"description\":\"Toy bricks for children 6 years+\""
        + "}";
    JSONAssert.assertEquals(expectedEntity, secondResponseEntity.getBody(), false);
  }

}
