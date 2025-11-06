const form = document.getElementById("converter-form");
const valueInput = document.getElementById("valueConvertido");
const resultInput = document.getElementById("resultValueConvertido");
const fromSelect = document.getElementById("from-convert");
const toSelect = document.getElementById("to-convert");
const inverterBtn = document.getElementById("invertevalues");
const convertBtn = document.getElementById("convertvalues");

valueInput.focus();

// Carrega moedas do backend
async function carregarMoedas() {
    try {
        const response = await fetch('http://localhost:8080/api/currencies');
        if (!response.ok) throw new Error("Erro ao carregar moedas");

        const data = await response.json();
        fromSelect.innerHTML = '';
        toSelect.innerHTML = '';

        for (const [codigo, nome] of Object.entries(data)) {
            const optFrom = document.createElement('option');
            optFrom.value = codigo;
            optFrom.textContent = `${codigo} - ${nome}`;
            fromSelect.appendChild(optFrom);

            const optTo = optFrom.cloneNode(true);
            toSelect.appendChild(optTo);
        }

        fromSelect.value = 'BRL';
        toSelect.value = 'USD';
    } catch (err) {
        console.error("Erro ao carregar moedas:", err);
    }
}

// Função de conversão via backend
async function converter() {
    const valor = parseFloat(valueInput.value);
    const de = fromSelect.value;
    const para = toSelect.value;

    if (!valor || valor <= 0) {
        alert("Digite um valor válido!");
        return;
    }

    convertBtn.disabled = true;
    convertBtn.textContent = "Carregando...";
    resultInput.value = "Carregando...";

    try {
        const response = await fetch(`http://localhost:8080/api/convert?from=${de}&to=${para}&amount=${valor}`);
        if (!response.ok) throw new Error("Erro na conversão");

        const data = await response.json();
        const resultado = data.convertedAmount ?? data.resultado ?? data.convertedValue ?? data.result ?? data.valorConvertido;


        if (!resultado) throw new Error("Sem resultado");

        resultInput.value = resultado.toFixed(2);
        resultInput.classList.add("success");

        // Mostrar card de resultado
        const resultadoCard = document.getElementById('resultado-card');
        const textoResultado = document.getElementById('textoResultado');
        resultadoCard.classList.add('show');
        textoResultado.textContent = `${valor} ${de} = ${resultado.toFixed(2)} ${para}`;

        // Gerar gráfico
        gerarGraficoCambio(de, para);
    } catch (err) {
        console.error("Erro:", err);
        resultInput.value = "Erro na conversão";
    } finally {
        convertBtn.disabled = false;
        convertBtn.textContent = "CONVERTER";
        setTimeout(() => resultInput.classList.remove("success"), 1500);
    }
}

function gerarGraficoCambio(de, para) {
    const ctx = document.getElementById('graficoCambio').getContext('2d');
    if (window.cambioChart) window.cambioChart.destroy();

    const dias = ['Seg', 'Ter', 'Qua', 'Qui', 'Sex'];
    const taxas = dias.map(() => (Math.random() * (5.5 - 4.8) + 4.8).toFixed(2));

    window.cambioChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: dias,
            datasets: [{
                label: `Taxa de câmbio ${de} → ${para}`,
                data: taxas,
                borderColor: 'white',
                borderWidth: 2,
                fill: false,
                tension: 0.3
            }]
        },
        options: {
            scales: {
                y: {
                    ticks: { color: 'white' },
                    grid: { color: 'rgba(255,255,255,0.1)' }
                },
                x: {
                    ticks: { color: 'white' },
                    grid: { color: 'rgba(255,255,255,0.1)' }
                }
            },
            plugins: {
                legend: { labels: { color: 'white' } }
            }
        }
    });
}

form.addEventListener("submit", (e) => {
    e.preventDefault();
    converter();
});

inverterBtn.addEventListener("click", (e) => {
    e.preventDefault();
    const temp = fromSelect.value;
    fromSelect.value = toSelect.value;
    toSelect.value = temp;
    converter();
});

window.addEventListener('DOMContentLoaded', carregarMoedas);
