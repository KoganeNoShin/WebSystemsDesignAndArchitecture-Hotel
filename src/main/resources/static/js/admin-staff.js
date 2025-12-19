document.addEventListener('DOMContentLoaded', function () {
    // Gestione Modale
    var modal = document.getElementById("addStaffModal");
    var btn = document.getElementById("openModalBtn");
    var span = document.getElementsByClassName("close")[0];

    if (modal && btn && span) {
        btn.onclick = function() {
            modal.style.display = "block";
        }

        span.onclick = function() {
            modal.style.display = "none";
        }

        window.onclick = function(event) {
            if (event.target == modal) {
                modal.style.display = "none";
            }
        }

        // Se ci sono errori nel form (rilevati da Thymeleaf), apri il modale automaticamente
        if (document.querySelector('.error-message')) {
            modal.style.display = "block";
        }
    }
});
