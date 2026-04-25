# codigo html

<div class="menu-lateral">
        <div class="menu-topo">
            <img src="/front-end/assets/images/lebron bb.jpg" alt="">
        </div>

        <div class="menu-itens">
            <a href="../telaInicial/index.html">
                <i class="fa-solid fa-house ativo"></i>
                <span class="tooltip-texto">Página inicial</span>
            </a>
            <a href="../pagina sobre/index.html">
                <i class="fa-regular fa-compass"></i>
                <span class="tooltip-texto">Sobre</span>
            </a>
            <a href="">
                <i class="fa-solid fa-trophy"></i>
                <span class="tooltip-texto">Ranking</span>
            </a>
            <a href="">
                <i class="fa-regular fa-calendar"></i>
                <span class="tooltip-texto">Calendário</span>
            </a>
            <a href="">
                <i class="fa-solid fa-bookmark"></i>
                <span class="tooltip-texto">Salvos</span>
            </a>
            <a href="">
                <i class="fa-solid fa-credit-card"></i>
                <span class="tooltip-texto">Assinaturas</span>
            </a>
        </div>

        <div class="menu-inferior">
            <a href="../configuraçoes/perfil/configperfil.html">
                <i class="fa-solid fa-gear"></i>
                <span class="tooltip-texto">Configurações</span>
            </a>
        </div>
    </div>

# codigo css

.menu-lateral {
    position: fixed;
    width: 80px;
    height: 100vh;
    background-color: #ffffff;
    border-right: 1px solid #ccc;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    padding: 20px 0;
}

.menu-topo img {
    width: 50px;
    border-radius: 50%;
    height: 50px;
    margin: 0;
    padding: 0;
}

.menu-itens {
    display: flex;
    flex-direction: column;
    gap: 50px;
}

.menu-itens i {
    font-size: 25px;
    color: #000000;
    cursor: pointer;
    position: relative;
    transition: 0.2s;

}

.menu-itens i:hover {
    transform: scale(1.2);
    color: #000;
}

.menu-inferior i:hover {
    transform: scale(1.2);
    color: #000;
}

.menu-inferior i {
    font-size: 25px;
    cursor: pointer;
    transition: 0.2s;
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
    width: 100%;
    height: 15px;
    text-decoration: none;
}

.tooltip-texto {
    visibility: hidden;
    opacity: 0;
    font-family: "DM Sans", sans-serif;
    font-size: 14px;
    padding: 6px 12px;
    border-radius: 6px;
    white-space: nowrap;
    position: absolute;
    left: 65px;
    top: 50%;
    transform: translateY(-50%);
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