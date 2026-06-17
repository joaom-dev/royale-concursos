# codigo html

<div class="menu-lateral">
        <div class="menu-topo">
            <img src="/front-end/assets/images/lebron bb.jpg" alt="">
        </div>

        <div class="menu-itens">
            <a href="../telaInicial/index.html">
                <p>Inicial</p>
                <!-- <i class="fa-solid fa-house ativo"></i> -->
                <!-- <span class="tooltip-texto">Página inicial</span> -->
            </a>
            <a href="../pagina sobre/index.html">
                <p>Sobre</p>
                <!-- <i class="fa-regular fa-compass"></i> -->
                <!-- <span class="tooltip-texto">Sobre</span> -->
            </a>
            <a href="/front-end/pages/pagina Ranking/Index.html">
                <p>Ranking</p>
                <!-- <i class="fa-solid fa-trophy"></i> -->
                <!-- <span class="tooltip-texto">Ranking</span> -->
            </a>
            <a href="/front-end/pages/tela de concursos/PgMc.html">
                <p>Concursos</p>
                <!-- <i class="fa-solid fa-bookmark"></i> -->
                <!-- <span class="tooltip-texto">Salvos</span> -->
            </a>
            <a href="/front-end/pages/paginaMeusConcursos/PgMc.html">
                <p>Salvos</p>
                <!-- <i class="fa-solid fa-bookmark"></i> -->
                <!-- <span class="tooltip-texto">Salvos</span> -->
            </a>
            <a href="../pagina assinatura/index.html">
                <p>Assinatura</p>
                <!-- <i class="fa-solid fa-credit-card"></i> -->
                <!-- <span class="tooltip-texto">Assinaturas</span> -->
            </a>
        </div>
        <div class="menu-inferior">
            <a href="../configuraçoes/perfil/configperfil.html">
                <i class="fa-solid fa-gear"></i>
                <span class="tooltip-texto">Configurações</span>
            </a>
        </div>
</div>

# css

.menu-lateral {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 70px;
    background-color: #ffffff;
    border-bottom: 1px solid #ccc;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px;
    z-index: 100;
}

.menu-inferior {
    padding-right: 12px;
}

.menu-topo img {
    width: 44px;
    height: 44px;
    border-radius: 50%;
}

.menu-itens {
    display: flex;
    flex-direction: row;
    gap: 50px;
}

.menu-itens p {
    font-size: 18px;
    color: #000000;
    cursor: pointer;
    transition: 0.2s;
}

.menu-inferior i {
    font-size: 25px;
    color: #000000;
    cursor: pointer;
    transition: 0.2s;
}

.menu-itens p:hover,
.menu-inferior i:hover {
    transform: scale(1.2);
}

.menu-inferior a {
    color: black;
}

.menu-itens a,
.menu-inferior a {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    height: auto;
    width: auto;
    text-decoration: none;
}

.tooltip-texto {
    visibility: hidden;
    opacity: 0;
    font-size: 14px;
    padding: 6px 12px;
    border-radius: 6px;
    white-space: nowrap;
    position: absolute;
    top: 40px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 9999;
    transition: opacity 0.2s ease, visibility 0.2s ease;
    background: black;
    color: white;
}

.menu-itens a:hover .tooltip-texto,
.menu-inferior a:hover .tooltip-texto {
    visibility: visible;
    opacity: 1;
}
