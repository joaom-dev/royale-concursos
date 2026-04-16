const cards = document.querySelectorAll(".card");
const dots = document.querySelectorAll(".dot");
let index = 0;

function mostrarSlide(i) {
  cards.forEach(card => card.classList.remove("ativo"));
  dots.forEach(dot => dot.classList.remove("ativo"));

  cards[i].classList.add("ativo");
  dots[i].classList.add("ativo");
}

function proximoSlide() {
  index++;
  if (index >= cards.length) index = 0;
  mostrarSlide(index);
}

function slideAnterior() {
  index--;
  if (index < 0) index = cards.length - 1;
  mostrarSlide(index);
}

document.getElementById("next").addEventListener("click", proximoSlide);
document.getElementById("prev").addEventListener("click", slideAnterior);

setInterval(proximoSlide, 4500);