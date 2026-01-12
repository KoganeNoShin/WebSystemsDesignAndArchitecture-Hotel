package com.websystemdesign.service;

import com.websystemdesign.dto.report.*;
import com.websystemdesign.model.*;
import com.websystemdesign.repository.PrenotazioneRepository;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final PrenotazioneRepository prenotazioneRepository;

    public ReportService(PrenotazioneRepository prenotazioneRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
    }

    public String generaXmlQuestura() throws Exception {
        List<Prenotazione> attive = prenotazioneRepository.findAll().stream()
                .filter(p -> p.getStato() == StatoPrenotazione.CHECKED_IN)
                .collect(Collectors.toList());

        ReportQuestura report = new ReportQuestura();
        report.setDataGenerazione(LocalDate.now().toString());
        List<OspiteQuesturaDto> lista = new ArrayList<>();

        for (Prenotazione p : attive) {
            Cliente c = p.getCliente();

            OspiteQuesturaDto capo = new OspiteQuesturaDto();
            capo.setNome(c.getUtente().getNome());
            capo.setCognome(c.getUtente().getCognome());
            capo.setDataNascita(c.getDataNascita());
            capo.setLuogoNascita(c.getLuogo());
            capo.setCittadinanza(c.getCittadinanza());
            capo.setTipoDocumento(c.getTipoDocumento());
            capo.setNumeroDocumento(c.getNumDocumento());
            lista.add(capo);

            if (p.getOspiti() != null) {
                for (Ospite o : p.getOspiti()) {
                    OspiteQuesturaDto ospite = new OspiteQuesturaDto();
                    ospite.setNome(o.getNome());
                    ospite.setCognome(o.getCognome());
                    ospite.setDataNascita(o.getDataNascita().toString());
                    ospite.setLuogoNascita(o.getLuogo());
                    ospite.setCittadinanza(o.getCittadinanza());
                    lista.add(ospite);
                }
            }
        }
        report.setOspiti(lista);
        return marshalToXml(report);
    }

    public String generaXmlTassa() throws Exception {
        List<Prenotazione> attive = prenotazioneRepository.findAll().stream()
                .filter(p -> p.getStato() == StatoPrenotazione.CHECKED_IN)
                .collect(Collectors.toList());

        ReportTassaSoggiorno report = new ReportTassaSoggiorno();
        report.setPeriodo(LocalDate.now().toString());
        List<SchedaTassaDto> schede = new ArrayList<>();

        for (Prenotazione p : attive) {
            SchedaTassaDto scheda = new SchedaTassaDto();
            scheda.setCapogruppo(p.getCliente().getUtente().getNome() + " " + p.getCliente().getUtente().getCognome());

            int numOspitiExtra = (p.getOspiti() != null) ? p.getOspiti().size() : 0;
            int totalePersone = 1 + numOspitiExtra;
            scheda.setNumeroOspitiTotali(totalePersone);

            int esenti = 0;
            if (isUnder12(p.getCliente().getDataNascita())) esenti++;

            if (p.getOspiti() != null) {
                for (Ospite o : p.getOspiti()) {
                    if (calcolaEta(o.getDataNascita()) < 12) esenti++;
                }
            }

            scheda.setNumeroEsenzioni(esenti);
            scheda.setMotivoEsenzione(esenti > 0 ? "Minori di 12 anni" : "");

            double costoTassa = 0.0;
            try {
                costoTassa = Double.parseDouble(p.getCamera().getSede().getTassaSoggiorno());
            } catch (NumberFormatException e) {
                costoTassa = 0.0;
            }

            scheda.setImportoTotale(costoTassa * (totalePersone - esenti));
            schede.add(scheda);
        }
        report.setSchede(schede);
        return marshalToXml(report);
    }

    private String marshalToXml(Object object) throws Exception {
        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);
        return writer.toString();
    }

    private int calcolaEta(LocalDate dataNascita) {
        if (dataNascita == null) return 0;
        return Period.between(dataNascita, LocalDate.now()).getYears();
    }

    private boolean isUnder12(String dataNascitaString) {
        try {
            LocalDate data = LocalDate.parse(dataNascitaString);
            return calcolaEta(data) < 12;
        } catch (Exception e) {
            return false;
        }
    }
}
