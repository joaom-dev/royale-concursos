const API_URL = "http://localhost:8080";

function getToken() { return localStorage.getItem("token"); }
function getHeaders() {
    return { "Content-Type": "application/json", "Authorization": `Bearer ${getToken()}` };
}

// ─── Abre/fecha modais ────────────────────────────────────────────────────────
const modal  = document.getElementById('modal-mensal');
const modal2 = document.getElementById('modal-vita');

document.getElementById('openModal').addEventListener('click', () => {
    if (!getToken()) { window.location.href = "/front-end/pages/tela de login/index.html"; return; }
    modal.showModal();
    document.body.classList.add('modal-open');
    gerarQrPix('mensal');
});

document.getElementById('openModal2').addEventListener('click', () => {
    if (!getToken()) { window.location.href = "/front-end/pages/tela de login/index.html"; return; }
    modal2.showModal();
    document.body.classList.add('modal-open');
    gerarQrPix('vita');
});

document.getElementById('closeModalMensal').addEventListener('click', () => {
    modal.close(); document.body.classList.remove('modal-open');
});
document.getElementById('closeModalVita').addEventListener('click', () => {
    modal2.close(); document.body.classList.remove('modal-open');
});

// ─── Seleção de método ────────────────────────────────────────────────────────
function selecionarMetodo(el, seletor, modalId) {
    el.closest(seletor).querySelectorAll('.metodo-mensal, .metodo-vita')
      .forEach(btn => btn.classList.remove('ativo'));
    el.classList.add('ativo');
    const metodo = el.dataset.metodo;
    ['pix','credito','debito'].forEach(s => {
        const sec = document.getElementById(`${modalId}-${s}`);
        if (sec) sec.style.display = 'none';
    });
    const alvo = document.getElementById(`${modalId}-${metodo}`);
    if (alvo) alvo.style.display = 'block';
}

// ─── Copiar chave PIX ─────────────────────────────────────────────────────────
function copiarChave(btn) {
    const chave = btn.closest('.pix-chave-box').querySelector('.pix-chave-texto').textContent;
    navigator.clipboard.writeText(chave).then(() => {
        const orig = btn.innerHTML;
        btn.innerHTML = '<i class="fa-solid fa-check"></i> Copiado!';
        btn.disabled = true;
        setTimeout(() => { btn.innerHTML = orig; btn.disabled = false; }, 2000);
    });
}

// ─── Buscar preços do backend e exibir ───────────────────────────────────────
async function carregarPrecos() {
    try {
        const res = await fetch(`${API_URL}/api/pagamentos/precos`, { headers: getHeaders() });
        if (!res.ok) return;
        const precos = await res.json();

        // Atualiza exibição dos preços nas telas (somente visual)
        const mensal = precos['MENSAL'];
        const vita   = precos['VITALICIO'];

        if (mensal) {
            document.querySelectorAll('.preco-mensal').forEach(el => {
                el.textContent = `R$${parseFloat(mensal).toFixed(2).replace('.', ',')}`;
            });
        }
        if (vita) {
            document.querySelectorAll('.preco-vita').forEach(el => {
                el.textContent = `R$${parseFloat(vita).toFixed(2).replace('.', ',')}`;
            });
        }
    } catch { /* silencioso */ }
}

// Guarda o id do pagamento PIX pendente por modal
const pixPagamentoId = { mensal: null, vita: null };

// ─── Gerar QR Code PIX — frontend NÃO manda valor ────────────────────────────
async function gerarQrPix(modalId) {
    const tipoPlano = modalId === 'mensal' ? 'MENSAL' : 'VITALICIO';
    const wrapper   = document.getElementById(`qr-${modalId}`);
    if (!wrapper) return;

    wrapper.innerHTML = '<p style="color:#aaa;font-size:13px;text-align:center;">Gerando QR Code...</p>';

    try {
        const res = await fetch(`${API_URL}/api/pagamentos`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify({
                // ⚠️ Sem campo "valor" — o backend define o preço
                metodoPagamento: 'PIX',
                tipoPlano: tipoPlano
            })
        });

        if (!res.ok) {
            const err = await res.json();
            wrapper.innerHTML = `<p style="color:#ff6b6b;font-size:13px;">${err.message || 'Erro ao gerar QR Code.'}</p>`;
            return;
        }

        const pagamento = await res.json();

        pixPagamentoId[modalId] = pagamento.id;
        if (pagamento.pixPayload) {
            document.getElementById(`pix-payload-${modalId}`).textContent = pagamento.pixPayload;
            wrapper.innerHTML = '';
            new QRCode(wrapper, {
                text: pagamento.pixPayload,
                width: 180,
                height: 180,
                colorDark: '#000000',
                colorLight: '#ffffff',
                correctLevel: QRCode.CorrectLevel.M
            });
        }
    } catch {
        wrapper.innerHTML = '<p style="color:#ff6b6b;font-size:13px;">Erro ao gerar QR Code. Tente novamente.</p>';
    }
}

// ─── Copiar payload PIX completo ──────────────────────────────────────────────
function copiarPayloadPix(modalId) {
    const payload = document.getElementById(`pix-payload-${modalId}`)?.textContent;
    if (!payload) return;
    navigator.clipboard.writeText(payload).then(() => {
        const btn = document.getElementById(`btn-copiar-payload-${modalId}`);
        if (!btn) return;
        const orig = btn.innerHTML;
        btn.innerHTML = '<i class="fa-solid fa-check"></i> Copiado!';
        btn.disabled = true;
        setTimeout(() => { btn.innerHTML = orig; btn.disabled = false; }, 2000);
    });
}

// ─── Pagar com cartão — frontend NÃO manda valor ─────────────────────────────
async function pagarCartao(modalId, tipoCartao = 'CARTAO_CREDITO') {
    const tipoPlano  = modalId === 'mensal' ? 'MENSAL' : 'VITALICIO';
    const sufixo     = tipoCartao === 'CARTAO_DEBITO' ? '-deb' : '';
    const btnId      = tipoCartao === 'CARTAO_DEBITO' ? `btn-pagar-${modalId}-deb` : `btn-pagar-${modalId}`;

    const num  = document.getElementById(`${modalId}-num-cartao${sufixo}`).value.trim();
    const nome = document.getElementById(`${modalId}-nome-cartao${sufixo}`).value.trim();
    const val  = document.getElementById(`${modalId}-validade${sufixo}`).value.trim();
    const cvv  = document.getElementById(`${modalId}-cvv${sufixo}`).value.trim();

    // Validação básica no frontend (segurança real está no backend)
    if (!num || !nome || !val || !cvv) { mostrarErroModal(modalId, 'Preencha todos os campos do cartão.'); return; }
    if (!/^\d{2}\/\d{2}$/.test(val))  { mostrarErroModal(modalId, 'Validade deve estar no formato MM/AA.'); return; }
    const [mes, ano] = val.split('/');
    if (new Date(2000 + parseInt(ano), parseInt(mes) - 1) < new Date()) {
        mostrarErroModal(modalId, 'Cartão vencido.'); return;
    }
    if (!/^\d{3,4}$/.test(cvv)) { mostrarErroModal(modalId, 'CVV inválido.'); return; }

    const btn = document.getElementById(btnId);
    btn.disabled = true;
    btn.textContent = 'Processando...';

    try {
        const res = await fetch(`${API_URL}/api/pagamentos`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify({
                // ⚠️ Sem campo "valor" — o backend define o preço
                metodoPagamento: tipoCartao,
                tipoPlano,
                numeroCartao:    num.replace(/\s/g, ''),
                validadeCartao:  val,
                cvv,
                nomeTitular:     nome
            })
        });

        const pagamento = await res.json();

        if (!res.ok) {
            mostrarErroModal(modalId, pagamento.message || 'Pagamento recusado. Verifique os dados.');
            return;
        }

        if (pagamento.status === 'APROVADO') {
            mostrarSucessoModal(modalId, '✅ Pagamento aprovado! Seu plano foi ativado.');
            await carregarBadgePlano();
            setTimeout(() => {
                modalId === 'mensal' ? modal.close() : modal2.close();
                document.body.classList.remove('modal-open');
            }, 2500);
        } else {
            mostrarErroModal(modalId, 'Pagamento não aprovado. Tente novamente.');
        }
    } catch {
        mostrarErroModal(modalId, 'Erro de conexão. Verifique sua internet.');
    } finally {
        btn.disabled = false;
        btn.innerHTML = `<i class="fa-solid fa-lock"></i> Pagar`;
    }
}

function pagarCartaoDebito(modalId) {
    return pagarCartao(modalId, 'CARTAO_DEBITO');
}

// ─── Confirmar PIX manual ─────────────────────────────────────────────────────
async function confirmarPix(modalId) {
    const id = pixPagamentoId[modalId];
    if (!id) {
        mostrarErroModal(modalId, 'Erro: pagamento não encontrado. Tente reabrir o modal.');
        return;
    }

    const btn = document.querySelector(`#${modalId === 'mensal' ? 'modal-mensal' : 'modal-vita'} .btn-pagar`);
    if (btn) { btn.disabled = true; btn.textContent = 'Confirmando...'; }

    try {
        const res = await fetch(`${API_URL}/api/pagamentos/${id}/confirmar-pix`, {
            method: 'POST',
            headers: getHeaders()
        });

        const data = await res.json();

        if (!res.ok) {
            mostrarErroModal(modalId, data.message || 'Erro ao confirmar pagamento.');
            return;
        }

        mostrarSucessoModal(modalId, '✅ Pagamento confirmado! Seu plano foi ativado.');
        await carregarBadgePlano();
        setTimeout(() => {
            modalId === 'mensal' ? modal.close() : modal2.close();
            document.body.classList.remove('modal-open');
        }, 2500);

    } catch {
        mostrarErroModal(modalId, 'Erro de conexão. Tente novamente.');
    } finally {
        if (btn) { btn.disabled = false; btn.innerHTML = '<i class="fa-solid fa-check"></i> Já realizei o pagamento'; }
    }
}

// ─── Feedback nos modais ──────────────────────────────────────────────────────
function mostrarErroModal(modalId, msg) {
    const el = document.getElementById(`msg-${modalId}`);
    if (!el) return;
    el.textContent = msg;
    el.style.color = '#ff6b6b';
    el.style.display = 'block';
    setTimeout(() => el.style.display = 'none', 5000);
}

function mostrarSucessoModal(modalId, msg) {
    const el = document.getElementById(`msg-${modalId}`);
    if (!el) return;
    el.textContent = msg;
    el.style.color = '#4ade80';
    el.style.display = 'block';
}

// ─── Badge do plano atual ─────────────────────────────────────────────────────
async function carregarBadgePlano() {
    const token = getToken();
    if (!token) return;
    try {
        const res = await fetch(`${API_URL}/api/planos/funcionalidades`, { headers: getHeaders() });
        if (!res.ok) return;
        const f = await res.json();

        const badge = document.getElementById('badge-plano-atual');
        if (!badge) return;

        const labels = { FREE: 'Plano FREE', MENSAL: 'Plano Mensal', VITALICIO: 'Plano Vitalício' };
        const colors = { FREE: '#888', MENSAL: '#293ab4', VITALICIO: '#f59e0b' };

        badge.textContent = labels[f.plano] || f.plano;
        badge.style.background = colors[f.plano] || '#888';
        badge.style.display = 'inline-block';

        const expEl = document.getElementById('plano-expiracao');
        if (expEl && f.planoExpiraEm) {
            const data = new Date(f.planoExpiraEm);
            expEl.textContent = `Renova em ${data.toLocaleDateString('pt-BR')}`;
            expEl.style.display = 'block';
        }

        document.querySelectorAll('.plan-card').forEach(c => c.classList.remove('plano-ativo'));
        if (f.plano === 'MENSAL')    document.querySelector('.plan-card.light')?.classList.add('plano-ativo');
        if (f.plano === 'VITALICIO') document.querySelector('.plan-card.dark')?.classList.add('plano-ativo');

    } catch { /* silencioso */ }
}

// ─── Formatar campos de cartão ────────────────────────────────────────────────
function formatarCartao(input) {
    let v = input.value.replace(/\D/g, '').substring(0, 16);
    input.value = v.replace(/(\d{4})(?=\d)/g, '$1 ');
}

function formatarValidade(input) {
    let v = input.value.replace(/\D/g, '').substring(0, 4);
    if (v.length >= 3) v = v.substring(0, 2) + '/' + v.substring(2);
    input.value = v;
}

// ─── Inicializar ──────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    ['mensal', 'vita'].forEach(id => {
        document.getElementById(`${id}-credito`).style.display = 'none';
        document.getElementById(`${id}-debito`).style.display  = 'none';
        document.getElementById(`${id}-pix`).style.display     = 'block';
    });

    document.querySelectorAll('.input-num-cartao').forEach(el => {
        el.addEventListener('input', () => formatarCartao(el));
    });
    document.querySelectorAll('.input-validade').forEach(el => {
        el.addEventListener('input', () => formatarValidade(el));
    });

    carregarBadgePlano();
    carregarPrecos(); // busca preços do backend para exibição
});