document.addEventListener("DOMContentLoaded", async () => {
    const moedaDe = localStorage.getItem("moedaDe");
    const moedaPara = localStorage.getItem("moedaPara");

    if (!moedaDe || !moedaPara) {
        alert("Nenhum relatório registrado ainda!");
        return;
    }

    console.log(`Exibindo relatório de ${moedaDe} → ${moedaPara}`);

    try {
        const response = await fetch(`http://localhost:8080/api/relatorios/${moedaPara}`);
        if (!response.ok) throw new Error("Erro ao carregar relatório");

        const relatorios = await response.json();
        console.log("Relatórios recebidos:", relatorios);

        const tabela = document.getElementById("tabela-relatorios");
        if (tabela) {
            relatorios.forEach(r => {
                // Calcula crescimento, sinal e cor
                const crescimento = (r.taxaCrescimento ?? 0) * 100;
                const sinal = crescimento > 0 ? "↑" : (crescimento < 0 ? "↓" : "—");
                const cor = crescimento > 0 ? "green" : (crescimento < 0 ? "red" : "gray");

                // Cria a linha da tabela
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${r.moeda}</td>
                    <td>${r.mediaSemana.toFixed(4)}</td>
                    <td>${r.valorAtual.toFixed(4)}</td>
                    <td style="color:${cor}">${crescimento.toFixed(4)}% ${sinal}</td>
                    <td>${r.inicioSemana} → ${r.fimSemana}</td>
                `;
                tabela.appendChild(row);
            });

        }

    } catch (err) {
        console.error("Erro ao carregar relatórios:", err);
    }
});
