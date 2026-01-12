package com.websystemdesign.dto;

import com.websystemdesign.model.StatoCamera;
import lombok.Data;
import java.util.List;

@Data
public class StaffCameraDto {
    private Long id;
    private String numero;
    private StatoCamera status;
    private String clienteAttuale;
    private List<String> note;
}
