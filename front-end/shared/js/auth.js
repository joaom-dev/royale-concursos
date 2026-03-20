const form = document.querySelector("form");

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.querySelector("#email").value;
    const password = document.querySelector("#senha").value;

    try {
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        if (response.ok) {
            const data = await response.json();

            localStorage.setItem("token", data.token);

            window.location.href = "../telainicial/index.html";
        } else {
            alert("Email ou senha inválidos");
        }

    } catch (error) {
        console.error(error);
        alert("Erro ao fazer login");
    }
});
