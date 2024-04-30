package com.example.springboot.controllers;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
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
    ProductRepository repository;

    @PostMapping("/addProduct")
    public ResponseEntity<ProductModel> addProduct(@RequestBody @Valid ProductRecordDto dto) {
        var productModel = new ProductModel();
        BeanUtils.copyProperties(dto, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(productModel));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        List<ProductModel> productList = repository.findAll();
        if(!productList.isEmpty()){
            for (ProductModel p : productList){
                UUID id = p.getIdProduct();
                p.add(linkTo(methodOn(ProductController.class).getProduct(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(productList);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getProduct(@PathVariable(value = "id") UUID id) {
        Optional<ProductModel> optionalProduct = repository.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found :(");
        }
        optionalProduct.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products list: "));
        return ResponseEntity.status(HttpStatus.OK).body(optionalProduct.get());
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductRecordDto dto) {
        Optional<ProductModel> optionalProduct = repository.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found :(");
        }
        ProductModel productModel = optionalProduct.get();
        BeanUtils.copyProperties(dto, productModel);

        try {
            ProductModel updatedProduct = repository.save(productModel);
            return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update product :(");
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> optionalProduct = repository.findById(id);
        if(optionalProduct.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found :(");
        }
        repository.delete(optionalProduct.get());
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted sucessfully");
    }

}
