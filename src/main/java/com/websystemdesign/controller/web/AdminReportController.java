package com.websystemdesign.controller.web;

import com.websystemdesign.service.ReportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String showPage() {
        return "admin/reports";
    }

    @GetMapping("/download/questura")
    public ResponseEntity<ByteArrayResource> downloadQuestura() {
        try {
            String xml = reportService.generaXmlQuestura();
            ByteArrayResource resource = new ByteArrayResource(xml.getBytes());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=report_questura.xml")
                    .contentType(MediaType.APPLICATION_XML)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/download/tassa")
    public ResponseEntity<ByteArrayResource> downloadTassa() {
        try {
            String xml = reportService.generaXmlTassa();
            ByteArrayResource resource = new ByteArrayResource(xml.getBytes());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=report_tassa.xml")
                    .contentType(MediaType.APPLICATION_XML)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
