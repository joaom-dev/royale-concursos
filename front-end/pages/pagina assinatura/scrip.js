// ABRIR TELA DO PAGAMENTO

const modal = document.getElementById('modal-mensal')
const modal2 = document.getElementById('modal-vita')

openModal.addEventListener('click', () => {
    modal.showModal()
    document.body.classList.add('modal-open')
})

openModal2.addEventListener('click', () => {
    modal2.showModal()
    document.body.classList.add('modal-open')
})

// FECHAR TELA DO PAGAMENTO

document.getElementById('closeModalMensal').addEventListener('click', () => {
    modal.close()
    document.body.classList.remove('modal-open')
})

document.getElementById('closeModalVita').addEventListener('click', () => {
    modal2.close()
    document.body.classList.remove('modal-open')
})

// SELECIONAR MÉTODO DE PAGAMENTO

function selecionarMetodo(el, seletor, modalId) {
    // Marca botão ativo
    const container = el.closest(seletor)
    container.querySelectorAll('.metodo-mensal, .metodo-vita').forEach(btn => btn.classList.remove('ativo'))
    el.classList.add('ativo')

    // Pega o método selecionado
    const metodo = el.dataset.metodo // 'pix', 'credito' ou 'debito'

    // Esconde todas as seções de conteúdo do modal
    const secoes = ['pix', 'credito', 'debito']
    secoes.forEach(s => {
        const el = document.getElementById(`${modalId}-${s}`)
        if (el) el.style.display = 'none'
    })

    // Mostra só a seção correta
    const alvo = document.getElementById(`${modalId}-${metodo}`)
    if (alvo) alvo.style.display = 'block'
}

// COPIAR CHAVE PIX

function copiarChave(btn) {
    const chave = btn.closest('.pix-chave-box').querySelector('.pix-chave-texto').textContent
    navigator.clipboard.writeText(chave).then(() => {
        const original = btn.innerHTML
        btn.innerHTML = '<i class="fa-solid fa-check"></i> Copiado!'
        btn.disabled = true
        setTimeout(() => {
            btn.innerHTML = original
            btn.disabled = false
        }, 2000)
    })
}

// INICIALIZAR — esconde crédito/débito, mostra PIX por padrão

document.addEventListener('DOMContentLoaded', () => {
    ['mensal', 'vita'].forEach(modalId => {
        document.getElementById(`${modalId}-credito`).style.display = 'none'
        document.getElementById(`${modalId}-debito`).style.display = 'none'
        document.getElementById(`${modalId}-pix`).style.display = 'block'
    })
})