// ── anuncios.js ───────────────────────────────────────────────────────────────
// Busca anúncios ativos da API e injeta no topo e na lateral da página.
// Uso: <script src="anuncios.js"></script> em qualquer página HTML.
// Dependência: anuncios.css linkado no <head>.

(function () {
    const API_URL = "http://localhost:8080";

    // ── Buscar anúncios ────────────────────────────────────────────────────────
    async function carregarAnuncios() {
        try {
            const res = await fetch(`${API_URL}/anuncios`);
            if (!res.ok) return;
            const lista = await res.json();
            if (!lista || lista.length === 0) return;

            // Separa por posição
            const topo    = lista.filter(a => a.posicao === "topo");
            const lateral = lista.filter(a => a.posicao === "lateral");

            // Pega um aleatório de cada posição (rotação simples)
            if (topo.length > 0)    renderTopo(topo[Math.floor(Math.random() * topo.length)]);
            if (lateral.length > 0) renderLateral(lateral[Math.floor(Math.random() * lateral.length)]);

        } catch (err) {
            // Silencioso: se o backend estiver offline, não quebra a página
            console.warn("[Anuncios] Não foi possível carregar:", err.message);
        }
    }

    // ── Render: banner topo ────────────────────────────────────────────────────
    function renderTopo(ad) {
        const container = document.getElementById("anuncio-topo");
        if (!container) return;

        container.innerHTML = `
            <span class="ad-label">Patrocinado</span>
            ${ad.imagemUrl ? `<img class="ad-imagem" src="${ad.imagemUrl}" alt="${ad.titulo}">` : ""}
            <div class="ad-texto">
                <span class="ad-titulo">${ad.titulo}</span>
                ${ad.descricao ? `<span class="ad-descricao">${ad.descricao}</span>` : ""}
            </div>
            ${ad.linkDestino
                ? `<a class="ad-btn" href="${ad.linkDestino}" target="_blank" rel="noopener">Saiba mais</a>`
                : ""}
            <button class="ad-fechar" title="Fechar anúncio">✕</button>
        `;

        container.querySelector(".ad-fechar").addEventListener("click", () => {
            container.classList.add("ad-oculto");
        });
    }

    // ── Render: card lateral ───────────────────────────────────────────────────
    function renderLateral(ad) {
        const container = document.getElementById("anuncio-lateral");
        if (!container) return;

        container.innerHTML = `
            <span class="ad-label">Patrocinado</span>
            
            ${ad.imagemUrl ? `<img class="ad-imagem" src="${ad.imagemUrl}" alt="${ad.titulo}">` : ""}
            <div class="ad-corpo">
                <span class="ad-titulo">${ad.titulo}</span>
                ${ad.descricao ? `<span class="ad-descricao">${ad.descricao}</span>` : ""}
                ${ad.linkDestino
                    ? `<a class="ad-btn" href="${ad.linkDestino}" target="_blank" rel="noopener">Saiba mais</a>`
                    : ""}
            </div>
        `;

        container.querySelector(".ad-fechar").addEventListener("click", () => {
            container.classList.add("ad-oculto");
        });
    }

    // ── Init ───────────────────────────────────────────────────────────────────
    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", carregarAnuncios);
    } else {
        carregarAnuncios();
    }
})();