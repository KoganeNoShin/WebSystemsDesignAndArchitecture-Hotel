function togglePassword(inputId, iconElement) {
    const input = document.getElementById(inputId);
    if (input.type === "password") {
        input.type = "text";
        iconElement.textContent = "👁️"; // Occhio aperto
        iconElement.style.color = "#c5a059";
    } else {
        input.type = "password";
        iconElement.textContent = "👁️‍🗨️"; // Occhio sbarrato o diverso
        iconElement.style.color = "#aaa";
    }
}
