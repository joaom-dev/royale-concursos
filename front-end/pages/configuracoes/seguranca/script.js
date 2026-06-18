const API_URL = "http://localhost:8080";

function getToken() {
    return localStorage.getItem("token");
}

// ─── Foto no menu lateral ─────────────────────────────────────────────────────
const fotoMenuLateral = document.getElementById("fotoMenuLateral");
if (fotoMenuLateral) {
    const fotoSalva = localStorage.getItem("fotoPerfil");
    if (fotoSalva) fotoMenuLateral.src = fotoSalva;
}

// ─── Carregar email do perfil ─────────────────────────────────────────────────
async function carregarEmail() {
    const token = getToken();
    if (!token) {
        window.location.href = "/front-end/pages/tela de login/index.html";
        return;
    }
    try {
        const res = await fetch(`${API_URL}/perfil`, {
            headers: { "Authorization": "Bearer " + token }
        });
        if (!res.ok) throw new Error();
        const perfil = await res.json();
        if (emailAtual) emailAtual.value = perfil.email || "";
    } catch {
        console.error("Erro ao carregar email");
    }
}

// ─── Elementos ────────────────────────────────────────────────────────────────
const emailAtual        = document.getElementById("email-atual");
const toggle2FA         = document.getElementById("toggle-2fa");
const btnAbrirEmail     = document.getElementById("btnAbrirEmail");
const secaoAlterarEmail = document.getElementById("secaoAlterarEmail");

// ─── Toggle seção alterar email ───────────────────────────────────────────────
btnAbrirEmail.addEventListener("click", () => {
    const aberto = secaoAlterarEmail.classList.toggle("aberto");
    btnAbrirEmail.innerHTML = aberto
        ? '<i class="fa-solid fa-xmark"></i> Cancelar'
        : '<i class="fa-solid fa-pen"></i> Alterar email';
    if (!aberto) {
        document.getElementById("novo-email").value = "";
        document.getElementById("confirmar-email").value = "";
    }
});

// ─── Mostrar/ocultar senhas ───────────────────────────────────────────────────
document.querySelectorAll(".eye-icon").forEach(icon => {
    icon.addEventListener("click", () => {
        const input = icon.previousElementSibling;
        input.type = input.type === "password" ? "text" : "password";
        icon.classList.toggle("fa-eye-slash");
    });
});

// ─── Salvar alterações ────────────────────────────────────────────────────────
document.getElementById("salvar").addEventListener("click", async () => {
    const token = getToken();
    if (!token) {
        window.location.href = "/front-end/pages/tela de login/index.html";
        return;
    }

    let sucesso = true;

    // — Alterar email —
    const novoEmail      = document.getElementById("novo-email").value.trim();
    const confirmarEmail = document.getElementById("confirmar-email").value.trim();

    if (novoEmail || confirmarEmail) {
        if (!novoEmail) { alert("Informe o novo email."); return; }
        if (novoEmail !== confirmarEmail) { alert("Os emails não coincidem."); return; }
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(novoEmail)) { alert("Email inválido."); return; }

        try {
            const res = await fetch(`${API_URL}/perfil`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({ email: novoEmail })
            });

            if (!res.ok) {
                const msg = await res.text();
                alert("Erro ao alterar email: " + (msg || "Tente novamente."));
                sucesso = false;
            } else {
                // Atualiza o campo de exibição e fecha a seção
                emailAtual.value = novoEmail;
                secaoAlterarEmail.classList.remove("aberto");
                btnAbrirEmail.innerHTML = '<i class="fa-solid fa-pen"></i> Alterar email';
                document.getElementById("novo-email").value = "";
                document.getElementById("confirmar-email").value = "";
            }
        } catch {
            alert("Erro de conexão ao alterar email.");
            sucesso = false;
        }
    }

    // — Alterar senha —
    const senhaAtual     = document.getElementById("senha-atual").value;
    const novaSenha      = document.getElementById("nova-senha").value;
    const confirmarSenha = document.getElementById("confirmar-senha").value;

    if (senhaAtual || novaSenha || confirmarSenha) {
        if (!senhaAtual)  { alert("Informe a senha atual."); return; }
        if (!novaSenha)   { alert("Informe a nova senha."); return; }
        if (novaSenha !== confirmarSenha) { alert("A nova senha e a confirmação não coincidem."); return; }
        if (novaSenha.length < 6) { alert("A nova senha deve ter pelo menos 6 caracteres."); return; }

        try {
            const res = await fetch(`${API_URL}/perfil/senha`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({ senhaAtual, novaSenha, confirmarSenha })
            });

            if (!res.ok) {
                const msg = await res.text();
                alert("Erro ao alterar senha: " + (msg || "Tente novamente."));
                sucesso = false;
            } else {
                document.getElementById("senha-atual").value     = "";
                document.getElementById("nova-senha").value      = "";
                document.getElementById("confirmar-senha").value = "";
            }
        } catch {
            alert("Erro de conexão ao alterar a senha.");
            sucesso = false;
        }
    }

    if (toggle2FA) localStorage.setItem("2fa", toggle2FA.checked);
    if (sucesso) alert("Configurações salvas com sucesso!");
});

// ─── Modal excluir conta ──────────────────────────────────────────────────────
const modal      = document.getElementById("modal");
const openModal  = document.getElementById("openModal");
const closeModal = document.getElementById("closeModal");

openModal.addEventListener("click", () => {
    modal.showModal();
    document.body.classList.add("modal-open");
});

closeModal.addEventListener("click", () => {
    modal.close();
    document.body.classList.remove("modal-open");
});

document.getElementById("sim").addEventListener("click", async () => {
    const token = getToken();
    if (!token) return;

    const btnSim = document.getElementById("sim");
    btnSim.disabled = true;
    btnSim.textContent = "Excluindo...";

    try {
        const res = await fetch(`${API_URL}/perfil`, {
            method: "DELETE",
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) throw new Error();

        // Limpa dados locais e redireciona para login
        localStorage.clear();
        sessionStorage.clear();
        modal.close();
        alert("Conta excluída com sucesso.");
        window.location.href = "/front-end/pages/tela de login/index.html";

    } catch {
        alert("Erro ao excluir conta. Tente novamente.");
        btnSim.disabled = false;
        btnSim.textContent = "Sim, excluir";
    }
});

// ─── Inicialização ────────────────────────────────────────────────────────────
window.addEventListener("load", () => {
    if (toggle2FA) toggle2FA.checked = localStorage.getItem("2fa") === "true";
    carregarEmail();
});