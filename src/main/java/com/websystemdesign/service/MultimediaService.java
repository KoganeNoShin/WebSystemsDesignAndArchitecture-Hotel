package com.websystemdesign.service;

import com.websystemdesign.model.Multimedia;
import com.websystemdesign.repository.MultimediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MultimediaService {

    private final MultimediaRepository multimediaRepository;

    @Autowired
    public MultimediaService(MultimediaRepository multimediaRepository) {
        this.multimediaRepository = multimediaRepository;
    }

    public List<Multimedia> getAllMultimedia() {
        return multimediaRepository.findAll();
    }

    public Optional<Multimedia> getMultimediaById(Long id) {
        return multimediaRepository.findById(id);
    }

    public Multimedia saveMultimedia(Multimedia multimedia) {
        return multimediaRepository.save(multimedia);
    }

    public void deleteMultimedia(Long id) {
        multimediaRepository.deleteById(id);
    }
}
