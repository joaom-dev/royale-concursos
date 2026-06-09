// ─── CONFIG ──────────────────────────────────────────────────────────────────
const API_BASE = 'http://localhost:8080/api/concursos';

// ─── Tabela de logos conhecidos ───────────────────────────────────────────────
const LOGOS_CONHECIDOS = {
    'ibge':             'https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/IBGE_logo.svg/320px-IBGE_logo.svg.png',
    'receita':          'https://upload.wikimedia.org/wikipedia/commons/thumb/5/54/Receita_Federal_do_Brasil_logo.svg/320px-Receita_Federal_do_Brasil_logo.svg.png',
    'ciee':             'https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/CIEE_logo.svg/320px-CIEE_logo.svg.png',
    'tj-mg':            'https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/Brasao_TJMG.svg/200px-Brasao_TJMG.svg.png',
    'tjmg':             'https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/Brasao_TJMG.svg/200px-Brasao_TJMG.svg.png',
    'justiça federal':  'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
    'jfrn':             'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
    'jfsp':             'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
    'trt':              'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
    'tse':              'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
    'stj':              'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
    'stf':              'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg/200px-Bras%C3%A3o_da_Justi%C3%A7a_Federal.svg.png',
    'polícia civil':    'https://upload.wikimedia.org/wikipedia/commons/thumb/4/forty/Coat_of_arms_of_Brazil.svg/200px-Coat_of_arms_of_Brazil.svg.png',
    'polícia militar':  'https://upload.wikimedia.org/wikipedia/commons/thumb/4/forty/Coat_of_arms_of_Brazil.svg/200px-Coat_of_arms_of_Brazil.svg.png',
    'prefeitura':       'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Bras%C3%A3o_de_armas_do_Brasil.svg/200px-Bras%C3%A3o_de_armas_do_Brasil.svg.png',
    'câmara':           'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Bras%C3%A3o_de_armas_do_Brasil.svg/200px-Bras%C3%A3o_de_armas_do_Brasil.svg.png',
    'governo':          'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Bras%C3%A3o_de_armas_do_Brasil.svg/200px-Bras%C3%A3o_de_armas_do_Brasil.svg.png',
    'cetesb':           'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Bras%C3%A3o_de_armas_do_Brasil.svg/200px-Bras%C3%A3o_de_armas_do_Brasil.svg.png',
    'spprev':           'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Bras%C3%A3o_de_armas_do_Brasil.svg/200px-Bras%C3%A3o_de_armas_do_Brasil.svg.png',
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

// ─── Inicialização ────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');

    if (!id) {
        mostrarErro();
        return;
    }

    carregarDetalhes(id);
});

// ─── Busca o concurso na API ──────────────────────────────────────────────────
async function carregarDetalhes(id) {
    try {
        let concurso = null;

        // Tenta primeiro GET /api/concursos/{id} (endpoint direto do Controller)
        try {
            const res = await fetch(`${API_BASE}/${id}`);
            if (res.ok) {
                concurso = await res.json();
            }
        } catch (_) {
            // endpoint individual indisponível, tenta fallback
        }

        // Fallback: busca lista completa e filtra pelo id
        if (!concurso) {
            const res = await fetch(API_BASE);
            if (!res.ok) throw new Error('Falha ao buscar concursos');
            const lista = await res.json();
            concurso = lista.find(c => String(c.id) === String(id));
        }

        if (!concurso) throw new Error('Concurso não encontrado');

        renderizarDetalhes(concurso);

    } catch (err) {
        console.error('Erro ao carregar detalhes:', err);
        mostrarErro();
    }
}

// ─── Renderização completa ────────────────────────────────────────────────────
function renderizarDetalhes(c) {

    // ── Breadcrumb ────────────────────────────────────────────────────────────
    document.getElementById('bc-nome').textContent = c.orgao || 'Detalhes';

    // ── Ícone / logo ──────────────────────────────────────────────────────────
    const iconeEl = document.getElementById('topo-icone');
    const logoUrl = logoManual(c.orgao);

    if (logoUrl) {
        const cor = corAvatar(c.orgao);
        iconeEl.innerHTML = `
            <img src="${logoUrl}" alt="${c.orgao}"
                 onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
            <span style="display:none; width:100%; height:100%; align-items:center; justify-content:center;
                         font-size:22px; font-weight:800; background:${cor.bg}; color:${cor.text}; border-radius:14px;">
                ${siglaOrgao(c.orgao)}
            </span>`;
    } else {
        const cor = corAvatar(c.orgao);
        iconeEl.style.background = cor.bg;
        iconeEl.style.color      = cor.text;
        iconeEl.textContent      = siglaOrgao(c.orgao);
    }

    // ── Título e subtítulo ────────────────────────────────────────────────────
    document.getElementById('topo-titulo').textContent = c.orgao || 'Sem título';
    document.getElementById('topo-areas').textContent  = c.situacao || c.cargo || '';

    // ── Meta informações ──────────────────────────────────────────────────────
    if (c.periodoInscricao) {
        document.getElementById('meta-periodo').innerHTML =
            `<i class="fa-regular fa-calendar"></i> Período: ${c.periodoInscricao}`;
    } else {
        document.getElementById('meta-periodo').style.display = 'none';
    }

    const ufTexto = c.estado ? `${c.estado} (${c.uf})` : (c.uf || '—');
    document.getElementById('meta-uf').innerHTML =
        `<i class="fa-solid fa-location-dot"></i> ${ufTexto}`;

    if (c.vagas) {
        document.getElementById('meta-vagas').innerHTML =
            `<i class="fa-solid fa-users"></i> ${c.vagas} vagas`;
    } else {
        document.getElementById('meta-vagas').style.display = 'none';
    }

    // ── Badge de status ───────────────────────────────────────────────────────
    const badge = document.getElementById('badge-status');
    if (c.tipo === 'aberto') {
        badge.className   = 'badge-status badge-aberto';
        badge.textContent = 'Inscrições abertas';
    } else if (c.tipo === 'previsto') {
        badge.className   = 'badge-status badge-previsto';
        badge.textContent = 'Previsto';
    } else {
        badge.className   = 'badge-status badge-fechado';
        badge.textContent = 'Encerrado';
    }

    // ── Botão INSCREVA-SE ─────────────────────────────────────────────────────
    const btnInscricao = document.getElementById('btn-inscricao');
    if (c.link && c.link.startsWith('http')) {
        btnInscricao.href = c.link;
        btnInscricao.style.opacity       = '1';
        btnInscricao.style.pointerEvents = 'auto';
    } else {
        btnInscricao.removeAttribute('href');
        btnInscricao.style.opacity       = '0.45';
        btnInscricao.style.pointerEvents = 'none';
        btnInscricao.title               = 'Link de inscrição não disponível';
    }

    // ── Botão Baixar Edital ───────────────────────────────────────────────────
    const btnEdital = document.getElementById('btn-edital');
    const urlEdital = c.editalUrl || null;
    if (urlEdital && urlEdital.startsWith('http')) {
        btnEdital.setAttribute('data-url', urlEdital);
        btnEdital.classList.remove('indisponivel');
        btnEdital.disabled = false;
        btnEdital.title    = '';
    } else {
        btnEdital.classList.add('indisponivel');
        btnEdital.disabled = true;
        btnEdital.title    = 'Edital não disponível';
    }

    // ── Banner de aviso ───────────────────────────────────────────────────────
    if (c.observacao) {
        document.getElementById('aviso-texto').textContent = c.observacao;
        document.getElementById('aviso-banner').style.display = 'flex';
    }

    // =========================================================================
    // ABA: DETALHES
    // =========================================================================

    // Grid de informações do processo
    const infoItems = [];
    if (c.tipo)            infoItems.push({ label: 'Status',           valor: tipoTexto(c.tipo) });
    if (c.vagas)           infoItems.push({ label: 'Vagas',            valor: c.vagas });
    if (c.uf)              infoItems.push({ label: 'Estado',           valor: ufTexto });
    if (c.nivel)           infoItems.push({ label: 'Nível',            valor: c.nivel });
    if (c.banca)           infoItems.push({ label: 'Banca',            valor: c.banca });
    if (c.cargo)           infoItems.push({ label: 'Cargo',            valor: c.cargo });
    if (c.salario)         infoItems.push({ label: 'Remuneração',      valor: 'R$ ' + c.salario });
    if (c.cargaHoraria)    infoItems.push({ label: 'Carga Horária',    valor: c.cargaHoraria });
    if (c.periodoInscricao)infoItems.push({ label: 'Período',          valor: c.periodoInscricao });

    const infoGrid = document.getElementById('info-grid');
    if (infoItems.length > 0) {
        infoGrid.innerHTML = infoItems.map(i => `
            <div class="info-item">
                <div class="info-label">${i.label}</div>
                <div class="info-valor">${i.valor}</div>
            </div>`).join('');
    } else {
        infoGrid.innerHTML = '<p class="nao-disponivel">Informações detalhadas não disponíveis.</p>';
    }

    // Descrição
    if (c.situacao) {
        document.getElementById('descricao-texto').textContent = c.situacao;
    } else {
        document.getElementById('secao-descricao').style.display = 'none';
    }

    // =========================================================================
    // ABA: REQUISITOS
    // =========================================================================

    const listaRequisitos = document.getElementById('requisitos-lista');
    if (c.requisitos && c.requisitos.trim()) {
        // Divide por \n ou ponto-e-vírgula para gerar os itens da lista
        const itens = c.requisitos
            .split(/\n|;/)
            .map(r => r.replace(/^[-•]\s*/, '').trim())
            .filter(Boolean);

        listaRequisitos.innerHTML = itens.map(r => `<li>${r}</li>`).join('');
    } else {
        listaRequisitos.innerHTML = '<li class="nao-disponivel">Requisitos não informados para este concurso.</li>';
    }

    // Nível / escolaridade como destaque
    if (c.nivel) {
        const secaoCursos = document.getElementById('secao-cursos');
        secaoCursos.style.display = '';
        document.getElementById('cursos-grid').innerHTML =
            `<div class="curso-item">${c.nivel}</div>`;
    }

    // =========================================================================
    // ABA: BENEFÍCIOS
    // =========================================================================

    const benGrid = document.getElementById('beneficios-grid');

    // Salário como card de destaque
    if (c.salario) {
        document.getElementById('ben-salario').style.display  = 'flex';
        document.getElementById('ben-salario-valor').textContent = 'R$ ' + c.salario;
    } else {
        document.getElementById('ben-salario').style.display = 'none';
    }

    // Benefícios em texto — divide por \n ou ponto-e-vírgula
    const iconesBeneficios = {
        'vale-transporte':    'fa-bus',
        'assistência médica': 'fa-heart-pulse',
        'vale-refeição':      'fa-utensils',
        'restaurante':        'fa-store',
        'plano de saúde':     'fa-stethoscope',
        'seguro de vida':     'fa-shield',
        'auxílio':            'fa-hand-holding-dollar',
        'férias':             'fa-umbrella-beach',
        '13':                 'fa-gift',
    };

    if (c.beneficios && c.beneficios.trim()) {
        const itens = c.beneficios
            .split(/\n|;/)
            .map(b => b.replace(/^[-•]\s*/, '').trim())
            .filter(Boolean);

        itens.forEach(b => {
            const chave = Object.keys(iconesBeneficios)
                .find(k => b.toLowerCase().includes(k));
            const icone = chave ? iconesBeneficios[chave] : 'fa-gift';
            benGrid.insertAdjacentHTML('beforeend', `
                <div class="beneficio-card">
                    <i class="fa-solid ${icone}"></i>
                    <div>
                        <span class="ben-label">Benefício</span>
                        <span class="ben-valor">${b}</span>
                    </div>
                </div>`);
        });
    } else if (!c.salario) {
        // Nenhum benefício nem salário
        document.getElementById('beneficios-extras').innerHTML =
            '<p class="nao-disponivel">Benefícios não informados para este concurso.</p>';
    }

    // Carga horária como seção separada
    if (c.cargaHoraria) {
        document.getElementById('secao-carga').style.display = '';
        document.getElementById('carga-texto').textContent   = c.cargaHoraria;
    }

    // ── Exibe a página ────────────────────────────────────────────────────────
    document.getElementById('loading-overlay').style.display = 'none';
    document.getElementById('detalhes-page').style.display   = 'block';
    document.title = `${c.orgao || 'Detalhes'} – Royale Concursos`;
}

// ─── Trocar aba ───────────────────────────────────────────────────────────────
function trocarAba(btn) {
    document.querySelectorAll('.aba').forEach(a => a.classList.remove('ativo'));
    document.querySelectorAll('.painel').forEach(p => p.classList.remove('ativo'));
    btn.classList.add('ativo');
    document.getElementById('painel-' + btn.dataset.aba).classList.add('ativo');
}

// ─── Baixar edital ────────────────────────────────────────────────────────────
function baixarEdital() {
    const btn = document.getElementById('btn-edital');
    const url = btn.getAttribute('data-url');
    if (url) window.open(url, '_blank', 'noopener');
}

// ─── Mostrar erro ─────────────────────────────────────────────────────────────
function mostrarErro() {
    document.getElementById('loading-overlay').style.display = 'none';
    document.getElementById('erro-container').style.display  = 'flex';
}

// ─── Helper ───────────────────────────────────────────────────────────────────
function tipoTexto(tipo) {
    if (tipo === 'aberto')   return 'Inscrições abertas';
    if (tipo === 'previsto') return 'Previsto';
    return 'Encerrado';
}