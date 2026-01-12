document.addEventListener('DOMContentLoaded', function () {
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

        if (document.querySelector('.error-message')) {
            modal.style.display = "block";
        }
    }
});
