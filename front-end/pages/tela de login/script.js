//mostrar senha
const eyeIcons = document.querySelectorAll(".eye-icon")

eyeIcons.forEach(icon => {
    icon.addEventListener("click", () => {
        const input = icon.previousElementSibling
        const type = input.type === "password" ? "text" : "password"
        input.type = type
        icon.classList.toggle("fa-eye-slash")
    })
})