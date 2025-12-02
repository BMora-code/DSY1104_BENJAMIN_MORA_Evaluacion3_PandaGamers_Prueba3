package com.example.backend.service;

import com.example.backend.model.Oferta;
import com.example.backend.repository.OfertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfertaService {

    @Autowired
    private OfertaRepository ofertaRepository;

    public List<Oferta> findAll() {
        return ofertaRepository.findAll();
    }

    public Oferta create(Oferta oferta) {
        return ofertaRepository.save(oferta);
    }

    public Oferta update(String id, Oferta oferta) {
        oferta.setId(id);
        return ofertaRepository.save(oferta);
    }

    public void delete(String id) {
        ofertaRepository.deleteById(id);
    }
}