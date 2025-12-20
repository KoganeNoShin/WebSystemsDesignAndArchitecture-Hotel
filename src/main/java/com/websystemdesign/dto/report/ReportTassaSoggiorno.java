package com.websystemdesign.dto.report;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import java.util.List;

@Data
@XmlRootElement(name = "ReportTassaSoggiorno")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportTassaSoggiorno {

    @XmlElement(name = "Periodo")
    private String periodo;

    @XmlElementWrapper(name = "ListaPernottamenti")
    @XmlElement(name = "Scheda")
    private List<SchedaTassaDto> schede;
}
