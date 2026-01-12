package com.websystemdesign.controller.api;

import com.websystemdesign.dto.CameraDto;
import com.websystemdesign.mapper.CameraMapper;
import com.websystemdesign.model.Camera;
import com.websystemdesign.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
public class CameraController {

    private final CameraService cameraService;
    private final CameraMapper cameraMapper;

    @Autowired
    public CameraController(CameraService cameraService, CameraMapper cameraMapper) {
        this.cameraService = cameraService;
        this.cameraMapper = cameraMapper;
    }

    @GetMapping
    public List<CameraDto> getAllCamera() {
        return cameraService.getAllCamera().stream()
                .map(cameraMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CameraDto getRoomById(@PathVariable Long id) {
        return cameraService.getRoomById(id)
                .map(cameraMapper::toDto)
                .orElse(null);
    }

    @PostMapping
    public CameraDto createRoom(@RequestBody CameraDto cameraDto) {
        Camera camera = cameraMapper.toEntity(cameraDto);
        
        Camera savedCamera = cameraService.saveRoom(camera);
        
        return cameraMapper.toDto(savedCamera);
    }

    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable Long id) {
        cameraService.deleteRoom(id);
    }
}
