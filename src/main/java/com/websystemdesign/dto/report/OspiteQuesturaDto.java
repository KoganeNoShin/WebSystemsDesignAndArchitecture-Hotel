package com.websystemdesign.dto.report;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class OspiteQuesturaDto {
    private String nome;
    private String cognome;
    private String dataNascita;
    private String luogoNascita;
    private String cittadinanza;

    private String tipoDocumento;
    private String numeroDocumento;
}
