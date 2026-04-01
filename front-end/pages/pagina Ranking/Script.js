// Dados iniciais pré-definidos (exemplo da imagem)
const initialUsers = [
  {
    initials: "AC",
    username: "Ana Carolina",
    contest: "Concursos do Banco do Brasil",
    score: 9850,
    corrects: "98/100",
  }

];

let users = [...initialUsers];
let totalUsers = 12450; // Usuários totais para mostrar no resumo
let contestsParticipated = 23; // Exemplo fixo, poderia contar concursos únicos no futuro

const rankingTableBody = document.querySelector("#rankingTable tbody");
const top3Div = document.querySelector("#top3Users");
const posicaoAtualCard = document.querySelector("#posicaoAtualCard");
const melhorPontuacaoCard = document.querySelector("#melhorPontuacaoCard");
const concursosParticipadosCard = document.querySelector("#concursosParticipadosCard");

const form = document.getElementById("userForm");

function updateTable() {
  // Ordenar por pontuação decrescente
  users.sort((a, b) => b.score - a.score);

  rankingTableBody.innerHTML = "";

  users.forEach((user, index) => {
    const position = index + 1;

    // Classes para medalhas
    let positionClass = "";
    let medalIcon = "";
    if (position === 1) {
      positionClass = "position-1";
      medalIcon = "🏅";
    } else if (position === 2) {
      positionClass = "position-2";
      medalIcon = "🥈";
    } else if (position === 3) {
      positionClass = "position-3";
      medalIcon = "🥉";
    }

    // Formatando pontuação com vírgula e 3 casas
    let scoreStr = (user.score / 1000).toFixed(3).replace('.', ',');

    const tr = document.createElement("tr");
    tr.classList.add(positionClass);
    tr.innerHTML = `
      <td><span class="medal">${medalIcon}</span> ${position}º</td>
      <td><span class="avatar-circle">${user.initials.toUpperCase()}</span> ${user.username}</td>
      <td>${user.contest}</td>
      <td class="score">${scoreStr}</td>
      <td>${user.corrects}</td>

    `;

    rankingTableBody.appendChild(tr);
  });

  updateTop3();
  updateSummary();
}

function updateTop3() {
  const top3 = users.slice(0, 3);
  top3Div.innerHTML = "";

  top3.forEach((user, i) => {
    const position = i + 1;
    let medalIcon = "";
    if (position === 1) medalIcon = "🏅";
    else if (position === 2) medalIcon = "🥈";
    else if (position === 3) medalIcon = "🥉";

    // Calcular % da maior pontuação
    const maxScore = users[0].score;
    const percent = ((user.score / maxScore) * 100).toFixed(1);

    // Formatar pontuação
    let scoreStr = (user.score / 1000).toFixed(3).replace('.', ',');

    const div = document.createElement("div");
    div.classList.add("top-user");
    div.innerHTML = `
      <span class="medal">${medalIcon}</span>
      <div class="avatar-large">${user.initials.toUpperCase()}</div>
      <p class="user-name">${user.username}</p>
      <p class="user-score">${scoreStr}</p>
      <div class="progress-bar">
        <div class="progress" style="width: ${percent}%"></div>
      </div>
      <small>${percent}% do máximo</small>
    `;
    top3Div.appendChild(div);
  });
}

function updateSummary() {
  // Vamos considerar o último usuário como o "Usuário Atual"
  const currentUser = users[users.length - 1];
  // Posição atual (index + 1)
  const positionAtual = users.findIndex(u => u === currentUser) + 1;

  // Melhor pontuação
  let melhorPontuacao = users[0];

  posicaoAtualCard.querySelector("h3").textContent = `${positionAtual}º`;
  posicaoAtualCard.querySelector("small").textContent = `Entre ${totalUsers.toLocaleString('pt-BR')} usuários`;

  melhorPontuacaoCard.querySelector("h3").textContent = (melhorPontuacao.score / 1000).toFixed(3).replace('.', ',');
  melhorPontuacaoCard.querySelector("small").textContent = melhorPontuacao.contest;

  concursosParticipadosCard.querySelector("h3").textContent = contestsParticipated;
}

// Validação e submissão do formulário
form.addEventListener("submit", (e) => {
  e.preventDefault();

  const username = form.username.value.trim();
  const initials = form.initials.value.trim().toUpperCase();
  const contest = form.contest.value;
  const score = parseInt(form.score.value, 10);
  const corrects = form.corrects.value.trim();

  // Verifique se as iniciais têm 2 caracteres e outras validações já feitas pelo HTML

  // Adicionar ao array
  users.push({
    username,
    initials,
    contest,
    score,
    corrects,

  });

  // Atualizar tabela e demais áreas
  updateTable();

  // Limpar formulário
  form.reset();
});

updateTable();