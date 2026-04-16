# Comandos para principal header do site

## HTLM

    <header>
        <button class="botao-menu">
            <i class="fa-solid fa-bars"></i>
        </button>

        <nav class="menu-lateral">

            <div class="perfil">
                <img src="./imgs/lebron bb.jpg" alt="logo-usuario">
                <p id="nome-usuario">Lebron BB</p>
            </div>
            

            <ul>
                <li class="nomes-categorias">Sistema</li>
                <li class="header-list"><a href="../configuraçoes/perfil/configperfil.html"><i class="fa-solid fa-gears"></i>Configuracoes</a></li>
                <li class="header-list"><a href="../pagina sobre/index.html"><i class="fa-solid fa-circle-exclamation"></i>Sobre</a></li>
                <li class="header-list"><a href="/front-end/pages/telaInicial/index.html"><i class="fa-solid fa-house"></i>Inicio</a></li>

                
                <li class="nomes-categorias">Salvos</li>
                <li class="header-list"><a href="#"><i class="fa-solid fa-book-open"></i>Minhas Provas</a></li>
                

            </ul>
        </nav>

        <p id="nome">royale concursos</p>
    </header>

    <main>
        <h2 id="tt">Assinaturas</h2>
    </main>
    
    <div class="background "></div>
    
## CSS
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}


header {
    background-color: #000;
    color: white;
    padding: 15px 40px;
    border-bottom: 1px solid #333;
    display: flex;
    align-items: center;
    justify-content: center;
 
}

main{
    color: white;
    background-color: black;
    font-family: "DM Sans", sans-serif;
    justify-content: center;
    display: flex;
    flex-direction: column;
    align-items: center;
    height: 140px;
    gap: 30px;
}

#tt{
    font-size: 35px;
}

#nome{
font-family: "DM Sans", sans-serif;
    font-size: 40px;
    padding-bottom: 25px;
    padding-top: 15px;
    
}

.perfil{
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    padding-top: 20px;
}

.perfil img{
    width: 100px;
    height: 100px;
    border-radius: 50%;
    object-fit: cover;
    margin-bottom: 8px
}

#nome-usuario{
    font-size: 25px;
    color: #ffffff;
    font-family: "DM Sans", sans-serif;
}

:root{
    --primary-color: rgb(14, 14, 14);
    --secondary-color: rgb(34, 33, 33);
    --text-color: white;
    --background-color: rgb(8, 8, 8);
}

.menu-lateral{
    position: fixed;
    left: -300px;
    top: 0;
    width: 300px;
    height: 100%;
    background-color: var(--background-color);
    z-index: 5;
    box-shadow: 0 0 3px rgb(37, 37, 37);
    transition: 0.5s ease;
}

.menu-lateral.ativo{
    left: 0;
}

.menu-lateral ul{
    list-style-type: none;
    padding: 0;
    margin-top: 30px;
    
}

.menu-lateral ul li{
    padding: 15px 30px;
    font-family: "DM Sans", sans-serif;
}

.menu-lateral ul li a{
    text-decoration: none;
    color: var(--text-color);
    font-size: 18px;
    display: flex;
    align-items: center;
    gap: 10px;
}



.menu-lateral ul li a i{
    font-size: 24px;
}

.header-list:hover{
    background-color: var(--secondary-color);
}

.menu-lateral ul li a i:hover{
    transform: translateX(5px) rotate(5deg);
}

.botao-menu{
    background-color: var(--primary-color);
    left: 25px;
    top: 25px;
    z-index: 6;
    border-radius: 5px;
    border: none;
    padding: 10px 15px;
    font-size: 24px;
    cursor: pointer;
    color: var(--text-color);
    transition: all 0.5s ease;
    position: absolute;
  
}

.botao-menu:hover{
    background-color: white;
    color: black;
}

.botao-menu.ativo{
    left: 380px;
}

body{
    transition: 0.5s ease;
}

body.ativo{
    margin-left: 300px;
    transition: 0.5s ease;
   
}

.background{
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.2);
    opacity: 0;
    visibility: hidden;
    z-index: 4;
}

.background.ativo{
    opacity: 2;
    visibility: visible;
    
}

.nomes-categorias {
    font-size: 2rem;
    border-bottom: 1px solid #333;
}

## JAVA SCRIPT

// COMANDO PARA MENU LATERAL
const botao = document.querySelector('.botao-menu')
const menuLateral = document.querySelector('.menu-lateral')
const conteudo = document.querySelector('body')
const background = document.querySelector('.background')

botao.addEventListener('click', ()=> {
    menuLateral.classList.toggle('ativo')
    conteudo.classList.toggle('ativo')
    background.classList.toggle('ativo')
    botao.classList.toggle('ativo')
})

background.addEventListener('click', () => {
    menuLateral.classList.remove('ativo')
    conteudo.classList.remove('ativo')
    background.classList.remove('ativo')
    botao.classList.remove('ativo')                       
})

