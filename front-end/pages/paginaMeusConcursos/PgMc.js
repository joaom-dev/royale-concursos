// ─── Lê concursos salvos do localStorage ──────────────────────────────────────
const STORAGE_KEY = 'royale_salvos';

function getConcursosSalvos() {
    try { return JSON.parse(localStorage.getItem(STORAGE_KEY)) || []; }
    catch { return []; }
}

// Normaliza campos da API para o formato esperado pelo card
function normalizarConcurso(c) {
    return {
        id:     c.id,
        nome:   c.orgao   || c.nome   || 'Concurso',
        orgao:  c.orgao   || '',
        area:   c.situacao || c.cargo || c.area || '',
        status: c.tipo === 'aberto'   ? 'open'
               : c.tipo === 'previsto' ? 'soon'
               : c.status || 'fechado',
        tipo:   c.tipo    || '',
        imagem: c.imagem  || null,
        imgAlt: c.orgao   || '',
        // guarda campos originais para a tela de detalhes
        _original: c,
    };
}

const concursos = getConcursosSalvos().map(normalizarConcurso);



// Serve para todos os concursos ficarem com a tag ALL para aparecerem no site
let tagAtiva = 'all';

// Puxa a estilizacao certa do CSS para cada status diferente
function badgeClass(status) {
  if (status === 'open') return 'badge-aberto';
  if (status === 'soon') return 'badge-breve';
  return 'badge-fechado';
}

// Retorna qual texto ira aparecer com base no status
function badgeTexto(status) {
  if (status === 'open') return 'Inscrições abertas';
  if (status === 'soon') return 'Encerra em breve';
  return 'Encerrado';
}

// Roda toda vez que o usuário digita na busca ou clica em filtro/tag.Ela filtra o array de concursos com base no que está ativo,depois reconstrói o HTML dos cards com os resultados.A página nunca recarrega — só o conteúdo do grid muda.
function renderCards() {
  const busca = document.getElementById('search').value.toLowerCase();
  const grid  = document.getElementById('cards-grid');

  const filtrados = concursos.filter(c => {
    //verifica se o concurso passa pelo filtro da tag ativa 
    const matchTag   = tagAtiva === 'all' || c.status === tagAtiva || c.tipo === tagAtiva;
    //verifica se o nome ou órgão contém o texto digitado na busca 
    const matchBusca = c.nome.toLowerCase().includes(busca) || c.orgao.toLowerCase().includes(busca);
    return matchTag && matchBusca;
  });

  // Serve para atualizar o contador de concursos
  document.getElementById('contagem-label').innerHTML =
    `Encontramos <strong>${filtrados.length}</strong> Processo${filtrados.length !== 1 ? 's' : ''} Público${filtrados.length !== 1 ? 's' : ''}`;

  // Se nenhum concurso passou nos filtros, exibe mensagem no lugar do grid 
  if (filtrados.length === 0) {
    const ehVazio = concursos.length === 0;
    grid.innerHTML = ehVazio
        ? `<div class="sem-resultados">
                <i class="fa-regular fa-bookmark" style="font-size:48px;color:#ccc;display:block;margin-bottom:16px;"></i>
                <p>Você ainda não salvou nenhum concurso.</p>
                <a href="../tela de concursos/index.html" style="margin-top:16px;display:inline-block;background:#293ab4;color:#fff;padding:10px 22px;border-radius:10px;text-decoration:none;font-weight:700;font-size:14px;">Explorar concursos →</a>
           </div>`
        : '<p class="sem-resultados">Nenhum concurso encontrado para sua pesquisa.</p>';
    return;
  }

  // Gera o HTML de cada card e injeta no grid de uma vez só 
  grid.innerHTML = filtrados.map(c => `
    <div class="card-concurso">
      <div class="card-topo">
        <div class="card-icone" id="icone-${c.id}">
          ${c.imagem
            ? `<img src="${c.imagem}" alt="${c.imgAlt}" onerror="this.style.display='none';document.getElementById('icone-${c.id}').dataset.fallback='1';">`
            : ''}
        </div>
        <div class="card-texto">
          <span class="card-badge ${badgeClass(c.status)}">${badgeTexto(c.status)}</span>
          <p class="card-nome">${c.nome}</p>
          <p class="card-area">${c.area}</p>
        </div>
      </div>
      <div class="card-divisor"></div>
      <div class="card-rodape">
        <button class="card-remover" onclick="removerSalvo('${c.id}')" title="Remover dos salvos">
          <i class="fa-regular fa-bookmark"></i>
        </button>
        <a href="../detalhes-concurso/index.html?id=${c.id}" class="card-ver">Ver detalhes →</a>
      </div>
    </div>
  `).join('');
}


function aplicarFiltros() {
  renderCards();
}

// Sistema dos filtros
function toggleFiltro(header) {
  const body    = header.nextElementSibling;
  const chevron = header.querySelector('.filtro-chevron');
  body.classList.toggle('aberto');
  chevron.classList.toggle('aberto');
}

// Sistema das tags rapidas 
document.querySelectorAll('.tag-rapida').forEach(tag => {
  tag.addEventListener('click', () => {
    document.querySelectorAll('.tag-rapida').forEach(t => t.classList.remove('ativo'));
    tag.classList.add('ativo');
    tagAtiva = tag.dataset.tag;
    renderCards();
  });
});

// Pega qualquer digitacao do teclado e mostra os cards com letras correspondentes
document.getElementById('search').addEventListener('input', renderCards);

// Remove um concurso dos salvos
function removerSalvo(id) {
    let salvos = [];
    try { salvos = JSON.parse(localStorage.getItem(STORAGE_KEY)) || []; } catch {}
    salvos = salvos.filter(c => String(c.id) !== String(id));
    localStorage.setItem(STORAGE_KEY, JSON.stringify(salvos));
    // Atualiza o array e re-renderiza
    concursos.length = 0;
    getConcursosSalvos().map(normalizarConcurso).forEach(c => concursos.push(c));
    renderCards();
}

// Assim que a tela carrega mostra todos os concursos
renderCards();