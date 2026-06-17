// ─── Utilitário: pega o token salvo no localStorage ───────────────────────────
function getToken() {
    return localStorage.getItem("token");
}

// ─── Foto no menu lateral (sincroniza com o que foi salvo em localStorage) ────
const fotoMenuLateral = document.getElementById("fotoMenuLateral");
if (fotoMenuLateral) {
    const fotoSalva = localStorage.getItem("fotoPerfil");
    if (fotoSalva) fotoMenuLateral.src = fotoSalva;
}

// ─── Toggle 2FA (preferência local) ──────────────────────────────────────────
const toggle2FA = document.getElementById("toggle-2fa");

window.addEventListener("load", () => {
    if (toggle2FA) toggle2FA.checked = localStorage.getItem("2fa") === "true";
});

// ─── Mostrar/ocultar senhas ───────────────────────────────────────────────────
const eyeIcons = document.querySelectorAll(".eye-icon");

eyeIcons.forEach(icon => {
    icon.addEventListener("click", () => {
        const input = icon.previousElementSibling;
        input.type = input.type === "password" ? "text" : "password";
        icon.classList.toggle("fa-eye-slash");
    });
});

// ─── Salvar alterações de segurança ──────────────────────────────────────────
document.getElementById("salvar").addEventListener("click", async () => {
    const token = getToken();
    if (!token) {
        window.location.href = "/front-end/pages/login/index.html";
        return;
    }

    const senhaAtual      = document.getElementById("senha-atual").value;
    const novaSenha       = document.getElementById("nova-senha").value;
    const confirmarSenha  = document.getElementById("confirmar-senha").value;

    // Só tenta alterar a senha se o usuário preencheu algum campo
    const algumaSenhaPreenchida = senhaAtual || novaSenha || confirmarSenha;

    if (algumasSenhaPreenchida()) {
        // Validações básicas no front antes de chamar a API
        if (!senhaAtual) {
            alert("Informe a senha atual.");
            return;
        }
        if (!novaSenha) {
            alert("Informe a nova senha.");
            return;
        }
        if (novaSenha !== confirmarSenha) {
            alert("A nova senha e a confirmação não coincidem.");
            return;
        }
        if (novaSenha.length < 6) {
            alert("A nova senha deve ter pelo menos 6 caracteres.");
            return;
        }

        try {
            const response = await fetch("/perfil/senha", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({
                    senhaAtual:      senhaAtual,
                    novaSenha:       novaSenha,
                    confirmarSenha:  confirmarSenha
                })
            });

            if (!response.ok) {
                const msg = await response.text();
                alert("Erro: " + (msg || "Não foi possível alterar a senha."));
                return;
            }

            // Limpa os campos após sucesso
            document.getElementById("senha-atual").value     = "";
            document.getElementById("nova-senha").value      = "";
            document.getElementById("confirmar-senha").value = "";

        } catch (err) {
            alert("Erro de conexão ao tentar alterar a senha.");
            return;
        }
    }

    // Salva preferência 2FA no localStorage
    if (toggle2FA) localStorage.setItem("2fa", toggle2FA.checked);

    alert("Configurações de segurança salvas!");

    // Helper interno
    function algumasSenhaPreenchida() {
        return senhaAtual || novaSenha || confirmarSenha;
    }
});

// ─── Modal de exclusão de conta ───────────────────────────────────────────────
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