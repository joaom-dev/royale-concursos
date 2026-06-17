// ─── URLs do back-end ─────────────────────────────────────────────────────────
const API_CONCURSOS = 'http://localhost:8080/api/concursos';
const API_RANKING   = 'http://localhost:8080/api/ranking';

// ─── Elementos ────────────────────────────────────────────────────────────────
const form             = document.getElementById('userForm');
const selectContest    = document.getElementById('contest');
const loadingConcursos = document.getElementById('loadingConcursos');
const erroMsg          = document.getElementById('erroMsg');
const rankingTableBody = document.querySelector('#rankingTable tbody');
const top3Div          = document.getElementById('top3Users');
const fotoInput        = document.getElementById('foto');
const searchInput      = document.getElementById('searchConcurso');

let fotoBase64     = null;
let totalConcursos = 0;
let todasOpcoes    = []; // guarda todas as options para filtro de busca

// ─── Token JWT do localStorage ────────────────────────────────────────────────
function getToken() {
  return localStorage.getItem('token') || sessionStorage.getItem('token');
}

// ─── 1. Carregar concursos e popular o <select> ───────────────────────────────
async function carregarConcursos() {
  loadingConcursos.style.display = 'inline';
  try {
    const res = await fetch(API_CONCURSOS);
    if (!res.ok) throw new Error();
    const lista = await res.json();
    totalConcursos = lista.length;
    document.getElementById('valConcursos').textContent = totalConcursos;

    lista.forEach(c => {
      const opt = document.createElement('option');
      opt.value = c.id;
      opt.textContent = `${c.orgao} — ${(c.uf || '').toUpperCase()}`;
      selectContest.appendChild(opt);
      todasOpcoes.push({ value: c.id, text: opt.textContent });
    });
  } catch {
    const opt = document.createElement('option');
    opt.disabled = true;
    opt.textContent = 'Erro ao carregar concursos';
    selectContest.appendChild(opt);
  } finally {
    loadingConcursos.style.display = 'none';
  }
}

// ─── 2. Busca/filtro de concurso ──────────────────────────────────────────────
if (searchInput) {
  searchInput.addEventListener('input', () => {
    const termo = searchInput.value.toLowerCase().trim();

    // Remove todas as options exceto a primeira (placeholder)
    while (selectContest.options.length > 1) {
      selectContest.remove(1);
    }

    const filtrados = termo
      ? todasOpcoes.filter(o => o.text.toLowerCase().includes(termo))
      : todasOpcoes;

    filtrados.forEach(o => {
      const opt = document.createElement('option');
      opt.value = o.value;
      opt.textContent = o.text;
      selectContest.appendChild(opt);
    });

    document.getElementById('valConcursos').textContent = filtrados.length;
  });
}

// ─── 3. Carregar ranking do concurso selecionado ──────────────────────────────
async function carregarRanking(concursoId, concursoNome) {
  try {
    const res = await fetch(`${API_RANKING}/${concursoId}`);
    if (!res.ok) throw new Error();
    const entries = await res.json();
    renderizarTabela(entries, concursoNome);
    renderizarTop3(entries);
    atualizarCards(entries);
  } catch {
    rankingTableBody.innerHTML = '<tr><td colspan="4" style="text-align:center;padding:20px;color:#aaa;">Erro ao carregar ranking.</td></tr>';
  }
}

// ─── 4. Quando muda o select ──────────────────────────────────────────────────
selectContest.addEventListener('change', () => {
  const id   = selectContest.value;
  const nome = selectContest.options[selectContest.selectedIndex]?.text || '';
  if (id) carregarRanking(id, nome);
});

// ─── 5. Capturar foto em base64 ───────────────────────────────────────────────
fotoInput.addEventListener('change', (e) => {
  const file = e.target.files[0];
  if (!file) { fotoBase64 = null; return; }
  const reader = new FileReader();
  reader.onload = (ev) => { fotoBase64 = ev.target.result; };
  reader.readAsDataURL(file);
});

// ─── 6. Submeter nota ao back-end ─────────────────────────────────────────────
form.addEventListener('submit', async (e) => {
  e.preventDefault();
  erroMsg.style.display = 'none';

  const token = getToken();
  if (!token) {
    mostrarErro('Você precisa estar logado para participar do ranking.');
    return;
  }

  const concursoId   = selectContest.value;
  const concursoNome = selectContest.options[selectContest.selectedIndex]?.text || '';
  const nota         = parseFloat(parseFloat(form.score.value).toFixed(2));

  if (!concursoId) { mostrarErro('Selecione um concurso.'); return; }
  if (isNaN(nota)) { mostrarErro('Informe uma nota válida.'); return; }

  const btnSubmit = form.querySelector('button[type="submit"]');
  btnSubmit.disabled = true;
  btnSubmit.textContent = 'Salvando...';

  try {
    const res = await fetch(API_RANKING, {
      method:  'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        concursoId: parseInt(concursoId),
        nota,
        fotoUrl: fotoBase64 || null
      })
    });

    if (res.status === 403) {
      // Plano FREE esgotado
      const msg = await res.text();
      mostrarErro(msg);
      return;
    }

    if (res.status === 409) {
      // Já cadastrado neste concurso
      const msg = await res.text();
      mostrarErro(msg || 'Você já cadastrou uma nota neste concurso.');
      return;
    }

    if (!res.ok) throw new Error();

    form.reset();
    fotoBase64 = null;
    await carregarRanking(concursoId, concursoNome);

  } catch {
    mostrarErro('Erro ao salvar. Verifique sua conexão.');
  } finally {
    btnSubmit.disabled = false;
    btnSubmit.textContent = 'Adicionar ao Ranking';
  }
});

// ─── 7. Renderizar tabela ─────────────────────────────────────────────────────
function renderizarTabela(entries, concursoNome) {
  if (entries.length === 0) {
    rankingTableBody.innerHTML = '<tr><td colspan="4" style="text-align:center;padding:20px;color:#aaa;">Nenhuma nota cadastrada ainda. Seja o primeiro!</td></tr>';
    return;
  }

  rankingTableBody.innerHTML = entries.map((u, i) => {
    const pos      = i + 1;
    const medals   = { 1: '🏅', 2: '🥈', 3: '🥉' };
    const posClass = pos <= 3 ? `position-${pos}` : '';
    const medal    = medals[pos] || '';
    const avatarHtml = u.fotoUrl
      ? `<img class="avatar-img" src="${u.fotoUrl}" alt="${esc(u.nome)}">`
      : `<span class="avatar-circle">${iniciais(u.nome)}</span>`;

    return `
      <tr class="${posClass}">
        <td><span class="medal">${medal}</span> ${pos}º</td>
        <td>${avatarHtml} ${esc(u.nome)}</td>
        <td>${esc(concursoNome)}</td>
        <td class="score">${u.nota.toFixed(2)}</td>
      </tr>`;
  }).join('');
}

// ─── 8. Renderizar Top 3 ─────────────────────────────────────────────────────
function renderizarTop3(entries) {
  const top3   = entries.slice(0, 3);
  const medals = ['🏅', '🥈', '🥉'];

  if (top3.length === 0) {
    top3Div.innerHTML = '<p style="text-align:center;color:#aaa;padding:20px;">Nenhuma nota ainda.</p>';
    return;
  }

  const maxNota = entries[0].nota;

  top3Div.innerHTML = top3.map((u, i) => {
    const pct = maxNota > 0 ? ((u.nota / maxNota) * 100).toFixed(1) : '0.0';
    const avatarHtml = u.fotoUrl
      ? `<img class="avatar-large-img" src="${u.fotoUrl}" alt="${esc(u.nome)}">`
      : `<div class="avatar-large">${iniciais(u.nome)}</div>`;

    return `
      <div class="top-user">
        <span class="medal">${medals[i]}</span>
        ${avatarHtml}
        <p class="user-name">${esc(u.nome)}</p>
        <p class="user-score">${u.nota.toFixed(2)}</p>
        <div class="progress-bar">
          <div class="progress" style="width:${pct}%"></div>
        </div>
        <small>${pct}% do máximo</small>
      </div>`;
  }).join('');
}

// ─── 9. Atualizar cards de resumo ─────────────────────────────────────────────
function atualizarCards(entries) {
  document.getElementById('valTotal').textContent = entries.length;
  if (entries.length > 0) {
    document.getElementById('valMelhorNota').textContent = entries[0].nota.toFixed(2);
    document.getElementById('lblMelhorNota').textContent = entries[0].nome;
  } else {
    document.getElementById('valMelhorNota').textContent = '--';
    document.getElementById('lblMelhorNota').textContent = '--';
  }
}

// ─── Utilitários ──────────────────────────────────────────────────────────────
function iniciais(nome) {
  return (nome || '?').split(' ').slice(0, 2).map(w => w[0]).join('').toUpperCase();
}

function esc(s) {
  return (s || '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

function mostrarErro(msg) {
  erroMsg.textContent = msg;
  erroMsg.style.display = 'block';
}

// ─── Iniciar ──────────────────────────────────────────────────────────────────
carregarConcursos();