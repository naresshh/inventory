package com.ecom.inventory.client;

import com.ecom.inventory.dto.ProductResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class ProductClient {

    @Autowired
    private RestClient restClient;

    @Value("${product.service.base-url}")
    private String productServiceBaseUrl;


    public boolean productExists(Long productId) {
        try {
            ProductResponseDTO product = restClient.get()
                    .uri(productServiceBaseUrl+ "/"+ productId)  // Endpoint of ProductService
                    .retrieve()
                    .body(ProductResponseDTO.class);

            return product != null;
        } catch (RestClientException e) {
            // You can log this properly in real projects
            System.out.println("Error contacting Product Service: " + e.getMessage());
            return false;
        }
    }

    public ProductResponseDTO getProductDetails(Long productId) {
        try {
            return restClient.get()
                    .uri(productServiceBaseUrl+ "/"+ productId)
                    .retrieve()
                    .body(ProductResponseDTO.class);

        } catch (RestClientException e) {
            throw new RuntimeException("Unable to fetch product details from Product Service");
        }
    }
}