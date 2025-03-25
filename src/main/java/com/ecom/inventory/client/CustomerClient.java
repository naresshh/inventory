package com.ecom.inventory.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class CustomerClient {

    private final RestClient restClient;
    private final String customerServiceBaseUrl;

    public CustomerClient(RestClient.Builder builder, @Value("${customer.service.base-url}") String customerServiceBaseUrl) {
        this.restClient = builder.baseUrl(customerServiceBaseUrl).build();
        this.customerServiceBaseUrl = customerServiceBaseUrl;
    }

    // Validate if the user is an admin
    public boolean isAdmin(String jwtToken) {
        return Boolean.TRUE.equals(
                restClient.get()
                        .uri(customerServiceBaseUrl+ "/validate-admin")  // Customer Service endpoint
                        .headers(headers -> headers.setBearerAuth(jwtToken))
                        .retrieve()
                        .body(Boolean.class) // Expecting a boolean response
        );
    }
}