package com.websystemdesign.controller.web;

import com.websystemdesign.dto.CameraDto;
import com.websystemdesign.dto.SedeDto;
import com.websystemdesign.mapper.CameraMapper;
import com.websystemdesign.model.Camera;
import com.websystemdesign.model.Sede;
import com.websystemdesign.model.StatoCamera;
import com.websystemdesign.service.CameraService;
import com.websystemdesign.service.SedeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/sedi")
public class AdminSedeController {

    private final SedeService sedeService;
    private final CameraService cameraService;
    private final CameraMapper cameraMapper;

    @Autowired
    public AdminSedeController(SedeService sedeService, CameraService cameraService, CameraMapper cameraMapper) {
        this.sedeService = sedeService;
        this.cameraService = cameraService;
        this.cameraMapper = cameraMapper;
    }

    // --- GESTIONE SEDI ---

    @GetMapping
    public String showSediPage(Model model) {
        model.addAttribute("sedi", sedeService.getAllSedi());
        if (!model.containsAttribute("sedeDto")) {
            model.addAttribute("sedeDto", new SedeDto());
        }
        return "admin/sedi";
    }

    @PostMapping("/new")
    public String addSede(@Valid @ModelAttribute("sedeDto") SedeDto sedeDto,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("sedi", sedeService.getAllSedi());
            model.addAttribute("openModal", true);
            return "admin/sedi";
        }

        Sede nuovaSede = new Sede(sedeDto.getNome(), sedeDto.getLocation(), sedeDto.getTassaSoggiorno());
        sedeService.saveSede(nuovaSede);

        redirectAttributes.addFlashAttribute("successMessage", "Struttura creata con successo!");
        return "redirect:/admin/sedi";
    }

    @PostMapping("/delete/{id}")
    public String deleteSede(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sedeService.deleteSede(id);
            redirectAttributes.addFlashAttribute("successMessage", "Struttura eliminata.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore: impossibile eliminare la sede (potrebbe avere camere o dipendenti associati).");
        }
        return "redirect:/admin/sedi";
    }

    // --- GESTIONE CAMERE PER UNA SPECIFICA SEDE ---

    @GetMapping("/{id}/camere")
    public String showCamerePage(@PathVariable("id") Long sedeId, Model model) {
        Sede sede = sedeService.getSedeById(sedeId)
                .orElseThrow(() -> new IllegalArgumentException("Sede non valida:" + sedeId));

        model.addAttribute("sede", sede);
        // Usiamo il nuovo metodo del service creato nello Step 1
        model.addAttribute("camere", cameraService.getCamereBySede(sedeId));

        if (!model.containsAttribute("cameraDto")) {
            CameraDto dto = new CameraDto();
            dto.setSedeId(sedeId); // Pre-impariamo l'ID della sede
            model.addAttribute("cameraDto", dto);
        }

        return "admin/camere"; // Nuovo file HTML
    }

    // Rinominiamo la rotta in 'save' perché gestisce sia create che update
    @PostMapping("/{id}/camere/save")
    public String saveCamera(@PathVariable("id") Long sedeId,
                             @Valid @ModelAttribute("cameraDto") CameraDto cameraDto,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            Sede sede = sedeService.getSedeById(sedeId).orElseThrow();
            model.addAttribute("sede", sede);
            model.addAttribute("camere", cameraService.getCamereBySede(sedeId));
            model.addAttribute("openModal", true);
            return "admin/camere";
        }

        Sede sede = sedeService.getSedeById(sedeId)
                .orElseThrow(() -> new IllegalArgumentException("Sede non valida"));

        Camera camera;

        if (cameraDto.getId() != null) {
            // UPDATE: Recuperiamo l'esistente
            camera = cameraService.getRoomById(cameraDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Camera non trovata"));

            // Aggiorniamo i campi (INCLUSA LA NUOVA TIPOLOGIA)
            camera.setNumero(cameraDto.getNumero());
            camera.setPostiLetto(cameraDto.getPostiLetto());
            camera.setPrezzoBase(cameraDto.getPrezzoBase());
            camera.setTipologia(cameraDto.getTipologia()); // <--- NUOVO!
            camera.setLuce(cameraDto.isLuce());
            camera.setTapparelle(cameraDto.isTapparelle());
            camera.setTemperatura(cameraDto.getTemperatura());

            // Nota: Non aggiorniamo lo status qui per non sovrascrivere check-in
        } else {
            // CREATE: Usiamo il mapper
            camera = cameraMapper.toEntity(cameraDto);
            camera.setSede(sede);
            // Se la tipologia è null, mettiamo default
            if (camera.getTipologia() == null || camera.getTipologia().isEmpty()) {
                camera.setTipologia("Standard");
            }
            camera.setStatus(StatoCamera.LIBERA);
        }

        cameraService.saveRoom(camera);

        redirectAttributes.addFlashAttribute("successMessage",
                cameraDto.getId() != null ? "Camera aggiornata!" : "Camera creata!");

        return "redirect:/admin/sedi/" + sedeId + "/camere";
    }

    @PostMapping("/camere/delete/{id}")
    public String deleteCamera(@PathVariable Long id, @RequestParam Long sedeId, RedirectAttributes redirectAttributes) {
        cameraService.deleteRoom(id);
        redirectAttributes.addFlashAttribute("successMessage", "Camera eliminata.");
        return "redirect:/admin/sedi/" + sedeId + "/camere";
    }
}