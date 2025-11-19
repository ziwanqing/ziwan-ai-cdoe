package com.example.ziwanaicodebackend.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DeleteRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 3584070451502785241L;
    /**
     * id
     */
    private Long id;


}
