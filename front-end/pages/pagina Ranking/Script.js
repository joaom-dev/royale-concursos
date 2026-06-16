const API_CONCURSOS = 'http://localhost:8080/api/concursos';
const API_RANKING   = 'http://localhost:8080/api/ranking';

const form                   = document.getElementById('userForm');
const selectContest          = document.getElementById('contest');
const loadingConcursos       = document.getElementById('loadingConcursos');
const erroMsg                = document.getElementById('erroMsg');
const rankingTableBody       = document.getElementById('rankingTableBody');
const top3Div                = document.getElementById('top3Users');
const fotoInput              = document.getElementById('foto');
const inputPesquisaConcurso  = document.getElementById('inputPesquisaConcurso');
const dropdownConcurso       = document.getElementById('dropdownConcurso');
const concursoEscolhido      = document.getElementById('concursoEscolhido');
const nomeConcursoEscolhido  = document.getElementById('nomeConcursoEscolhido');
const btnTrocarConcurso      = document.getElementById('btnTrocarConcurso');

let fotoBase64       = null;
let totalConcursos   = 0;
let listaConcursos   = [];
let concursoAtual    = null;

async function carregarConcursos() {
  loadingConcursos.style.display = 'inline';
  try {
    const res = await fetch(API_CONCURSOS);
    if (!res.ok) throw new Error();
    listaConcursos = await res.json();
    totalConcursos = listaConcursos.length;
    document.getElementById('valConcursos').textContent = totalConcursos;

    listaConcursos.forEach(c => {
      const opt = document.createElement('option');
      opt.value = c.id;
      opt.textContent = `${c.orgao} — ${(c.uf || '').toUpperCase()}`;
      selectContest.appendChild(opt);
    });
  } catch {
    inputPesquisaConcurso.placeholder = 'Erro ao carregar concursos';
    inputPesquisaConcurso.disabled = true;
  } finally {
    loadingConcursos.style.display = 'none';
  }
}

inputPesquisaConcurso.addEventListener('input', () => {
  const q = inputPesquisaConcurso.value.toLowerCase().trim();

  const filtrados = q.length === 0
    ? listaConcursos.slice(0, 30)
    : listaConcursos.filter(c =>
        (c.orgao || '').toLowerCase().includes(q) ||
        (c.uf    || '').toLowerCase().includes(q)
      ).slice(0, 40);

  if (filtrados.length === 0) {
    dropdownConcurso.innerHTML = '<div class="dropdown-vazio">Nenhum concurso encontrado</div>';
  } else {
    dropdownConcurso.innerHTML = filtrados.map(c => `
      <div class="dropdown-item-concurso" data-id="${c.id}" data-nome="${esc(c.orgao)}">
        <span class="dropdown-orgao">${esc(c.orgao || 'Não informado')}</span>
        <span class="dropdown-uf">${(c.uf || '').toUpperCase()}</span>
      </div>
    `).join('');

    dropdownConcurso.querySelectorAll('.dropdown-item-concurso').forEach(item => {
      item.addEventListener('click', () => selecionarConcurso(
        parseInt(item.dataset.id),
        item.dataset.nome
      ));
    });
  }

  dropdownConcurso.style.display = 'block';
});

inputPesquisaConcurso.addEventListener('focus', () => {
  if (listaConcursos.length > 0) inputPesquisaConcurso.dispatchEvent(new Event('input'));
});

document.addEventListener('click', (e) => {
  if (!e.target.closest('.pesquisa-concurso')) {
    dropdownConcurso.style.display = 'none';
  }
});

function selecionarConcurso(id, nome) {
  concursoAtual = { id, nome };

  selectContest.value = id;

  nomeConcursoEscolhido.textContent = nome;
  concursoEscolhido.style.display = 'flex';
  inputPesquisaConcurso.style.display = 'none';
  dropdownConcurso.style.display = 'none';

  carregarRanking(id, nome);
}

btnTrocarConcurso.addEventListener('click', () => {
  concursoAtual = null;
  selectContest.value = '';
  concursoEscolhido.style.display = 'none';
  inputPesquisaConcurso.style.display = 'block';
  inputPesquisaConcurso.value = '';
  inputPesquisaConcurso.focus();
  rankingTableBody.innerHTML = '';
  top3Div.innerHTML = '';
});

async function carregarRanking(concursoId, concursoNome) {
  try {
    const res = await fetch(`${API_RANKING}/${concursoId}`);
    if (!res.ok) throw new Error();
    const entries = await res.json();
    renderizarTabela(entries, concursoNome);
    renderizarTop3(entries);
    atualizarCards(entries);
  } catch {
    rankingTableBody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:20px;color:#aaa;">Erro ao carregar ranking.</td></tr>';
  }
}

fotoInput.addEventListener('change', (e) => {
  const file = e.target.files[0];
  if (!file) { fotoBase64 = null; return; }
  const reader = new FileReader();
  reader.onload = (ev) => { fotoBase64 = ev.target.result; };
  reader.readAsDataURL(file);
});

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  erroMsg.style.display = 'none';

  const concursoId   = selectContest.value;
  const concursoNome = concursoAtual?.nome || '';
  const nota         = parseFloat(parseFloat(form.score.value).toFixed(2));

  if (!concursoId) { mostrarErro('Selecione um concurso.'); return; }
  if (isNaN(nota)) { mostrarErro('Informe uma nota válida.'); return; }

  const btnSubmit = form.querySelector('button[type="submit"]');
  btnSubmit.disabled = true;
  btnSubmit.textContent = 'Salvando...';

  try {
    const res = await fetch(API_RANKING, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ concursoId: parseInt(concursoId), nota, fotoUrl: fotoBase64 || null })
    });

    if (res.status === 409) {
      const msg = await res.text();
      mostrarErro(msg || 'Você já está cadastrado neste concurso.');
      return;
    }

    if (res.status === 403) {
      const msg = await res.text();
      mostrarErro(msg || 'Plano FREE permite nota em apenas 1 concurso. Faça upgrade!');
      return;
    }

    if (!res.ok) throw new Error();

    form.score.value = '';
    fotoInput.value  = '';
    fotoBase64       = null;
    await carregarRanking(concursoId, concursoNome);

  } catch {
    mostrarErro('Erro ao salvar. Verifique sua conexão.');
  } finally {
    btnSubmit.disabled = false;
    btnSubmit.textContent = 'Adicionar ao Ranking';
  }
});

function renderizarTabela(entries, concursoNome) {
  if (entries.length === 0) {
    rankingTableBody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:20px;color:#aaa;">Nenhuma nota cadastrada ainda. Seja o primeiro!</td></tr>';
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
        <td>${u.cpfMascarado || ''}</td>
      </tr>`;
  }).join('');
}

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

carregarConcursos();