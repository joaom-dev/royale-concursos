/* 
    BANCO DE DADOS LOCAL
    Array com todos os concursos disponíveis na página.
    Cada objeto representa um concurso com suas informações.
    Para adicionar novos concursos, basta incluir um novo objeto aqui.
*/
const concursos = [
  { id:1,  nome:"Jovem Aprendiz CIEE – Pré-inscrição",          orgao:"CIEE",                area:"Alimentação, Magarefe, Comércio e Varejo", status:"open",  tipo:"federal",   icone:"🎓" },
  { id:2,  nome:"Câmara Municipal de Guarujá – SP",              orgao:"Câmara Guarujá",      area:"Legislativo Municipal",                    status:"open",  tipo:"municipal", icone:"🏛️" },
  { id:3,  nome:"Câmara Municipal de Cruzeiro/SP",               orgao:"Câmara Cruzeiro",     area:"Legislativo Municipal",                    status:"open",  tipo:"municipal", icone:"🏛️" },
  { id:4,  nome:"Justiça Federal no Rio Grande do Norte – JFRN", orgao:"JFRN",                area:"Poder Judiciário Federal",                 status:"open",  tipo:"federal",   icone:"⚖️" },
  { id:5,  nome:"São Paulo Previdência – SPPREV",                orgao:"SPPREV",              area:"Previdência Estadual",                     status:"open",  tipo:"estadual",  icone:"🏢" },
  { id:6,  nome:"CETESB – Companhia Ambiental do Estado de SP",  orgao:"CETESB",              area:"Meio Ambiente",                            status:"open",  tipo:"estadual",  icone:"🌿" },
  { id:7,  nome:"Tribunal de Justiça – TJ/MG",                   orgao:"TJ-MG",               area:"Poder Judiciário Estadual",                status:"soon",  tipo:"estadual",  icone:"⚖️" },
  { id:8,  nome:"Prefeitura de Campinas – SP",                   orgao:"Prefeitura Campinas", area:"Administração Pública",                    status:"open",  tipo:"municipal", icone:"🏙️" },
  { id:9,  nome:"IBGE – Instituto Brasileiro de Geografia",      orgao:"IBGE",                area:"Pesquisa e Estatística",                   status:"soon",  tipo:"federal",   icone:"📊" },
  { id:10, nome:"Polícia Civil – RS",                            orgao:"PC-RS",               area:"Segurança Pública",                        status:"open",  tipo:"estadual",  icone:"🚔" },
  { id:11, nome:"Câmara Municipal de Sorocaba – SP",             orgao:"Câmara Sorocaba",     area:"Legislativo Municipal",                    status:"open",  tipo:"municipal", icone:"🏛️" },
  { id:12, nome:"Receita Federal do Brasil",                     orgao:"RFB",                 area:"Administração Tributária",                 status:"soon",  tipo:"federal",   icone:"🧾" },
];

/* tag ativa no momento — começa em "all" para mostrar todos os concursos */
let tagAtiva = 'all';

/* retorna a classe CSS correta para colorir o badge conforme o status */
function badgeClass(status) {
  if (status === 'open') return 'badge-aberto';
  if (status === 'soon') return 'badge-breve';
  return 'badge-fechado';
}

/* retorna o texto legível do badge conforme o status */
function badgeTexto(status) {
  if (status === 'open') return 'Inscrições abertas';
  if (status === 'soon') return 'Encerra em breve';
  return 'Encerrado';
}

/* 
    FUNÇÃO PRINCIPAL
    Roda toda vez que o usuário digita na busca ou clica em filtro/tag.
    Ela filtra o array de concursos com base no que está ativo,
    depois reconstrói o HTML dos cards com os resultados.
    A página nunca recarrega — só o conteúdo do grid muda.
*/
function renderCards() {
  const busca = document.getElementById('search').value.toLowerCase();
  const grid  = document.getElementById('cards-grid');

  const filtrados = concursos.filter(c => {
    /* verifica se o concurso passa pelo filtro da tag ativa */
    const matchTag   = tagAtiva === 'all' || c.status === tagAtiva || c.tipo === tagAtiva;
    /* verifica se o nome ou órgão contém o texto digitado na busca */
    const matchBusca = c.nome.toLowerCase().includes(busca) || c.orgao.toLowerCase().includes(busca);
    return matchTag && matchBusca;
  });

  /* atualiza o contador de resultados no topo */
  document.getElementById('contagem-label').innerHTML =
    `Encontramos <strong>${filtrados.length}</strong> Processo${filtrados.length !== 1 ? 's' : ''} Público${filtrados.length !== 1 ? 's' : ''}`;

  /* se nenhum concurso passou nos filtros, exibe mensagem no lugar do grid */
  if (filtrados.length === 0) {
    grid.innerHTML = '<p class="sem-resultados">Nenhum concurso encontrado para sua pesquisa.</p>';
    return;
  }

  /* gera o HTML de cada card e injeta no grid de uma vez só */
  grid.innerHTML = filtrados.map(c => `
    <div class="card-concurso">
      <div class="card-topo">
        <div class="card-icone">${c.icone}</div>
        <div class="card-texto">
          <span class="card-badge ${badgeClass(c.status)}">${badgeTexto(c.status)}</span>
          <p class="card-nome">${c.nome}</p>
          <p class="card-area">${c.area}</p>
        </div>
      </div>
      <div class="card-divisor"></div>
      <div class="card-rodape">
        <button class="card-ver">Ver detalhes →</button>
      </div>
    </div>
  `).join('');
}

/* chamada pelos checkboxes da sidebar — por enquanto só re-renderiza os cards */
function aplicarFiltros() {
  renderCards();
}

/* 
    ABRIR/FECHAR GRUPOS DE FILTRO
    Recebe o header clicado, pega o elemento irmão (.filtro-body)
    e alterna a classe .aberto nele. O CSS usa essa classe para
    mostrar ou esconder as opções com display: flex / display: none.
    A seta também rotaciona via classe .aberto no CSS.
*/
function toggleFiltro(header) {
  const body    = header.nextElementSibling;
  const chevron = header.querySelector('.filtro-chevron');
  body.classList.toggle('aberto');
  chevron.classList.toggle('aberto');
}

/* 
    TAGS RÁPIDAS
    Ao clicar em uma tag, remove .ativo de todas as outras,
    coloca .ativo só na clicada, salva qual tag está ativa
    e re-renderiza os cards com o novo filtro.
*/
document.querySelectorAll('.tag-rapida').forEach(tag => {
  tag.addEventListener('click', () => {
    document.querySelectorAll('.tag-rapida').forEach(t => t.classList.remove('ativo'));
    tag.classList.add('ativo');
    tagAtiva = tag.dataset.tag;
    renderCards();
  });
});

/* 
    BUSCA EM TEMPO REAL
    Escuta qualquer digitação no input e re-renderiza os cards
    a cada letra, sem precisar apertar Enter.
*/
document.getElementById('search').addEventListener('input', renderCards);

/* 
    INICIALIZAÇÃO
    Chama renderCards() assim que a página carrega para
    preencher o grid com todos os concursos imediatamente.
*/
renderCards();