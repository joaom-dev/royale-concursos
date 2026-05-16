
const eyeIcon = document.querySelector('.eye-icon')
const senhaInput2 = document.getElementById('password')

if (eyeIcon && senhaInput2) {
    eyeIcon.addEventListener('click', () => {
        const type = senhaInput2.type === 'password' ? 'text' : 'password';
        senhaInput2.type = type;
        eyeIcon.classList.toggle('fa-eye-slash');
    });
}

