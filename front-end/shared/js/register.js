console.log("JS carregou");

const form = document.querySelector("form");
const senhaInput = document.getElementById('senha');


// Verifica se o CPF tem a estrutura matemática correta (dígitos verificadores)
function validarCPF(cpf) {
    // Remove pontos e traço, deixa só números
    cpf = cpf.replace(/[^\d]/g, '');

    // CPF deve ter exatamente 11 dígitos
    if (cpf.length !== 11) return false;

    // Rejeita sequências repetidas como 000.000.000-00, 111.111.111-11, etc.
    if (/^(\d)\1+$/.test(cpf)) return false;

    // Calcula e valida o 1º dígito verificador
    let soma = 0;
    for (let i = 0; i < 9; i++) soma += parseInt(cpf[i]) * (10 - i);
    let resto = (soma * 10) % 11;
    if (resto === 10 || resto === 11) resto = 0;
    if (resto !== parseInt(cpf[9])) return false;

    // Calcula e valida o 2º dígito verificador
    soma = 0;
    for (let i = 0; i < 10; i++) soma += parseInt(cpf[i]) * (11 - i);
    resto = (soma * 10) % 11;
    if (resto === 10 || resto === 11) resto = 0;
    if (resto !== parseInt(cpf[10])) return false;

    return true; // CPF matematicamente válido
}

// Lista de domínios de email conhecidos e existentes
const dominiosValidos = [
    'gmail.com', 'yahoo.com', 'hotmail.com', 'outlook.com', 'icloud.com',
    'live.com', 'msn.com', 'uol.com.br', 'bol.com.br', 'terra.com.br',
    'ig.com.br', 'globo.com', 'r7.com', 'protonmail.com', 'mail.com',
    'yahoo.com.br', 'hotmail.com.br', 'outlook.com.br', 'oi.com.br'
];

function validarEmail(email) {
    // Verifica o formato básico do email com regex
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regex.test(email)) return { valido: false, mensagem: "Formato de email inválido" };

    // Extrai o domínio (parte depois do @)
    const dominio = email.split('@')[1].toLowerCase();

    // Verifica se o domínio está na lista de provedores conhecidos
    if (!dominiosValidos.includes(dominio)) {
        return { valido: false, mensagem: "Provedor de email não reconhecido" };
    }

    return { valido: true, mensagem: "" };
}

function validarFormulario() {
    const email = document.getElementById("email").value;
    const erroemail = document.getElementById("erro-email");
    const senha = document.getElementById("senha").value;
    const errosenha = document.getElementById("erro-senha");
    const cpf = document.getElementById("cpf").value;
    const errocpf = document.getElementById("erro-cpf"); // elemento de erro do CPF no HTML

    // Validação de Email
    const resultadoEmail = validarEmail(email);
    if (!resultadoEmail.valido) {
        erroemail.innerText = resultadoEmail.mensagem;
        return false;
    }
    erroemail.innerText = ""; // limpa mensagem de erro se estiver correto

    // Validação de CPF 
    if (!validarCPF(cpf)) {
        if (errocpf) errocpf.innerText = "CPF inválido";
        return false;
    }
    if (errocpf) errocpf.innerText = ""; // limpa mensagem de erro se estiver correto

    // Validação de Senha
    if (senha.length < 8) {
        errosenha.innerText = "A senha deve conter no mínimo 8 caracteres";
        return false;
    }
    errosenha.innerText = ""; // limpa mensagem de erro se estiver correto

    return true; // todos os campos válidos
}

// ENVIO DO FORMULÁRIO 
if (form) {
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        // Só envia se todos os campos forem válidos
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