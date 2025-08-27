package com.diamco.v1.entities.dtos;

import lombok.Data;

@Data
public class PasswordUpdateDTO {
    private String ancienMdp;
    private String nouveauMdp;
}
