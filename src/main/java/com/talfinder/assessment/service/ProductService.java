package com.talfinder.assessment.service;

import java.util.List;

import com.talfinder.assessment.domain.Product;

public interface ProductService {

  Product save(Product product);

  Product getProduct(Integer productId);

  List<Product> listAll();

}
