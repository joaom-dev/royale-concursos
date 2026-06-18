// ─── Sincronizador de foto de perfil (shared) ─────────────────────────────────
// Coloque este arquivo em: /front-end/shared/js/fotoSync.js
// Inclua em TODAS as páginas que têm o header com foto:
// <script src="/front-end/shared/js/fotoSync.js"></script>

(function () {
    const API_URL = "http://localhost:8080";
    const FOTO_PADRAO = "/front-end/assets/images/lebron bb.jpg";

    function getToken() {
        return localStorage.getItem("token");
    }

    // Aplica a foto em qualquer img com id="fotoHeader" na página
    function aplicarFoto(url) {
        const img = document.getElementById("fotoHeader");
        if (img) img.src = url || FOTO_PADRAO;
    }

    // 1. Aplica imediatamente do localStorage (sem piscar)
    const fotoSalva = localStorage.getItem("fotoPerfil");
    if (fotoSalva) aplicarFoto(fotoSalva);

    // 2. Sincroniza com a API em segundo plano
    async function sincronizar() {
        const token = getToken();
        if (!token) return;

        try {
            const res = await fetch(`${API_URL}/perfil`, {
                headers: { "Authorization": "Bearer " + token }
            });
            if (!res.ok) return;

            const perfil = await res.json();
            if (perfil.foto) {
                const url = API_URL + perfil.foto;
                localStorage.setItem("fotoPerfil", url);
                aplicarFoto(url);
            }
        } catch (err) {
            // falha silenciosa — mantém foto do localStorage
        }
    }

    sincronizar();
})();