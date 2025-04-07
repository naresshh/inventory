package com.ecom.inventory.controller;


import com.ecom.inventory.client.CustomerClient;
import com.ecom.inventory.dto.InventoryRequestDTO;
import com.ecom.inventory.dto.InventoryResponseDTO;
import com.ecom.inventory.exception.AdminAccessException;
import com.ecom.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:5173")
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
        try {
            boolean isAdmin = customerClient.isAdmin(jwtToken);
            if (!isAdmin) {
                throw new AdminAccessException("User is not an admin.");
            }
        } catch (AdminAccessException e) {
            // If it's already an AdminAccessException, don't wrap it again—just rethrow it
            throw e;
        }catch (Exception e) {
            throw new AdminAccessException("Error checking admin status", e);
        }

        InventoryResponseDTO responseDTO = inventoryService.addInventory(request).getBody();

        // Return the response with a successful message
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/deduct")
    public ResponseEntity<List<InventoryResponseDTO>> deductInventory(@RequestBody List<InventoryRequestDTO> requestList, @RequestParam Long customerId) {
        return ResponseEntity.ok(inventoryService.deductInventory(requestList, customerId));
    }
    @GetMapping("/check/{productId}")
    public ResponseEntity<InventoryResponseDTO> checkInventory(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.checkInventory(productId));
    }

    @GetMapping("/all")
    public List<InventoryResponseDTO> getAllInventory() {
        List<InventoryResponseDTO> inventories = inventoryService.getAllInventory();
        return inventories;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<InventoryResponseDTO> updateInventoryQuantity(
            @PathVariable Long id,
            @RequestBody InventoryRequestDTO request,
            @RequestHeader("Authorization") String token) {

        // Extract JWT token (Remove "Bearer " prefix)
        String jwtToken = token.replace("Bearer ", "");

        // Validate if the user is an admin
        boolean isAdmin = customerClient.isAdmin(jwtToken);
        if (!isAdmin) {
            return ResponseEntity.status(403).build(); // Forbidden if not admin
        }

        // Perform the inventory update and return the updated inventory item
        InventoryResponseDTO updatedInventory = inventoryService.updateInventoryQuantity(id, request);
        return ResponseEntity.ok(updatedInventory);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            inventoryService.deleteByProductId(id);  // Call the service to delete
            return ResponseEntity.noContent().build();  // HTTP 204 No Content (successful delete)
        } catch (Exception e) {
            return ResponseEntity.status(404).build();  // HTTP 404 Not Found (if product ID does not exist)
        }
    }
}