/**
 * LÓGICA DE CONVERSÃO COM BACKEND JAVA (Spring Boot)
 */
const form = document.getElementById("converter-form");
const valueInput = document.getElementById("valueConvertido");
const resultInput = document.getElementById("resultValueConvertido");
const fromSelect = document.getElementById("from-convert");
const toSelect = document.getElementById("to-convert");
const inverterBtn = document.getElementById("invertevalues");

// Inverter as moedas
inverterBtn.addEventListener("click", (e) => {
    e.preventDefault();
    const temp = fromSelect.value;
    fromSelect.value = toSelect.value;
    toSelect.value = temp;
});

// Enviar para o backend Spring Boot
form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const valor = parseFloat(valueInput.value);
    const de = fromSelect.value;
    const para = toSelect.value;

    if (!valor || valor <= 0) {
        alert("Digite um valor válido!");
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/convert", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ valor, de, para })
        });

        if (!response.ok) throw new Error("Erro na conversão");

        const data = await response.json();

        if (data.resultado !== undefined) {
            resultInput.value = data.resultado.toFixed(2);
        } else {
            resultInput.value = "Erro na conversão";
        }

    } catch (error) {
        console.error("Erro:", error);
        resultInput.value = "Erro ao conectar com o servidor";
    }
});
