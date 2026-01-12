package com.websystemdesign.dto.report;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class SchedaTassaDto {
    private String capogruppo;
    private int numeroOspitiTotali;
    private int numeroEsenzioni;
    private String motivoEsenzione;
    private double importoTotale;
}
