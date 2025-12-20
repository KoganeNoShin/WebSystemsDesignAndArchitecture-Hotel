package com.websystemdesign.dto.report;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class SchedaTassaDto {
    private String capogruppo;       // Nome e Cognome
    private int numeroOspitiTotali;  // Quante persone pagano
    private int numeroEsenzioni;     // Quanti non pagano (es. bambini)
    private String motivoEsenzione;  // Es. "Minori di 12 anni"
    private double importoTotale;    // Quanto devono pagare
}
