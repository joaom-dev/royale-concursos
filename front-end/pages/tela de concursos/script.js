// ─── Configuração ────────────────────────────────────────────────────────────
// Aponte para a URL do seu backend Spring Boot
const API_BASE = 'http://localhost:8080/api/concursos';

// ─── Estado da aplicação ─────────────────────────────────────────────────────
let tagAtiva        = 'all';
let todosOsConcursos = [];   // cache local após o fetch

// ─── Inicialização ────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  carregarConcursos();

  // Barra de pesquisa: filtra em tempo real no cache local
  document.getElementById('search').addEventListener('input', renderCards);

  // Tags rápidas
  document.querySelectorAll('.tag-rapida').forEach(tag => {
    tag.addEventListener('click', () => {
      document.querySelectorAll('.tag-rapida').forEach(t => t.classList.remove('ativo'));
      tag.classList.add('ativo');
      tagAtiva = tag.dataset.tag;
      renderCards();
    });
  });
});

// ─── Busca inicial no backend ─────────────────────────────────────────────────
async function carregarConcursos() {
  mostrarLoading(true);

  try {
    const res = await fetch(API_BASE);

    if (!res.ok) throw new Error(`Erro ${res.status}: ${res.statusText}`);

    todosOsConcursos = await res.json();
    renderCards();

  } catch (err) {
    console.error('Falha ao carregar concursos:', err);
    mostrarErro('Não foi possível carregar os concursos. Tente novamente mais tarde.');
  } finally {
    mostrarLoading(false);
  }
}

// ─── Filtros dos checkboxes da sidebar ───────────────────────────────────────
// Chamado pelo onchange de cada checkbox no HTML
function aplicarFiltros() {
  renderCards();
}

// ─── Renderização dos cards ───────────────────────────────────────────────────
function renderCards() {
  const busca      = document.getElementById('search').value.toLowerCase().trim();
  const grid       = document.getElementById('cards-grid');
  const checkboxes = document.querySelectorAll('.filtro-body input[type="checkbox"]:checked');

  // Agrupa os valores marcados por categoria de filtro
  const filtrosSelecionados = {
    status:   [],
    vagas:    [],
    estado:   [],
    municipio:[],
    orgao:    [],
    nivel:    [],
    ano:      [],
    tipo:     [],
  };

  checkboxes.forEach(cb => {
    const grupo = cb.closest('.filtro-grupo');
    const titulo = grupo.querySelector('.filtro-header span').textContent.toLowerCase();

    if (titulo.includes('status'))          filtrosSelecionados.status.push(cb.value);
    else if (titulo.includes('vagas'))      filtrosSelecionados.vagas.push(cb.value);
    else if (titulo.includes('estado'))     filtrosSelecionados.estado.push(cb.value);
    else if (titulo.includes('município'))  filtrosSelecionados.municipio.push(cb.value);
    else if (titulo.includes('órgão'))      filtrosSelecionados.orgao.push(cb.value);
    else if (titulo.includes('nível'))      filtrosSelecionados.nivel.push(cb.value);
    else if (titulo.includes('ano'))        filtrosSelecionados.ano.push(cb.value);
    else if (titulo.includes('tipo'))       filtrosSelecionados.tipo.push(cb.value);
  });

  const filtrados = todosOsConcursos.filter(c => {
    // ── Tag rápida ──────────────────────────────────────────────────────────
    // A API retorna tipo = "aberto" | "previsto"
    // As tags "open" e "soon" mapeiam para o campo situacao/tipo da API
    const matchTag = (() => {
      if (tagAtiva === 'all')      return true;
      if (tagAtiva === 'open')     return c.tipo === 'aberto';
      if (tagAtiva === 'soon')     return c.tipo === 'previsto';
      if (tagAtiva === 'federal')  return c.situacao?.toLowerCase().includes('federal') || c.orgao?.toLowerCase().includes('federal');
      if (tagAtiva === 'estadual') return c.situacao?.toLowerCase().includes('estadual') || c.uf !== undefined;
      if (tagAtiva === 'municipal')return c.situacao?.toLowerCase().includes('municipal') || c.orgao?.toLowerCase().includes('prefeitura') || c.orgao?.toLowerCase().includes('câmara');
      return true;
    })();

    // ── Barra de busca ──────────────────────────────────────────────────────
    const matchBusca = !busca ||
      c.orgao?.toLowerCase().includes(busca) ||
      c.situacao?.toLowerCase().includes(busca) ||
      c.estado?.toLowerCase().includes(busca) ||
      c.uf?.toLowerCase().includes(busca);

    // ── Checkboxes de status ────────────────────────────────────────────────
    const matchStatus = filtrosSelecionados.status.length === 0 || (() => {
      if (filtrosSelecionados.status.includes('open')   && c.tipo === 'aberto')   return true;
      if (filtrosSelecionados.status.includes('soon')   && c.tipo === 'previsto') return true;
      if (filtrosSelecionados.status.includes('closed') && c.tipo === 'fechado')  return true;
      return false;
    })();

    // ── Checkboxes de estado ────────────────────────────────────────────────
    const matchEstado = filtrosSelecionados.estado.length === 0 ||
      filtrosSelecionados.estado.some(uf => c.uf?.toUpperCase() === uf.toUpperCase());

    // ── Checkboxes de tipo de processo ──────────────────────────────────────
    const matchTipo = filtrosSelecionados.tipo.length === 0 || (() => {
      if (filtrosSelecionados.tipo.includes('concurso') && c.situacao?.toLowerCase().includes('concurso')) return true;
      if (filtrosSelecionados.tipo.includes('seletivo') && c.situacao?.toLowerCase().includes('seletivo')) return true;
      if (filtrosSelecionados.tipo.includes('estagio')  && c.situacao?.toLowerCase().includes('estágio'))  return true;
      return false;
    })();

    return matchTag && matchBusca && matchStatus && matchEstado && matchTipo;
  });

  // ── Atualiza contador ──────────────────────────────────────────────────────
  const total = filtrados.length;
  document.getElementById('contagem-label').innerHTML =
    `Encontramos <strong>${total}</strong> Processo${total !== 1 ? 's' : ''} Público${total !== 1 ? 's' : ''}`;

  // ── Sem resultados ─────────────────────────────────────────────────────────
  if (total === 0) {
    grid.innerHTML = '<p class="sem-resultados">Nenhum concurso encontrado para sua pesquisa.</p>';
    return;
  }

  // ── Renderiza os cards ─────────────────────────────────────────────────────
  grid.innerHTML = filtrados.map(c => `
    <div class="card-concurso" onclick="abrirDetalhes('${c.link || ''}')">
      <div class="card-topo">
        <div class="card-icone">
          <span class="card-icone-sigla">${siglaOrgao(c.orgao)}</span>
        </div>
        <div class="card-texto">
          <span class="card-badge ${badgeClass(c.tipo)}">${badgeTexto(c.tipo)}</span>
          <p class="card-nome">${c.orgao}</p>
          <p class="card-area">${c.situacao || 'Sem descrição'}</p>
        </div>
      </div>
      <div class="card-divisor"></div>
      <div class="card-rodape">
        <span class="card-uf">${c.estado ? c.estado + ' (' + c.uf + ')' : c.uf}</span>
        ${c.vagas ? `<span class="card-vagas">${c.vagas} vagas</span>` : ''}
        <button class="card-ver" onclick="event.stopPropagation(); abrirDetalhes('${c.link || ''}')">
          Ver detalhes →
        </button>
      </div>
    </div>
  `).join('');
}

// ─── Funções auxiliares ───────────────────────────────────────────────────────

// Abre o edital em nova aba (se o link existir)
function abrirDetalhes(link) {
  if (link && link.startsWith('http')) {
    window.open(link, '_blank', 'noopener');
  }
}

// Gera uma sigla de até 3 letras para o ícone do card
function siglaOrgao(nome) {
  if (!nome) return '?';
  const palavras = nome.trim().split(/\s+/);
  if (palavras.length === 1) return palavras[0].substring(0, 3).toUpperCase();
  return palavras
    .filter(p => p.length > 2)
    .slice(0, 2)
    .map(p => p[0].toUpperCase())
    .join('');
}

// CSS class do badge com base no tipo vindo da API
function badgeClass(tipo) {
  if (tipo === 'aberto')   return 'badge-aberto';
  if (tipo === 'previsto') return 'badge-breve';
  return 'badge-fechado';
}

// Texto do badge
function badgeTexto(tipo) {
  if (tipo === 'aberto')   return 'Inscrições abertas';
  if (tipo === 'previsto') return 'Previsto';
  return 'Encerrado';
}

// ─── Filtros (accordion da sidebar) ──────────────────────────────────────────
function toggleFiltro(header) {
  const body    = header.nextElementSibling;
  const chevron = header.querySelector('.filtro-chevron');
  body.classList.toggle('aberto');
  chevron.classList.toggle('aberto');
}

// ─── Loading / Erro ───────────────────────────────────────────────────────────
function mostrarLoading(ativo) {
  const grid = document.getElementById('cards-grid');
  if (ativo) {
    grid.innerHTML = `
      <div class="loading-container">
        <div class="loading-spinner"></div>
        <p>Carregando concursos...</p>
      </div>`;
  }
}

function mostrarErro(msg) {
  const grid = document.getElementById('cards-grid');
  grid.innerHTML = `<p class="sem-resultados" style="color:#b52a2a;">${msg}</p>`;
}