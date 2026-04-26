

//foto de perfil
const inputFoto = document.getElementById("inputFoto")
const fotoPerfil = document.getElementById("fotoPerfil")

inputFoto.addEventListener("change", () => {
    const arquivo = inputFoto.files[0]
    if (arquivo) {
        const reader = new FileReader ()
        reader.onload = () => {
            fotoPerfil.src = reader.result
        }
        reader.readAsDataURL(arquivo)
    }

})

//config de perfil
const temaToggle = document.getElementById("tema")
const notifEmail = document.getElementById("notif-email")
const notifPlatform = document.getElementById("notif-platform")
const botaoSalvar = document.getElementById("salvar")

// carregar preferências salvas
window.addEventListener("load", () => {
    temaToggle.checked = localStorage.getItem("tema") === "true"
    notifEmail.checked = localStorage.getItem("notifEmail") === "true"
    notifPlatform.checked = localStorage.getItem("notifPlatform") === "true"

    aplicarTema()
})

// salvar ao clicar
botaoSalvar.addEventListener("click", () => {
    localStorage.setItem("tema", temaToggle.checked)
    localStorage.setItem("notifEmail", notifEmail.checked)
    localStorage.setItem("notifPlatform", notifPlatform.checked)

    alert("Alterações salvas com sucesso!")
})

// aplicar tema
