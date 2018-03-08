package com.talfinder.assessment.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.talfinder.assessment.domain.Product;
import com.talfinder.assessment.service.ProductService;

@RestController
@EnableAutoConfiguration
public class ProductRestController {

  @Autowired
  private ProductService productService;

  @RequestMapping("/products")
  List<Product> listAllProducts() {
    return productService.listAll();
  }

  @RequestMapping("/products/{productId}")
  ResponseEntity<Product> getSingleProduct(@PathVariable Integer productId) {
    Product author = productService.getProduct(productId);
    if(author == null) {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(author, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/products", method = RequestMethod.POST)
  ResponseEntity<Product> save(@RequestBody Product product) {
    product = productService.save(product);
    return ResponseEntity.ok(product);
  }

}
