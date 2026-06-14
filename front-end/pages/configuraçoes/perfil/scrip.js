//foto de perfil
const inputFoto = document.getElementById("inputFoto")
const fotoPerfil = document.getElementById("fotoPerfil")

if (inputFoto) {
    inputFoto.addEventListener("change", () => {
        const arquivo = inputFoto.files[0]
        if (arquivo) {
            const reader = new FileReader()
            reader.onload = () => {
                fotoPerfil.src = reader.result
            }
            reader.readAsDataURL(arquivo)
        }
    })
}

//config de perfil
const temaToggle = document.getElementById("tema")
const notifEmail = document.getElementById("notif-email")
const notifPlatform = document.getElementById("notif-platform")
const botaoSalvar = document.getElementById("salvar")

// aplicar tema (roda em TODAS as páginas)
function aplicarTema() {
    const temaSalvo = localStorage.getItem("tema") === "true"
    if (temaSalvo) {
        document.documentElement.setAttribute("data-theme", "dark")
    } else {
        document.documentElement.removeAttribute("data-theme")
    }
    if (temaToggle) temaToggle.checked = temaSalvo
}

// carregar preferências salvas
window.addEventListener("load", () => {
    if (notifEmail) notifEmail.checked = localStorage.getItem("notifEmail") === "true"
    if (notifPlatform) notifPlatform.checked = localStorage.getItem("notifPlatform") === "true"

    aplicarTema()
})

// alternar tema em tempo real ao clicar no switch (sem precisar salvar)
if (temaToggle) {
    temaToggle.addEventListener("change", () => {
        localStorage.setItem("tema", temaToggle.checked)
        aplicarTema()
    })
}

// salvar ao clicar
if (botaoSalvar) {
    botaoSalvar.addEventListener("click", () => {
        localStorage.setItem("tema", temaToggle.checked)
        localStorage.setItem("notifEmail", notifEmail.checked)
        if (notifPlatform) localStorage.setItem("notifPlatform", notifPlatform.checked)

        aplicarTema()
        alert("Alterações salvas com sucesso!")
    })
}