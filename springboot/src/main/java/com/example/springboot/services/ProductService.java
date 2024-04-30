package com.example.springboot.services;

import com.example.springboot.controllers.ProductController;
import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.hibernate.procedure.ProcedureOutputs;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ProductService {

    @Autowired
    ProductRepository repository;

    public ProductModel addProduct(ProductRecordDto dto){
        var productModel = new ProductModel();
        BeanUtils.copyProperties(dto, productModel);
        return repository.save(productModel);
    }

    public List<ProductModel> getAllProducts(){
        List<ProductModel> productList = repository.findAll();
        if(!productList.isEmpty()){
            for (ProductModel p : productList){
                UUID id = p.getIdProduct();
                p.add(linkTo(methodOn(ProductController.class).getProduct(id)).withSelfRel());
            }
        }
        return productList;
    }

    public Optional<ProductModel> getProduct(UUID id) {
        return repository.findById(id);
    }

    public Optional<ProductModel> updateProduct(UUID id, ProductRecordDto dto) {
        Optional<ProductModel> optionalProduct = repository.findById(id);
        if (optionalProduct.isPresent()) {
            ProductModel productModel = optionalProduct.get();
            BeanUtils.copyProperties(dto, productModel);
            repository.save(productModel);
        }
        return optionalProduct;
    }

    public Optional<ProductModel> deleteProduct(UUID id){
        Optional<ProductModel> optionalProduct = repository.findById(id);
        if(optionalProduct.isPresent()){
            repository.delete(optionalProduct.get());
        }
        return optionalProduct;
    }
}
