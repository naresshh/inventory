package com.ecom.inventory.client;

import com.ecom.inventory.exception.AdminAccessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;


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
        try {
            return Boolean.TRUE.equals(
                    restClient.get()
                            .uri(customerServiceBaseUrl + "/validate-admin")  // Customer Service endpoint
                            .headers(headers -> headers.setBearerAuth(jwtToken))
                            .retrieve()
                            .body(Boolean.class) // Expecting a boolean response
            );
        } catch (RestClientResponseException e) {
            // Handles cases like 403 Forbidden (non-admin user) or 401 Unauthorized
            if (e.getRawStatusCode() == 403) {
                return false;  // Non-admin user, not an error
            }
            throw new AdminAccessException("Admin validation failed: " + e.getMessage(), e);
        } catch (RestClientException e) {
            // Handles service-down scenarios (connection refused, timeout, etc.)
            throw new AdminAccessException("Error contacting Customer Service: " + e.getMessage(), e);
        }
    }
}