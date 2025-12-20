package com.websystemdesign.dto.report;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import java.util.List;

@Data
@XmlRootElement(name = "ReportQuestura")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportQuestura {

    @XmlElement(name = "DataGenerazione")
    private String dataGenerazione;

    @XmlElementWrapper(name = "ElencoOspiti")
    @XmlElement(name = "Ospite")
    private List<OspiteQuesturaDto> ospiti;
}
