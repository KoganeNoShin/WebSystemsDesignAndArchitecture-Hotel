package com.websystemdesign.repository;

import com.websystemdesign.model.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {
    List<Camera> findBySedeId(Long sedeId);
}
