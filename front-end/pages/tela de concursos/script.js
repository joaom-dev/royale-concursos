// Caminho para a página de detalhes
const DETALHES_PATH = '/front-end/pages/detalhes-concurso/index.html';

// Aponte para a URL do seu backend Spring Boot
const API_BASE = 'http://localhost:8080/api/concursos';


let tagAtiva = 'all';
let todosOsConcursos = [];   // cache local após o fetch

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

// Busca inicial no backend
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

// Chamado pelo onchange de cada checkbox no HTML
function aplicarFiltros() {
  renderCards();
}

// Renderização dos cards
function renderCards() {
  const busca = document.getElementById('search').value.toLowerCase().trim();
  const grid = document.getElementById('cards-grid');
  const checkboxes = document.querySelectorAll('.filtro-body input[type="checkbox"]:checked');

  // Agrupa os valores marcados por categoria de filtro
  const filtrosSelecionados = {
    status: [],
    vagas: [],
    estado: [],
    municipio: [],
    orgao: [],
    nivel: [],
    ano: [],
    tipo: [],
  };

  checkboxes.forEach(cb => {
    const grupo = cb.closest('.filtro-grupo');
    const titulo = grupo.querySelector('.filtro-header span').textContent.toLowerCase();

    if (titulo.includes('status')) filtrosSelecionados.status.push(cb.value);
    else if (titulo.includes('vagas')) filtrosSelecionados.vagas.push(cb.value);
    else if (titulo.includes('estado')) filtrosSelecionados.estado.push(cb.value);
    else if (titulo.includes('município')) filtrosSelecionados.municipio.push(cb.value);
    else if (titulo.includes('órgão')) filtrosSelecionados.orgao.push(cb.value);
    else if (titulo.includes('nível')) filtrosSelecionados.nivel.push(cb.value);
    else if (titulo.includes('ano')) filtrosSelecionados.ano.push(cb.value);
    else if (titulo.includes('tipo')) filtrosSelecionados.tipo.push(cb.value);
  });

  const filtrados = todosOsConcursos.filter(c => {
    // ── Tag rápida ──────────────────────────────────────────────────────────
    const matchTag = (() => {
      if (tagAtiva === 'all') return true;
      if (tagAtiva === 'open') return c.tipo === 'aberto';
      if (tagAtiva === 'soon') return c.tipo === 'previsto';
      if (tagAtiva === 'federal') return c.situacao?.toLowerCase().includes('federal') || c.orgao?.toLowerCase().includes('federal');
      if (tagAtiva === 'estadual') return c.situacao?.toLowerCase().includes('estadual') || c.uf !== undefined;
      if (tagAtiva === 'municipal') return c.situacao?.toLowerCase().includes('municipal') || c.orgao?.toLowerCase().includes('prefeitura') || c.orgao?.toLowerCase().includes('câmara');
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
      if (filtrosSelecionados.status.includes('open') && c.tipo === 'aberto') return true;
      if (filtrosSelecionados.status.includes('soon') && c.tipo === 'previsto') return true;
      if (filtrosSelecionados.status.includes('closed') && c.tipo === 'fechado') return true;
      return false;
    })();

    // ── Checkboxes de estado ────────────────────────────────────────────────
    const matchEstado = filtrosSelecionados.estado.length === 0 ||
      filtrosSelecionados.estado.some(uf => c.uf?.toUpperCase() === uf.toUpperCase());

    // ── Checkboxes de tipo de processo ──────────────────────────────────────
    const matchTipo = filtrosSelecionados.tipo.length === 0 || (() => {
      if (filtrosSelecionados.tipo.includes('concurso') && c.situacao?.toLowerCase().includes('concurso')) return true;
      if (filtrosSelecionados.tipo.includes('seletivo') && c.situacao?.toLowerCase().includes('seletivo')) return true;
      if (filtrosSelecionados.tipo.includes('estagio') && c.situacao?.toLowerCase().includes('estágio')) return true;
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
  grid.innerHTML = filtrados.map((c, index) => {
    // Usa c.id se disponível, caso contrário usa o índice como fallback
    const concursoId = c.id !== undefined ? c.id : index;

    return `
      <div class="card-concurso" onclick="abrirDetalhes('${concursoId}')">
        <div class="card-topo">
          <div class="card-icone">
            ${iconeHtml(c.orgao, c.link)}
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
          <button class="card-ver" onclick="event.stopPropagation(); abrirDetalhes('${concursoId}')">
            Ver detalhes →
          </button>
        </div>
      </div>
    `;
  }).join('');
}

// ─── Redireciona para a página de detalhes do concurso ───────────────────────
function abrirDetalhes(id) {
  const url = `${DETALHES_PATH}?id=${encodeURIComponent(id)}`;
  window.location.href = url;
}

// ─── Tabela de logos conhecidos ───────────────────────────────────────────────
const LOGOS_CONHECIDOS = {
  'ibge': 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/IBGE_logo.svg/320px-IBGE_logo.svg.png',
  'receita': 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/54/Receita_Federal_do_Brasil_logo.svg/320px-Receita_Federal_do_Brasil_logo.svg.png',
  'ciee': 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/CIEE_logo.svg/320px-CIEE_logo.svg.png',
  'tj-mg': 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/Brasao_TJMG.svg/200px-Brasao_TJMG.svg.png',
  'tjmg': 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/Brasao_TJMG.svg/200px-Brasao_TJMG.svg.png',
  'justiça federal': 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
  'jfrn': 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
  'jfsp': 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
  'trt': 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
  'tse': 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
  'stj': 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
  'stf': 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
  'polícia civil': 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/forty/Coat_of_arms_of_Brazil.svg/200px-Coat_of_arms_of_Brazil.svg.png',
  'polícia militar': 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/forty/Coat_of_arms_of_Brazil.svg/200px-Coat_of_arms_of_Brazil.svg.png',
  'prefeitura': 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Bras%C3%A3o_de_armas_do_Brasil.svg/200px-Bras%C3%A3o_de_armas_do_Brasil.svg.png',
  'câmara': 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Bras%C3%A3o_de_armas_do_Brasil.svg/200px-Bras%C3%A3o_de_armas_do_Brasil.svg.png',
  'governo': 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Bras%C3%A3o_de_armas_do_Brasil.svg/200px-Bras%C3%A3o_de_armas_do_Brasil.svg.png',
  'cetesb': 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Bras%C3%A3o_de_armas_do_Brasil.svg/200px-Bras%C3%A3o_de_armas_do_Brasil.svg.png',
  'spprev': 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Bras%C3%A3o_de_armas_do_Brasil.svg/200px-Bras%C3%A3o_de_armas_do_Brasil.svg.png',
};

function logoManual(orgao) {
  if (!orgao) return null;
  const nome = orgao.toLowerCase();
  for (const chave of Object.keys(LOGOS_CONHECIDOS)) {
    if (nome.includes(chave)) return LOGOS_CONHECIDOS[chave];
  }
  return null;
}

function corAvatar(nome) {
  const cores = [
    { bg: '#dbeafe', text: '#1e40af' },
    { bg: '#dcfce7', text: '#166534' },
    { bg: '#fef9c3', text: '#854d0e' },
    { bg: '#fce7f3', text: '#9d174d' },
    { bg: '#ede9fe', text: '#5b21b6' },
    { bg: '#ffedd5', text: '#9a3412' },
    { bg: '#cffafe', text: '#155e75' },
    { bg: '#f1f5f9', text: '#334155' },
  ];
  let hash = 0;
  for (let i = 0; i < (nome || '').length; i++) hash += nome.charCodeAt(i);
  return cores[hash % cores.length];
}

function iconeHtml(orgao, link) {
  const manual = logoManual(orgao);

  if (manual) {
    const cor = corAvatar(orgao);
    return `
      <img src="${manual}" alt="${orgao}" class="card-icone-img"
           onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
      <span class="card-icone-avatar" style="display:none; background:${cor.bg}; color:${cor.text};">
        ${siglaOrgao(orgao)}
      </span>`;
  }

  const cor = corAvatar(orgao);
  return `<span class="card-icone-avatar" style="background:${cor.bg}; color:${cor.text};">
    ${siglaOrgao(orgao)}
  </span>`;
}

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

function badgeClass(tipo) {
  if (tipo === 'aberto') return 'badge-aberto';
  if (tipo === 'previsto') return 'badge-breve';
  return 'badge-fechado';
}

function badgeTexto(tipo) {
  if (tipo === 'aberto') return 'Inscrições abertas';
  if (tipo === 'previsto') return 'Previsto';
  return 'Encerrado';
}

// ─── Filtros (accordion da sidebar) ──────────────────────────────────────────
function toggleFiltro(header) {
  const body = header.nextElementSibling;
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