package com.example.springboot.controllers;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import com.example.springboot.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ProductController {

    @Autowired
    ProductService service;

    @PostMapping("/addProduct")
    public ResponseEntity<ProductModel> addProduct(@RequestBody ProductRecordDto dto) {
        ProductModel productModel = service.addProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productModel);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        List<ProductModel> productList = service.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(productList);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getProduct(@PathVariable(value = "id") UUID id) {
        Optional<ProductModel> optionalProduct = service.getProduct(id);
        if (optionalProduct.isPresent()) {
            ProductModel product = optionalProduct.get();
            product.add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products list: "));
            return ResponseEntity.status(HttpStatus.OK).body(product);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found :(");
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id, @RequestBody ProductRecordDto dto) {
        Optional<ProductModel> optionalProduct = service.updateProduct(id, dto);
        if (optionalProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(optionalProduct.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found :(");
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> optionalProduct = service.deleteProduct(id);
        if(optionalProduct.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found :(");
        }
    }

}
