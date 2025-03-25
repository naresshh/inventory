package com.ecom.inventory.controller;


import com.ecom.inventory.client.CustomerClient;
import com.ecom.inventory.dto.InventoryRequestDTO;
import com.ecom.inventory.dto.InventoryResponseDTO;
import com.ecom.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private CustomerClient customerClient;

    @PostMapping("/add")
    public ResponseEntity<InventoryResponseDTO> addInventory(
            @RequestBody InventoryRequestDTO request,
            @RequestHeader("Authorization") String token) {

        // Extract JWT token (Remove "Bearer " prefix)
        String jwtToken = token.replace("Bearer ", "");

        // Validate if the user is an admin
        boolean isAdmin = customerClient.isAdmin(jwtToken);
        if (!isAdmin) {
            return ResponseEntity.status(403).build(); // Forbidden if not admin
        }

        return ResponseEntity.ok(inventoryService.addInventory(request));
    }

    @PostMapping("/deduct")
    public ResponseEntity<List<InventoryResponseDTO>> deductInventory(@RequestBody List<InventoryRequestDTO> requestList, @RequestParam Long customerId) {
        return ResponseEntity.ok(inventoryService.deductInventory(requestList, customerId));
    }
    @GetMapping("/check/{productId}")
    public ResponseEntity<InventoryResponseDTO> checkInventory(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.checkInventory(productId));
    }
}