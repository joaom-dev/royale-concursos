package com.royaleconcursos.service;

import com.royaleconcursos.dto.AnuncioDTO;
import com.royaleconcursos.model.Anuncio;
import com.royaleconcursos.repository.AnuncioRepository;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnuncioService {
    
    @Autowired
    private AnuncioRepository anuncioRepository;

    public List<Anuncio> listarAtivos () {
        return anuncioRepository.findAnunciosAtivos(LocalDateTime.now());
    }

    public Anuncio criar (AnuncioDTO dto) {

        Anuncio a = new Anuncio();
        a.setTitulo(dto.getTitulo());
        a.setDescricao(dto.getDescricao());
        a.setImagemUrl(dto.getImagemUrl());
        a.setLinkDestino(dto.getLinkDestino());
        a.setPosicao(dto.getPosicao());
        a.setDataInicio(dto.getDataInicio());
        a.setDataFim(dto.getDataFim());
        a.setAtivo(true);
        return anuncioRepository.save(a);
    }

    public void toggleAtivo (Long id){
        Anuncio a = anuncioRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Anuncio não encontrado"));
        a.setAtivo(!a.isAtivo());
        anuncioRepository.save(a);
    }

    public void deletar (Long id) {
        anuncioRepository.deleteById(id);
    }
}
