document.addEventListener('DOMContentLoaded', function () {
    const input = document.getElementById('luogoNascita');
    if (input) {
        const suggestions = document.createElement('div');
        suggestions.setAttribute('id', 'suggestions');
        input.parentNode.appendChild(suggestions);

        let comuni = [];

        // Carica i comuni dal file JSON
        fetch('/json/comuni.json')
            .then(response => response.json())
            .then(data => {
                comuni = data;
            })
            .catch(error => console.error('Errore nel caricamento dei comuni:', error));

        input.addEventListener('input', function () {
            const value = this.value.toLowerCase();
            suggestions.innerHTML = '';
            if (!value) return;

            const filteredComuni = comuni.filter(comune =>
                comune.toLowerCase().startsWith(value)
            );

            filteredComuni.slice(0, 10).forEach(comune => { // Mostra solo i primi 10 risultati
                const suggestionItem = document.createElement('div');
                suggestionItem.classList.add('suggestion-item');
                suggestionItem.textContent = comune;
                suggestionItem.addEventListener('click', function () {
                    input.value = this.textContent;
                    suggestions.innerHTML = '';
                });
                suggestions.appendChild(suggestionItem);
            });
        });

        document.addEventListener('click', function (e) {
            if (e.target !== input) {
                suggestions.innerHTML = '';
            }
        });
    }
});
