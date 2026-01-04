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
import java.time.Period;
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
        dto.setNome(cliente.getUtente().getNome());
        dto.setCognome(cliente.getUtente().getCognome());
        dto.setUsername(cliente.getUtente().getUsername());

        dto.setCittadinanza(cliente.getCittadinanza());
        dto.setLuogoNascita(cliente.getLuogo());
        dto.setNumDocumento(cliente.getNumDocumento());
        
        if (cliente.getDataNascita() != null && !cliente.getDataNascita().isEmpty()) {
            try {
                dto.setDataNascita(LocalDate.parse(cliente.getDataNascita()));
            } catch (DateTimeParseException e) {
                // Ignora
            }
        }

        if (cliente.getTipoDocumento() != null && !cliente.getTipoDocumento().isEmpty()) {
            for (TipoDocumento tipo : TipoDocumento.values()) {
                if (tipo.getDescrizione().equals(cliente.getTipoDocumento())) {
                    dto.setTipoDocumento(tipo);
                    break;
                }
            }
        }
        
        boolean isProfileComplete = checkProfileComplete(cliente);

        model.addAttribute("profileDto", dto);
        model.addAttribute("isProfileComplete", isProfileComplete);
        
        return "cliente/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileDto") ClienteProfileDto dto,
                                BindingResult bindingResult,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Cliente cliente = clienteService.getClienteByUsername(username).orElseThrow();
        
        // Ricalcola isProfileComplete per la vista in caso di errore
        boolean isProfileComplete = checkProfileComplete(cliente);
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("isProfileComplete", isProfileComplete);
            return "cliente/profile";
        }
        
        // Validazione Età (18+) solo se stiamo modificando la data
        if (dto.getDataNascita() != null) {
            if (Period.between(dto.getDataNascita(), LocalDate.now()).getYears() < 18) {
                bindingResult.rejectValue("dataNascita", "error.dataNascita", "Devi essere maggiorenne.");
                model.addAttribute("isProfileComplete", isProfileComplete);
                return "cliente/profile";
            }
        }
        
        // Se i dati anagrafici erano già presenti, non li sovrascriviamo (immutabilità)
        // Ma permettiamo sempre l'aggiornamento del documento
        if (isProfileComplete) {
            dto.setCittadinanza(cliente.getCittadinanza());
            dto.setLuogoNascita(cliente.getLuogo());
            if (cliente.getDataNascita() != null) {
                dto.setDataNascita(LocalDate.parse(cliente.getDataNascita()));
            }
        }

        clienteService.updateClienteProfile(username, dto);

        redirectAttributes.addFlashAttribute("successMessage", "Dati aggiornati con successo!");
        return "redirect:/cliente/dashboard";
    }
    
    private boolean checkProfileComplete(Cliente cliente) {
        return cliente.getCittadinanza() != null && !cliente.getCittadinanza().isEmpty() &&
               cliente.getLuogo() != null && !cliente.getLuogo().isEmpty() &&
               cliente.getDataNascita() != null && !cliente.getDataNascita().isEmpty();
               // Non controlliamo il documento qui per il lock, perché il documento è sempre editabile
    }
}
