// tema.js - incluir em TODAS as páginas
function aplicarTema() {
    const temaSalvo = localStorage.getItem("tema") === "true"
    if (temaSalvo) {
        document.documentElement.setAttribute("data-theme", "dark")
    } else {
        document.documentElement.removeAttribute("data-theme")
    }
}

window.addEventListener("load", aplicarTema)