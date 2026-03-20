
//mostrar senha
const eyeIcon = document.querySelector('.eye-icon')
const senhaInput = document.getElementById('password')

if (eyeIcon && senhaInput) {
    eyeIcon.addEventListener('click', () => {
        const type = senhaInput.type === 'password' ? 'text' : 'password';
        senhaInput.type = type;
        eyeIcon.classList.toggle('fa-eye-slash');
    });
}

function validarFormulario(){
    const email = document.getElementById("email").value
    const erroemail = document.getElementById("erro-email")
    const senha = document.getElementById("password").value
    const errosenha = document.getElementById("erro-senha")
    const cpf = document.getElementById("cpf").value
    const nome = document.getElementById("nome").value

    //e uma expressao que serve para verificar textos, neste caso esta regex e para validar emails
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/ 

    if(!regex.test(email)) {
        erroemail.innerText = "Email Valido"
        return false
    }

    erroemail.innerText = "" // serve para apagar a mensagem de erro depois de estar certo

    if(senha.length < 8) {
        errosenha.innerText = "A senha deve conter no minimo 8 caracteres"
        return false
    }

    errosenha.innerText = ""
    return true
}