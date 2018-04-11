package com.talfinder.assessment.web;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.RegularExpressionValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
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
    jdbcTemplate.execute("ALTER SEQUENCE hibernate_sequence RESTART WITH 1");
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

  @Test
  public void getSingleProductIncorrectId() throws JSONException {
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
    urlVariables.put("id","-100");
    ResponseEntity<String> secondResponseEntity =
        restTemplate.getForEntity("/products/{id}", String.class, urlVariables);
    assertEquals(HttpStatus.NOT_FOUND, secondResponseEntity.getStatusCode());
    String expectedMessage = "{"
        + "\"message\":\"Product could not be found\","
        + "\"details\":\"Product with specified product id:[-100] could not be found.\""
        + "}";
    JSONAssert.assertEquals(expectedMessage, secondResponseEntity.getBody(), false);

    urlVariables = new HashMap<>();
    urlVariables.put("id","-1000");
    ResponseEntity<String> thirdResponseEntity =
        restTemplate.getForEntity("/products/{id}", String.class, urlVariables);
    assertEquals(HttpStatus.NOT_FOUND, thirdResponseEntity.getStatusCode());
    expectedMessage = "{"
        + "\"message\":\"Product could not be found\","
        + "\"details\":\"Product with specified product id:[-1000] could not be found.\""
        + "}";
    JSONAssert.assertEquals(expectedMessage, thirdResponseEntity.getBody(), false);
  }

  @Test
  public void createProductNameOverflow() throws JSONException {
    int len = 2000;
    String str = "a";
    String longName = IntStream.range(0, len).mapToObj(i -> str).collect(Collectors.joining(""));

    String requestJson = "{"
        + "\"name\":\"" + longName + "\","
        + "\"sku\":\"PRD-10001\","
        + "\"description\":\"Toy bricks for children 6 years+\""
        + "}";
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type","application/json");
    HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
    ResponseEntity<String> responseEntity =
        restTemplate.postForEntity("/products", request, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    String expected = "{"
        + "\"message\":\"Validation failed\","
        + "\"details\":\"Name should have minimum 2 characters and maximum of 255\""
        + "}";
    JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
  }

  @Test
  public void createProductWithInvalidSKU() throws JSONException {
    String requestJson = "{"
        + "\"name\":\"Toy Bricks\","
        + "\"sku\":\"PRD-XYZABC\","
        + "\"description\":\"Toy bricks for children 6 years+\""
        + "}";
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type","application/json");
    HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
    ResponseEntity<String> responseEntity =
        restTemplate.postForEntity("/products", request, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    String expected = "{"
        + "\"message\":\"Validation failed\","
        + "\"details\":\"SKU should be of format ABC-12345\""
        + "}";
    JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
  }

}
