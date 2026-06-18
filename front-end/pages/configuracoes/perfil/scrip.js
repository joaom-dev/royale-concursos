// ─── Utilitário ───────────────────────────────────────────────────────────────
function getToken() {
    return localStorage.getItem("token");
}

const API_URL = "http://localhost:8080";

// ─── Elementos do DOM ─────────────────────────────────────────────────────────
const inputFoto       = document.getElementById("inputFoto");
const fotoPerfil      = document.getElementById("fotoPerfil");
const fotoMenuLateral = document.getElementById("fotoMenuLateral");
const nomeAtual       = document.getElementById("nome-atual");   // somente leitura
const inputNome       = document.getElementById("full-name");    // novo nome
const temaToggle      = document.getElementById("tema");
const notifEmail      = document.getElementById("notif-email");
const botaoSalvar     = document.getElementById("salvar");

const FOTO_PADRAO = "/front-end/assets/images/foto-padrao.webp";

// ─── Tema escuro ──────────────────────────────────────────────────────────────
function aplicarTema() {
    const temaSalvo = localStorage.getItem("tema") === "true";
    document.documentElement[temaSalvo ? "setAttribute" : "removeAttribute"]("data-theme", "dark");
    if (temaToggle) temaToggle.checked = temaSalvo;
}

// ─── Foto ─────────────────────────────────────────────────────────────────────
function atualizarFotoNaTela(url) {
    const src = url || FOTO_PADRAO;
    if (fotoPerfil)      fotoPerfil.src     = src;
    if (fotoMenuLateral) fotoMenuLateral.src = src;
    localStorage.setItem("fotoPerfil", src);
}

function carregarFotoLocal() {
    const fotoSalva = localStorage.getItem("fotoPerfil");
    if (fotoSalva && fotoMenuLateral) fotoMenuLateral.src = fotoSalva;
}

// ─── Carregar perfil da API ───────────────────────────────────────────────────
async function carregarPerfil() {
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

        // Exibe o nome atual no campo desabilitado
        if (nomeAtual) nomeAtual.value = perfil.name || "";

        // Foto
        const urlFoto = perfil.foto ? `${API_URL}${perfil.foto}` : null;
        atualizarFotoNaTela(urlFoto);

    } catch (err) {
        console.error(err);
        alert("Erro ao carregar dados do perfil. Verifique sua conexão.");
    }
}

// ─── Preview da foto ao selecionar ───────────────────────────────────────────
if (inputFoto) {
    inputFoto.addEventListener("change", () => {
        const arquivo = inputFoto.files[0];
        if (!arquivo) return;
        const reader = new FileReader();
        reader.onload = () => atualizarFotoNaTela(reader.result);
        reader.readAsDataURL(arquivo);
    });
}

// ─── Toggle tema ──────────────────────────────────────────────────────────────
if (temaToggle) {
    temaToggle.addEventListener("change", () => {
        localStorage.setItem("tema", temaToggle.checked);
        aplicarTema();
    });
}

// ─── Salvar ───────────────────────────────────────────────────────────────────
if (botaoSalvar) {
    botaoSalvar.addEventListener("click", async () => {
        const token = getToken();
        if (!token) return;

        const novoNome = inputNome ? inputNome.value.trim() : "";

        // 1. Atualizar nome (só se o usuário digitou algo)
        if (novoNome) {
            try {
                const res = await fetch(`${API_URL}/perfil`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": "Bearer " + token
                    },
                    body: JSON.stringify({ name: novoNome })
                });

                if (!res.ok) {
                    const msg = await res.text();
                    throw new Error(msg || "Erro ao atualizar nome");
                }

                // Atualiza o campo "nome atual" na tela com o novo valor
                if (nomeAtual) nomeAtual.value = novoNome;
                if (inputNome) inputNome.value = "";

            } catch (err) {
                alert("Erro ao salvar nome: " + err.message);
                return;
            }
        }

        // 2. Upload de foto (só se selecionou uma nova)
        if (inputFoto && inputFoto.files[0]) {
            const formData = new FormData();
            formData.append("foto", inputFoto.files[0]);

            try {
                const resFoto = await fetch(`${API_URL}/perfil/foto`, {
                    method: "POST",
                    headers: { "Authorization": "Bearer " + token },
                    body: formData
                });

                if (resFoto.ok) {
                    const urlRelativa = await resFoto.text();
                    atualizarFotoNaTela(`${API_URL}${urlRelativa}`);
                } else {
                    console.warn("Não foi possível salvar a foto.");
                }
            } catch (err) {
                console.warn("Erro no upload da foto:", err);
            }
        }

        // 3. Preferências locais
        localStorage.setItem("tema", temaToggle ? temaToggle.checked : false);
        localStorage.setItem("notifEmail", notifEmail ? notifEmail.checked : false);
        aplicarTema();

        alert("Alterações salvas com sucesso!");
    });
}

// ─── Inicialização ────────────────────────────────────────────────────────────
window.addEventListener("load", () => {
    if (notifEmail) notifEmail.checked = localStorage.getItem("notifEmail") === "true";
    aplicarTema();
    carregarFotoLocal();
    carregarPerfil();
});