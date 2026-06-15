// ABRIR TELA DO PAGAMENTO

const modal = document.getElementById('modal-mensal')
const modal2 = document.getElementById('modal-vita')

openModal.addEventListener('click', () => {
    modal.showModal()
    document.body.classList.add('modal-open')
})

openModal2.addEventListener('click', () => {
    modal2.showModal()
    document.body.classList.add('modal-open')
})

// FECHAR TELA DO PAGAMENTO

document.getElementById('closeModalMensal').addEventListener('click', () => {
    modal.close()
    document.body.classList.remove('modal-open')
})

document.getElementById('closeModalVita').addEventListener('click', () => {
    modal2.close()
    document.body.classList.remove('modal-open')
})

// SELECIONAR MÉTODO DE PAGAMENTO

function selecionarMetodo(el, seletor, modalId) {
    // Marca botão ativo
    const container = el.closest(seletor)
    container.querySelectorAll('.metodo-mensal, .metodo-vita').forEach(btn => btn.classList.remove('ativo'))
    el.classList.add('ativo')

    // Pega o método selecionado
    const metodo = el.dataset.metodo // 'pix', 'credito' ou 'debito'

    // Esconde todas as seções de conteúdo do modal
    const secoes = ['pix', 'credito', 'debito']
    secoes.forEach(s => {
        const el = document.getElementById(`${modalId}-${s}`)
        if (el) el.style.display = 'none'
    })

    // Mostra só a seção correta
    const alvo = document.getElementById(`${modalId}-${metodo}`)
    if (alvo) alvo.style.display = 'block'
}

// COPIAR CHAVE PIX

function copiarChave(btn) {
    const chave = btn.closest('.pix-chave-box').querySelector('.pix-chave-texto').textContent
    navigator.clipboard.writeText(chave).then(() => {
        const original = btn.innerHTML
        btn.innerHTML = '<i class="fa-solid fa-check"></i> Copiado!'
        btn.disabled = true
        setTimeout(() => {
            btn.innerHTML = original
            btn.disabled = false
        }, 2000)
    })
}

// INICIALIZAR — esconde crédito/débito, mostra PIX por padrão

document.addEventListener('DOMContentLoaded', () => {
    ['mensal', 'vita'].forEach(modalId => {
        document.getElementById(`${modalId}-credito`).style.display = 'none'
        document.getElementById(`${modalId}-debito`).style.display = 'none'
        document.getElementById(`${modalId}-pix`).style.display = 'block'
    })
})

const API_URL = "http://localhost:8080";

function getToken() {
    return localStorage.getItem("token");
}

function getHeaders() {
    return {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${getToken()}`
    };
}

async function tratarErro(response) {
    const data = await response.json();
    throw new Error(data.message || "Erro desconhecido");
}

async function login(email, senha) {
    try {
        const response = await fetch(`${API_URL}/api/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, senha })
        });

        if (!response.ok) await tratarErro(response);

        const data = await response.json();
        localStorage.setItem("token", data.token);
        localStorage.setItem("usuario", JSON.stringify({
            nome: data.nome,
            email: data.email,
            role: data.role
        }));

        return data;
    } catch (erro) {
        exibirMensagem("erro", erro.message);
        throw erro;
    }
}

async function registrar(nome, email, senha) {
    try {
        const response = await fetch(`${API_URL}/api/auth/registrar`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ nome, email, senha })
        });

        if (!response.ok) await tratarErro(response);

        const data = await response.json();
        localStorage.setItem("token", data.token);
        localStorage.setItem("usuario", JSON.stringify({
            nome: data.nome,
            email: data.email,
            role: data.role
        }));

        return data;
    } catch (erro) {
        exibirMensagem("erro", erro.message);
        throw erro;
    }
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("usuario");
    localStorage.removeItem("funcionalidades");
    window.location.href = "/login.html";
}

async function buscarFuncionalidades() {
    try {
        const response = await fetch(`${API_URL}/api/planos/funcionalidades`, {
            headers: getHeaders()
        });

        if (!response.ok) await tratarErro(response);

        const funcionalidades = await response.json();
        localStorage.setItem("funcionalidades", JSON.stringify(funcionalidades));

        return funcionalidades;
    } catch (erro) {
        console.error("Erro ao buscar funcionalidades:", erro);
        throw erro;
    }
}

async function aplicarPlano() {
    const funcionalidades = await buscarFuncionalidades();

    const anuncios = document.querySelectorAll(".anuncio");
    anuncios.forEach(el => {
        el.style.display = funcionalidades.exibirAnuncios ? "block" : "none";
    });

    const aviso = document.getElementById("ranking-aviso");
    if (!funcionalidades.rankingCompleto && aviso) {
        aviso.style.display = "block";
        aviso.innerHTML = `Você está vendo apenas o top ${funcionalidades.limiteRanking}.
            <a href="/planos.html">Faça upgrade para VITALÍCIO</a> e veja o ranking completo!`;
    }

    const badge = document.getElementById("badge-plano");
    if (badge) {
        badge.textContent = funcionalidades.plano;
        badge.className = `badge-plano ${funcionalidades.plano.toLowerCase()}`;
    }

    const expiracao = document.getElementById("plano-expiracao");
    if (expiracao && funcionalidades.planoExpiraEm) {
        const data = new Date(funcionalidades.planoExpiraEm);
        expiracao.textContent = `Seu plano expira em ${data.toLocaleDateString("pt-BR")}`;
    }

    return funcionalidades;
}

function validarCartaoFrontend(numero, validade, cvv) {
    const erros = [];
    const numeroLimpo = numero.replace(/[\s-]/g, "");

    if (!/^\d{13,19}$/.test(numeroLimpo)) erros.push("Número do cartão inválido");

    if (!/^\d{2}\/\d{2}$/.test(validade)) {
        erros.push("Validade deve estar no formato MM/AA");
    } else {
        const [mes, ano] = validade.split("/");
        const expiracao = new Date(2000 + parseInt(ano), parseInt(mes) - 1);
        if (expiracao < new Date()) erros.push("Cartão vencido");
    }

    if (!/^\d{3,4}$/.test(cvv)) erros.push("CVV inválido");

    return erros;
}

async function pagarComCartao(dados) {
    const erros = validarCartaoFrontend(dados.numero, dados.validade, dados.cvv);
    if (erros.length > 0) {
        exibirMensagem("erro", erros.join(", "));
        return null;
    }

    try {
        exibirCarregando(true);

        const response = await fetch(`${API_URL}/api/pagamentos`, {
            method: "POST",
            headers: getHeaders(),
            body: JSON.stringify({
                valor: dados.valor,
                metodoPagamento: dados.tipo,
                descricao: dados.descricao || `Pagamento plano ${dados.tipoPlano}`,
                numeroCartao: dados.numero,
                validadeCartao: dados.validade,
                cvv: dados.cvv,
                tipoPlano: dados.tipoPlano || null
            })
        });

        if (!response.ok) await tratarErro(response);

        const pagamento = await response.json();

        if (pagamento.status === "APROVADO") {
            exibirMensagem("sucesso", "Pagamento aprovado! Seu plano foi ativado.");
            await aplicarPlano();
        } else {
            exibirMensagem("erro", "Pagamento recusado. Verifique os dados do cartão.");
        }

        return pagamento;
    } catch (erro) {
        exibirMensagem("erro", erro.message);
        return null;
    } finally {
        exibirCarregando(false);
    }
}

async function pagarComPix(valor, tipoPlano) {
    try {
        exibirCarregando(true);

        const response = await fetch(`${API_URL}/api/pagamentos`, {
            method: "POST",
            headers: getHeaders(),
            body: JSON.stringify({
                valor: valor,
                metodoPagamento: "PIX",
                descricao: `Plano ${tipoPlano} - Royale Concursos`,
                tipoPlano: tipoPlano
            })
        });

        if (!response.ok) await tratarErro(response);

        const pagamento = await response.json();

        if (pagamento.pixPayload) {
            exibirQRCode(pagamento.pixPayload);
            exibirCopiaCola(pagamento.pixPayload);
        }

        return pagamento;
    } catch (erro) {
        exibirMensagem("erro", erro.message);
        return null;
    } finally {
        exibirCarregando(false);
    }
}

function exibirQRCode(pixPayload) {
    const container = document.getElementById("qrcode-container");
    if (!container) return;

    container.innerHTML = "";

    if (typeof QRCode !== "undefined") {
        new QRCode(container, { text: pixPayload, width: 200, height: 200 });
    }

    const secaoPix = document.getElementById("secao-pix");
    if (secaoPix) secaoPix.style.display = "block";
}

function exibirCopiaCola(pixPayload) {
    const codigoPix = document.getElementById("codigo-pix");
    if (codigoPix) {
        codigoPix.textContent = pixPayload;
        codigoPix.dataset.payload = pixPayload;
    }
}

async function copiarPix() {
    const codigoPix = document.getElementById("codigo-pix");
    if (!codigoPix) return;

    try {
        await navigator.clipboard.writeText(codigoPix.dataset.payload);
        exibirMensagem("sucesso", "Código PIX copiado!");
    } catch {
        exibirMensagem("erro", "Não foi possível copiar. Copie o código manualmente.");
    }
}

async function carregarHistorico() {
    try {
        const response = await fetch(`${API_URL}/api/pagamentos`, {
            headers: getHeaders()
        });

        if (!response.ok) await tratarErro(response);

        const pagamentos = await response.json();
        exibirHistorico(pagamentos);

        return pagamentos;
    } catch (erro) {
        exibirMensagem("erro", "Erro ao carregar histórico: " + erro.message);
    }
}

function exibirHistorico(pagamentos) {
    const container = document.getElementById("historico-pagamentos");
    if (!container) return;

    if (pagamentos.length === 0) {
        container.innerHTML = "<p>Nenhum pagamento encontrado.</p>";
        return;
    }

    container.innerHTML = pagamentos.map(p => `
        <div class="pagamento-item">
            <span class="pagamento-valor">R$ ${p.valor.toFixed(2)}</span>
            <span class="pagamento-metodo">${traduzirMetodo(p.metodoPagamento)}</span>
            <span class="pagamento-status status-${p.status.toLowerCase()}">${traduzirStatus(p.status)}</span>
            <span class="pagamento-data">${new Date(p.criadoEm).toLocaleDateString("pt-BR")}</span>
        </div>
    `).join("");
}

function exibirMensagem(tipo, texto) {
    const el = document.getElementById("mensagem");
    if (!el) { console.log(`[${tipo}] ${texto}`); return; }

    el.textContent = texto;
    el.className = `mensagem mensagem-${tipo}`;
    el.style.display = "block";

    setTimeout(() => { el.style.display = "none"; }, 5000);
}

function exibirCarregando(mostrar) {
    const el = document.getElementById("carregando");
    if (el) el.style.display = mostrar ? "block" : "none";
}

function traduzirMetodo(metodo) {
    const traducoes = {
        "CARTAO_CREDITO":   "Cartão de Crédito",
        "CARTAO_DEBITO":    "Cartão de Débito",
        "PIX":              "PIX",
        "CARTEIRA_DIGITAL": "Carteira Digital"
    };
    return traducoes[metodo] || metodo;
}

function traduzirStatus(status) {
    const traducoes = {
        "PENDENTE":    "Pendente",
        "PROCESSANDO": "Processando",
        "APROVADO":    "Aprovado",
        "RECUSADO":    "Recusado",
        "CANCELADO":   "Cancelado",
        "ESTORNADO":   "Estornado"
    };
    return traducoes[status] || status;
}

function inicializarPagina() {
    if (!getToken()) {
        window.location.href = "/login.html";
        return;
    }
    aplicarPlano();
}