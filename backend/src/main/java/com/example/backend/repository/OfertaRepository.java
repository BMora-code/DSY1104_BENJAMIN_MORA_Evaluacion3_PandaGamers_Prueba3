package com.example.backend.repository;

import com.example.backend.model.Oferta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfertaRepository extends MongoRepository<Oferta, String> {

}