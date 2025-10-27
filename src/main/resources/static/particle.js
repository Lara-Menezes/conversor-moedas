/**
 * EFEITO DE PARTÍCULAS ($) NO FUNDO
 */
const canvas = document.getElementById('moneyParticles');
const ctx = canvas.getContext('2d');
canvas.width = window.innerWidth;
canvas.height = window.innerHeight;

const particles = [];
const total = 80;

for (let i = 0; i < total; i++) {
    particles.push({
        x: Math.random() * canvas.width,
        y: Math.random() * canvas.height,
        size: 14 + Math.random() * 10,
        speedY: 0.3 + Math.random() * 1,
        opacity: 0.4 + Math.random() * 0.6
    });
}

function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    particles.forEach(p => {
        ctx.font = `${p.size}px Arial`;
        ctx.fillStyle = `rgba(255, 255, 255, ${p.opacity})`;
        ctx.fillText('$', p.x, p.y);
    });
}

function update() {
    particles.forEach(p => {
        p.y += p.speedY;
        if (p.y > canvas.height + 20) {
            p.y = -20;
            p.x = Math.random() * canvas.width;
        }
    });
}

function animate() {
    draw();
    update();
    requestAnimationFrame(animate);
}

animate();

window.addEventListener('resize', () => {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
});