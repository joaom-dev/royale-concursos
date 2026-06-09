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
import java.util.Optional;
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

    // Roda ao iniciar e depois a cada 6 horas
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

                JsonNode root    = objectMapper.readTree(json);
                String estado    = root.path("estado").asText();
                List<Concurso> novos = new ArrayList<>();

                JsonNode abertos   = root.path("concursos_abertos");
                JsonNode previstos = root.path("concursos_previstos");

                if (abertos.isArray())   abertos.forEach(n  -> novos.add(mapear(n, uf, estado, "aberto")));
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

    // ─── Métodos de consulta (usados pelo Controller) ─────────────────────────

    public List<ConcursoDTO> listarTodos() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Novo: busca por ID individual (usado pela tela de detalhes)
    public Optional<ConcursoDTO> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDTO);
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

    // ─── Helpers privados ─────────────────────────────────────────────────────

    // Tenta ler um campo tentando várias grafias possíveis que a API pode retornar
    private String ler(JsonNode node, String... chaves) {
        for (String chave : chaves) {
            String valor = node.path(chave).asText("");
            if (!valor.isBlank()) return valor;
        }
        return "";
    }

    private Concurso mapear(JsonNode node, String uf, String estado, String tipo) {
        Concurso c = new Concurso();

        // ── Campos básicos ────────────────────────────────────────────────────
        String orgao = ler(node, "Órgão", "orgao", "Orgao", "organ");
        c.setOrgao(orgao.isBlank() ? "Não informado" : orgao);

        c.setSituacao(ler(node, "Situação", "situacao", "Situacao", "situation"));
        c.setVagas(ler(node, "Vagas", "vagas", "vacancies"));
        c.setUf(uf.toUpperCase());
        c.setEstado(estado);
        c.setTipo(tipo);
        c.setAtualizadoEm(LocalDateTime.now());

        // ── Link de inscrição ─────────────────────────────────────────────────
        String link = ler(node, "link", "Link", "url", "URL", "linkInscricao", "link_inscricao");
        c.setLink(link.isBlank() ? null : link);

        // ── Edital ────────────────────────────────────────────────────────────
        String editalUrl = ler(node, "edital_url", "EditalUrl", "edital", "Edital",
                               "link_edital", "linkEdital", "editalLink");
        c.setEditalUrl(editalUrl.isBlank() ? null : editalUrl);

        // ── Período de inscrições ─────────────────────────────────────────────
        String periodo = ler(node, "periodo_inscricao", "Período de Inscrição",
                             "PeriodoInscricao", "periodo", "Periodo");
        // Tenta montar a partir de data_inicio + data_fim se não vier direto
        if (periodo.isBlank()) {
            String inicio = ler(node, "data_inicio", "DataInicio", "dataInicio", "inicio");
            String fim    = ler(node, "data_fim",    "DataFim",    "dataFim",    "fim");
            if (!inicio.isBlank() && !fim.isBlank()) periodo = inicio + " até " + fim;
            else if (!inicio.isBlank())              periodo = "A partir de " + inicio;
        }
        c.setPeriodoInscricao(periodo.isBlank() ? null : periodo);

        // ── Nível de escolaridade ─────────────────────────────────────────────
        String nivel = ler(node, "nivel", "Nível", "nivel_escolaridade",
                           "escolaridade", "Escolaridade", "education");
        c.setNivel(nivel.isBlank() ? null : nivel);

        // ── Banca ─────────────────────────────────────────────────────────────
        String banca = ler(node, "banca", "Banca", "organizadora",
                           "Organizadora", "bancaOrganizadora");
        c.setBanca(banca.isBlank() ? null : banca);

        // ── Cargo ─────────────────────────────────────────────────────────────
        String cargo = ler(node, "cargo", "Cargo", "cargos", "Cargos", "position");
        c.setCargo(cargo.isBlank() ? null : cargo);

        // ── Salário ───────────────────────────────────────────────────────────
        String salario = ler(node, "salario", "Salário", "remuneracao",
                             "Remuneração", "remuneracao_base", "salary");
        c.setSalario(salario.isBlank() ? null : salario);

        // ── Requisitos ────────────────────────────────────────────────────────
        String requisitos = ler(node, "requisitos", "Requisitos", "requirements");
        c.setRequisitos(requisitos.isBlank() ? null : requisitos);

        // ── Benefícios ────────────────────────────────────────────────────────
        String beneficios = ler(node, "beneficios", "Benefícios", "beneficio",
                                "Beneficio", "benefits");
        c.setBeneficios(beneficios.isBlank() ? null : beneficios);

        // ── Carga horária ─────────────────────────────────────────────────────
        String cargaHoraria = ler(node, "carga_horaria", "CargaHoraria",
                                  "Carga Horária", "cargaHoraria", "workload");
        c.setCargaHoraria(cargaHoraria.isBlank() ? null : cargaHoraria);

        // ── Observação / aviso ────────────────────────────────────────────────
        String observacao = ler(node, "observacao", "Observação", "observacoes",
                                "aviso", "Aviso", "obs", "notice");
        c.setObservacao(observacao.isBlank() ? null : observacao);

        return c;
    }

    private ConcursoDTO toDTO(Concurso c) {
        return new ConcursoDTO(
            c.getId(),
            c.getOrgao(),
            c.getSituacao(),
            c.getUf(),
            c.getEstado(),
            c.getTipo(),
            c.getVagas(),
            c.getLink(),
            c.getEditalUrl(),
            c.getPeriodoInscricao(),
            c.getNivel(),
            c.getBanca(),
            c.getCargo(),
            c.getSalario(),
            c.getRequisitos(),
            c.getBeneficios(),
            c.getCargaHoraria(),
            c.getObservacao(),
            c.getAtualizadoEm()
        );
    }
}