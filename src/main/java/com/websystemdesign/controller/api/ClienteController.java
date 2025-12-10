package com.websystemdesign.controller.api;

import com.websystemdesign.model.Cliente;
import com.websystemdesign.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService customerService){
        this.clienteService = customerService;
    }

    //un po' particolare, li prendo tutti da tutti gli hotel? bisogna realizzare qualche query un po' più specifica
    @GetMapping
    public List<Cliente> getAllClienti(){
        return clienteService.getAllClienti();
    }

    @GetMapping("/{id}")
    public Optional<Cliente> getClienteByID(@PathVariable Long id){
        return clienteService.getClienteById(id);
    }

    @PostMapping
    public Cliente saveCliente(@RequestBody Cliente customer){
        return clienteService.saveCliente(customer);
    }

    @DeleteMapping("/{id}")
    public void deleteCliente(@PathVariable Long id){
        clienteService.deleteCliente(id);
    }
}
