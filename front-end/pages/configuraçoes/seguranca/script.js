

// toggle 2FA
const toggle2FA = document.getElementById("toggle-2fa")

window.addEventListener("load", () => {
    toggle2FA.checked = localStorage.getItem("2fa") === "true"
})

document.getElementById("salvar").addEventListener("click", () => {
    localStorage.setItem("2fa", toggle2FA.checked)
    alert("Configurações de segurança salvas!")
})

// mostrar/ocultar todas as senhas
const eyeIcons = document.querySelectorAll(".eye-icon")

eyeIcons.forEach(icon => {
    icon.addEventListener("click", () => {
        const input = icon.previousElementSibling
        const type = input.type === "password" ? "text" : "password"
        input.type = type
        icon.classList.toggle("fa-eye-slash")
    })
})

const modal = document.getElementById('modal')
const openModal = document.getElementById('openModal')
const closeModal = document.getElementById('closeModal')

openModal.addEventListener('click', ()=>{
    modal.showModal()
    document.body.classList.add('modal-open')
})

closeModal.addEventListener('click', ()=>{
    modal.close()
    document.body.classList.remove('modal-open')
})