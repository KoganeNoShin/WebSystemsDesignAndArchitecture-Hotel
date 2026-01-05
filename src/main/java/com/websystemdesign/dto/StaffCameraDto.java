package com.websystemdesign.dto;

import com.websystemdesign.model.StatoCamera;
import lombok.Data;
import java.util.List;

@Data
public class StaffCameraDto {
    private Long id;
    private String numero;
    private StatoCamera status; // LIBERA, OCCUPATA, DA_PULIRE
    private String clienteAttuale; // Nome Cognome (utile per lo staff)
    private List<String> note; // Es. "Allergia polvere", "Cane in camera"
}
