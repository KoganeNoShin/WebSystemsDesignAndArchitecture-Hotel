function togglePassword(inputId, iconElement) {
    const input = document.getElementById(inputId);
    if (input.type === "password") {
        input.type = "text";
        iconElement.textContent = "👁️";
        iconElement.style.color = "#c5a059";
    } else {
        input.type = "password";
        iconElement.textContent = "👁️‍🗨️";
        iconElement.style.color = "#aaa";
    }
}
