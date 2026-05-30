package com.royaleconcursos.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.royaleconcursos.dto.ConcursoDTO;
import com.royaleconcursos.model.Concurso;
import com.royaleconcursos.repository.ConcursoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConcursoService {

    private static final Logger log = LoggerFactory.getLogger(ConcursoService.class);

    private static final String API_BASE_URL = "https://concursos-api.deno.dev";

    private static final String[] UFS = {
        "ac", "al", "ap", "am", "ba", "ce", "df", "es", "go",
        "ma", "mt", "ms", "mg", "pa", "pb", "pr", "pe", "pi",
        "rj", "rn", "rs", "ro", "rr", "sc", "sp", "se", "to"
    };

    private final ConcursoRepository repository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ConcursoService(ConcursoRepository repository) {
        this.repository   = repository;
        this.webClient    = WebClient.create(API_BASE_URL);
        this.objectMapper = new ObjectMapper();
    }

    // Roda ao iniciar e depois a cada 6 horas (em ms: 6 * 60 * 60 * 1000)
    @Scheduled(fixedDelay = 21_600_000, initialDelay = 0)
    @Transactional
    public void sincronizarTodosOsEstados() {
        log.info("===== Iniciando sincronização de concursos [{}] =====", LocalDateTime.now());

        for (String uf : UFS) {
            try {
                String json = webClient.get()
                        .uri("/" + uf)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (json == null || json.isBlank()) {
                    log.warn("Resposta vazia para UF: {}", uf.toUpperCase());
                    continue;
                }

                JsonNode root   = objectMapper.readTree(json);
                String estado   = root.path("estado").asText();
                List<Concurso> novos = new ArrayList<>();

                JsonNode abertos  = root.path("concursos_abertos");
                JsonNode previstos = root.path("concursos_previstos");

                if (abertos.isArray())  abertos.forEach(n  -> novos.add(mapear(n, uf, estado, "aberto")));
                if (previstos.isArray()) previstos.forEach(n -> novos.add(mapear(n, uf, estado, "previsto")));

                repository.deleteByUfIgnoreCase(uf);
                repository.saveAll(novos);

                log.info("UF {} — {} concursos salvos", uf.toUpperCase(), novos.size());

            } catch (Exception e) {
                log.error("Erro ao sincronizar UF {}: {}", uf.toUpperCase(), e.getMessage());
            }
        }

        log.info("===== Sincronização concluída [{}] =====", LocalDateTime.now());
    }

    //  Métodos de consulta (usados pelo Controller) 

    public List<ConcursoDTO> listarTodos() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ConcursoDTO> listarPorUf(String uf) {
        return repository.findByUfIgnoreCase(uf).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ConcursoDTO> listarPorTipo(String tipo) {
        return repository.findByTipo(tipo).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ConcursoDTO> listarPorUfETipo(String uf, String tipo) {
        return repository.findByUfIgnoreCaseAndTipo(uf, tipo).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ConcursoDTO> buscarPorOrgao(String texto) {
        return repository.findByOrgaoContainingIgnoreCase(texto).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Helpers privados

    private Concurso mapear(JsonNode node, String uf, String estado, String tipo) {
        Concurso c = new Concurso();
        // A API pode retornar "Órgão" (com acento) ou "orgao" dependendo da versão
        String orgao = node.path("Órgão").asText(node.path("orgao").asText(""));
        c.setOrgao(orgao.isBlank() ? "Não informado" : orgao);
        c.setSituacao(node.path("Situação").asText(node.path("situacao").asText("")));
        c.setVagas(node.path("Vagas").asText(node.path("vagas").asText("")));
        c.setLink(node.path("link").asText(""));
        c.setUf(uf.toUpperCase());
        c.setEstado(estado);
        c.setTipo(tipo);
        c.setAtualizadoEm(LocalDateTime.now());
        return c;
    }

    private ConcursoDTO toDTO(Concurso c) {
        return new ConcursoDTO(
            c.getId(), c.getOrgao(), c.getSituacao(),
            c.getUf(), c.getEstado(), c.getTipo(),
            c.getVagas(), c.getLink(), c.getAtualizadoEm()
        );
    }
}