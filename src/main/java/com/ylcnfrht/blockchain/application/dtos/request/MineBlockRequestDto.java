package com.ylcnfrht.blockchain.application.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MineBlockRequestDto {
    
    @NotBlank(message = "Miner address cannot be blank")
    private String minerAddress;
}
