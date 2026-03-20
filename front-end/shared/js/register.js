console.log("JS carregou");

const form = document.querySelector("form");
const eyeIcon = document.querySelector('.eye-icon');
const senhaInput = document.getElementById('senha');

if (eyeIcon && senhaInput) {
    eyeIcon.addEventListener('click', () => {
        const type = senhaInput.type === 'password' ? 'text' : 'password';
        senhaInput.type = type;
        eyeIcon.classList.toggle('fa-eye-slash');
    });
}

function validarFormulario() {
    const email = document.getElementById("email").value;
    const erroemail = document.getElementById("erro-email");
    const senha = document.getElementById("senha").value;
    const errosenha = document.getElementById("erro-senha");

    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!regex.test(email)) {
        erroemail.innerText = "Email inválido";
        return false;
    }

    erroemail.innerText = "";

    if (senha.length < 8) {
        errosenha.innerText = "A senha deve ter no mínimo 8 caracteres";
        return false;
    }

    errosenha.innerText = "";

    return true;
}

if (form) {
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        if (!validarFormulario()) return;

        const name = document.querySelector("#nome").value;
        const email = document.querySelector("#email").value;
        const password = document.querySelector("#senha").value;
        const cpf = document.querySelector("#cpf").value;

        try {
            const response = await fetch("http://localhost:8080/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    name,
                    email,
                    password,
                    cpf
                })
            });

            const data = await response.json();
            console.log("RESPOSTA:", data);

            if (response.ok) {
                alert("Usuário cadastrado com sucesso!");
                window.location.href = "../telaInicial/index.html";
            } else {
                alert(data.message);
            }

        } catch (error) {
            console.error("ERRO COMPLETO:", error);
            alert("Erro na requisição");
        }
    });
}