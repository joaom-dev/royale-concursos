// ─── Utilitário ───────────────────────────────────────────────────────────────
function getToken() {
    return localStorage.getItem("token");
}

const API_URL = "http://localhost:8080";

// ─── Foto no menu lateral ─────────────────────────────────────────────────────
const fotoMenuLateral = document.getElementById("fotoMenuLateral");
if (fotoMenuLateral) {
    const fotoSalva = localStorage.getItem("fotoPerfil");
    if (fotoSalva) fotoMenuLateral.src = fotoSalva;
}

// ─── Elementos de email ───────────────────────────────────────────────────────
const emailAtual = document.getElementById("email-atual");

// ─── Toggle 2FA ───────────────────────────────────────────────────────────────
const toggle2FA = document.getElementById("toggle-2fa");

// ─── Carregar email do perfil via API ─────────────────────────────────────────
async function carregarEmail() {
    const token = getToken();
    if (!token) {
        window.location.href = "/front-end/pages/tela de login/index.html";
        return;
    }

    try {
        const response = await fetch(`${API_URL}/perfil`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!response.ok) throw new Error("Erro ao buscar perfil");

        const perfil = await response.json();
        if (emailAtual) emailAtual.value = perfil.email || "";

    } catch (err) {
        console.error("Erro ao carregar email:", err);
    }
}

// ─── Mostrar/ocultar senhas ───────────────────────────────────────────────────
const eyeIcons = document.querySelectorAll(".eye-icon");
eyeIcons.forEach(icon => {
    icon.addEventListener("click", () => {
        const input = icon.previousElementSibling;
        input.type = input.type === "password" ? "text" : "password";
        icon.classList.toggle("fa-eye-slash");
    });
});

// ─── Salvar ───────────────────────────────────────────────────────────────────
document.getElementById("salvar").addEventListener("click", async () => {
    const token = getToken();
    if (!token) {
        window.location.href = "/front-end/pages/tela de login/index.html";
        return;
    }

    const senhaAtual     = document.getElementById("senha-atual").value;
    const novaSenha      = document.getElementById("nova-senha").value;
    const confirmarSenha = document.getElementById("confirmar-senha").value;

    // Só chama o backend se algum campo de senha foi preenchido
    if (senhaAtual || novaSenha || confirmarSenha) {
        if (!senhaAtual) { alert("Informe a senha atual."); return; }
        if (!novaSenha)  { alert("Informe a nova senha."); return; }
        if (novaSenha !== confirmarSenha) { alert("A nova senha e a confirmação não coincidem."); return; }
        if (novaSenha.length < 6) { alert("A nova senha deve ter pelo menos 6 caracteres."); return; }

        try {
            const response = await fetch(`${API_URL}/perfil/senha`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({ senhaAtual, novaSenha, confirmarSenha })
            });

            if (!response.ok) {
                const msg = await response.text();
                alert("Erro: " + (msg || "Não foi possível alterar a senha."));
                return;
            }

            document.getElementById("senha-atual").value     = "";
            document.getElementById("nova-senha").value      = "";
            document.getElementById("confirmar-senha").value = "";

        } catch (err) {
            alert("Erro de conexão ao tentar alterar a senha.");
            return;
        }
    }

    if (toggle2FA) localStorage.setItem("2fa", toggle2FA.checked);
    alert("Configurações de segurança salvas!");
});

// ─── Modal de exclusão ────────────────────────────────────────────────────────
const modal     = document.getElementById("modal");
const openModal = document.getElementById("openModal");
const closeModal = document.getElementById("closeModal");

openModal.addEventListener("click", () => {
    modal.showModal();
    document.body.classList.add("modal-open");
});

closeModal.addEventListener("click", () => {
    modal.close();
    document.body.classList.remove("modal-open");
});

// ─── Inicialização ────────────────────────────────────────────────────────────
window.addEventListener("load", () => {
    if (toggle2FA) toggle2FA.checked = localStorage.getItem("2fa") === "true";
    carregarEmail();
});