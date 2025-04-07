package com.ecom.inventory.service;

import com.ecom.inventory.client.ProductClient;
import com.ecom.inventory.dto.InventoryRequestDTO;
import com.ecom.inventory.dto.InventoryResponseDTO;
import com.ecom.inventory.dto.ProductResponseDTO;
import com.ecom.inventory.entity.Inventory;
import com.ecom.inventory.entity.InventoryTransaction;
import com.ecom.inventory.exception.InventoryNotFoundException;
import com.ecom.inventory.exception.ProductValidationException;
import com.ecom.inventory.mapper.InventoryMapper;
import com.ecom.inventory.repository.InventoryRepository;
import com.ecom.inventory.repository.InventoryTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    public Object deleteByProductId;
    @Autowired
    private InventoryRepository repository;

    @Autowired
    private InventoryTransactionRepository transactionRepository;
    @Autowired
    private ProductClient productClient;
    @Autowired
    private InventoryMapper mapper;

    /**
     * Add product to inventory (stocking)
     */
    @Transactional
    public ResponseEntity<InventoryResponseDTO> addInventory(InventoryRequestDTO requestDTO) {
        ProductResponseDTO product = productClient.getProductDetails(requestDTO.getProductId());

        if (product == null) {
            throw new ProductValidationException("Product not found in Product Service");
        }

        Inventory inventory = repository.findByProductId(requestDTO.getProductId())
                .orElse(null);

        if (inventory == null) {
            // New inventory - just set the quantity
            inventory = mapper.toEntity(requestDTO);
            inventory.setProductName(product.getProductTitle());
            inventory.setCategoryId(product.getCategoryId());
            inventory.setQuantity(requestDTO.getQuantity());
            if(product.getCategoryId()!=null){
                inventory.setCategoryId(product.getCategoryId());
            }
        } else {
            // Existing inventory - increment the quantity
            inventory.setQuantity(inventory.getQuantity() + requestDTO.getQuantity());
        }
        repository.save(inventory);

        InventoryResponseDTO response = mapper.toDTO(inventory);
        response.setMessage("Item added successfully!");

        return ResponseEntity.ok(response);

    }
    // Deduct inventory for a list of products
    public List<InventoryResponseDTO> deductInventory(List<InventoryRequestDTO> requestDTOList,Long customerId) {
        // Create a list to hold the response DTOs for each inventory update
        return requestDTOList.stream()
                .map(requestDTO -> {
                    // Fetch inventory for each productId in the list
                    Inventory inventory = repository.findByProductId(requestDTO.getProductId())
                            .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for productId: " + requestDTO.getProductId()));

                    // Check if there's sufficient stock
                    if (inventory.getQuantity() < requestDTO.getQuantity()) {
                        throw new ProductValidationException("Insufficient inventory for productId: " + requestDTO.getProductId());
                    }

                    // Deduct the inventory
                    inventory.setQuantity(inventory.getQuantity() - requestDTO.getQuantity());
                    repository.save(inventory);

                    InventoryTransaction transaction = new InventoryTransaction();
                    transaction.setInventory(inventory);  // Link to the inventory
                    transaction.setQuantity(requestDTO.getQuantity());  // Quantity deducted
                    transaction.setCustomerId(customerId);  // The customer who made the deduction
                    transaction.setOrderId(requestDTO.getOrderId());  // Optionally, if order ID is passed
                    transaction.setTransactionType("Order Deduction");  // Deduction type
// transaction.setReason(reason);  // Reason for the deduction (e.g., "Order #123")
                    transaction.setTransactionDate(LocalDateTime.now());  // Current timestamp

                    transactionRepository.save(transaction);
                    // Return the response DTO for the updated inventory
                    return mapper.toDTO(inventory);
                })
                .collect(Collectors.toList());


    }
    /**
     * Check product inventory
     */
    public InventoryResponseDTO checkInventory(Long productId) {
        Inventory inventory = repository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for productId: " + productId));

        return mapper.toDTO(inventory);
    }

    public List<InventoryResponseDTO> getAllInventory() {
        List<Inventory> inventoryList = repository.findAll();

        return inventoryList.stream()
                .map(mapper::toDTO)  // Calls `toDTO(Inventory inventory)`
                .collect(Collectors.toList());
    }

    public InventoryResponseDTO updateInventoryQuantity(Long id, InventoryRequestDTO request) {
        // Find the inventory by ID
        Inventory inventory = repository.findByProductId(id).orElseThrow(() -> new RuntimeException("Inventory not found"));

        // Update the quantity
        inventory.setQuantity(request.getQuantity());

        // Save the updated inventory and return the response DTO
        repository.save(inventory);

        // Map and return updated inventory as DTO
        return mapper.toDTO(inventory);
    }

    @Transactional
    public void deleteByProductId(Long productId) {
        // Check if inventory exists
        Inventory inventory = repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        transactionRepository.deleteByInventoryId(productId);
        // If exists, delete it
        repository.delete(inventory);  // This will delete the inventory entry
    }
}