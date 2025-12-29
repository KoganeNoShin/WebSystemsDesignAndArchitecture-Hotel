package com.websystemdesign.controller.web;

import com.websystemdesign.dto.ClienteProfileDto;
import com.websystemdesign.model.Cliente;
import com.websystemdesign.model.TipoDocumento;
import com.websystemdesign.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/cliente")
public class ClienteProfileController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteProfileController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/profile")
    public String showProfileForm(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Cliente cliente = clienteService.getClienteByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato"));

        ClienteProfileDto dto = new ClienteProfileDto();
        // Dati Utente (Read-only)
        dto.setNome(cliente.getUtente().getNome());
        dto.setCognome(cliente.getUtente().getCognome());
        dto.setUsername(cliente.getUtente().getUsername());

        // Dati Cliente (Editable) - Gestione Null Safety
        dto.setCittadinanza(cliente.getCittadinanza());
        dto.setLuogoNascita(cliente.getLuogo());
        dto.setNumDocumento(cliente.getNumDocumento());
        
        // Conversione Data Nascita (String -> LocalDate)
        if (cliente.getDataNascita() != null && !cliente.getDataNascita().isEmpty()) {
            try {
                dto.setDataNascita(LocalDate.parse(cliente.getDataNascita()));
            } catch (DateTimeParseException e) {
                // Ignora se il formato è errato
            }
        }

        // Conversione Tipo Documento (String -> Enum)
        if (cliente.getTipoDocumento() != null && !cliente.getTipoDocumento().isEmpty()) {
            for (TipoDocumento tipo : TipoDocumento.values()) {
                if (tipo.getDescrizione().equals(cliente.getTipoDocumento())) {
                    dto.setTipoDocumento(tipo);
                    break;
                }
            }
        }

        model.addAttribute("profileDto", dto);
        return "cliente/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileDto") ClienteProfileDto dto,
                                BindingResult bindingResult,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "cliente/profile";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        clienteService.updateClienteProfile(userDetails.getUsername(), dto);

        redirectAttributes.addFlashAttribute("successMessage", "Dati aggiornati con successo!");
        return "redirect:/cliente/dashboard";
    }
}
