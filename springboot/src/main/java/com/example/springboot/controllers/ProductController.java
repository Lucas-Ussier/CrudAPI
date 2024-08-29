package com.example.springboot.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;

import jakarta.validation.Valid;

@RestController
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @PostMapping("/products")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        List<ProductModel> products = productRepository.findAll();
        if(!products.isEmpty()) {
            for(ProductModel product : products) {
                UUID id = product.getIdProduct();
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @GetMapping("/products/{idProduct}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value = "idProduct") UUID idProduct) {
        Optional<ProductModel> product0 = productRepository.findById(idProduct);
        if(product0.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        product0.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Todos os Produtos"));
        return ResponseEntity.status(HttpStatus.OK).body(product0.get());
    }

    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @PutMapping("/products/{idProduct}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "idProduct") UUID idProduct, @RequestBody @Valid ProductRecordDto productRecordDto) {
        Optional<ProductModel> product0 = productRepository.findById(idProduct);
        if(product0.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        var productModel = product0.get();
        BeanUtils.copyProperties(productRecordDto, productModel);

        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }

    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @DeleteMapping("/products/{idProduct}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "idProduct") UUID idProduct) {
        Optional<ProductModel> product0 = productRepository.findById(idProduct);
        if(product0.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        productRepository.delete(product0.get());

        return ResponseEntity.status(HttpStatus.OK).body("Product deleted");
    }
}
