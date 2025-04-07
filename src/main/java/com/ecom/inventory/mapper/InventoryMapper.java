    package com.ecom.inventory.mapper;

    import com.ecom.inventory.dto.InventoryRequestDTO;
    import com.ecom.inventory.dto.InventoryResponseDTO;
    import com.ecom.inventory.entity.Inventory;
    import org.springframework.stereotype.Component;

    @Component
    public class InventoryMapper {

        // Manual Entity -> DTO conversion
        public InventoryResponseDTO toDTO(Inventory inventory) {
            InventoryResponseDTO dto = new InventoryResponseDTO();
            dto.setProductId(inventory.getProductId());
            dto.setProductTitle(inventory.getProductName());
            dto.setQuantity(inventory.getQuantity());
            return dto;
        }

        // Manual DTO -> Entity conversion
        public Inventory toEntity(InventoryRequestDTO requestDTO) {
            Inventory inventory = new Inventory();
            inventory.setProductId(requestDTO.getProductId());
            inventory.setQuantity(requestDTO.getQuantity());
            inventory.setProductName(requestDTO.getProductTitle());
            return inventory;
        }
    }