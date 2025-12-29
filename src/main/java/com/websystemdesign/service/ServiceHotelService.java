package com.websystemdesign.service;

import com.websystemdesign.model.Service;
import com.websystemdesign.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceHotelService {

    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceHotelService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
}
