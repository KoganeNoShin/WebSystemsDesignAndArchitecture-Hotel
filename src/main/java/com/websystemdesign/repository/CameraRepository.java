package com.websystemdesign.repository;

import com.websystemdesign.model.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {
    // Per ora lo lasciamo vuoto.
    // JpaRepository ci regala già metodi come: save(), findAll(), findById(), delete()
}
