package com.websystemdesign.controller.api;

import com.websystemdesign.dto.ClienteDto;
import com.websystemdesign.mapper.ClienteMapper;
import com.websystemdesign.model.Cliente;
import com.websystemdesign.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clienti")
public class ClienteController {

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    @Autowired
    public ClienteController(ClienteService clienteService, ClienteMapper clienteMapper) {
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
    }

    @GetMapping
    public List<ClienteDto> getAllClienti() {
        return clienteService.getAllClienti().stream()
                .map(clienteMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ClienteDto getClienteById(@PathVariable Long id) {
        return clienteService.getClienteById(id)
                .map(clienteMapper::toDto)
                .orElse(null);
    }

    @PostMapping
    public ClienteDto createCliente(@RequestBody ClienteDto clienteDto) {
        Cliente cliente = clienteMapper.toEntity(clienteDto);
        Cliente savedCliente = clienteService.saveCliente(cliente);
        return clienteMapper.toDto(savedCliente);
    }

    @DeleteMapping("/{id}")
    public void deleteCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
    }
}
