// Mostrar/ocultar senha - Nova Senha
    const toggleNova = document.getElementById('toggleNova');
    const inputNova = document.getElementById('novaSenha');
    toggleNova.addEventListener('click', () => {
        const tipo = inputNova.type === 'password' ? 'text' : 'password';
        inputNova.type = tipo;
        toggleNova.classList.toggle('fa-eye');
        toggleNova.classList.toggle('fa-eye-slash');
    });

    // Mostrar/ocultar senha - Confirmar Senha
    const toggleConfirmar = document.getElementById('toggleConfirmar');
    const inputConfirmar = document.getElementById('confirmarSenha');
    toggleConfirmar.addEventListener('click', () => {
        const tipo = inputConfirmar.type === 'password' ? 'text' : 'password';
        inputConfirmar.type = tipo;
        toggleConfirmar.classList.toggle('fa-eye');
        toggleConfirmar.classList.toggle('fa-eye-slash');
    });
