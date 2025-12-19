document.addEventListener('DOMContentLoaded', function () {
    const cittadinanzaSelect = document.getElementById('cittadinanza');
    const luogoNascitaInput = document.getElementById('luogoNascita');

    if (cittadinanzaSelect && luogoNascitaInput) {
        const suggestions = document.createElement('div');
        suggestions.setAttribute('id', 'suggestions');
        luogoNascitaInput.parentNode.appendChild(suggestions);

        let cityList = [];

        const countryToJSON = {
            "Italiana": "italia.json",
            "Francese": "francia.json",
            "Tedesca": "germania.json",
            "Spagnola": "spagna.json",
            "Statunitense": "usa.json"
        };

        function loadCities(country) {
            const jsonFile = countryToJSON[country];
            if (jsonFile) {
                luogoNascitaInput.disabled = false;
                fetch(`/json/${jsonFile}`)
                    .then(response => response.json())
                    .then(data => {
                        cityList = data;
                    })
                    .catch(error => console.error(`Errore nel caricamento di ${jsonFile}:`, error));
            } else {
                cityList = [];
                luogoNascitaInput.disabled = true;
                luogoNascitaInput.value = 'N/A per questa cittadinanza';
            }
        }

        cittadinanzaSelect.addEventListener('change', function() {
            const selectedCountry = this.value;
            luogoNascitaInput.value = ''; // Pulisce l'input al cambio
            loadCities(selectedCountry);
        });

        luogoNascitaInput.addEventListener('input', function () {
            const value = this.value.toLowerCase();
            suggestions.innerHTML = '';
            if (!value || cityList.length === 0) return;

            const filteredCities = cityList.filter(city =>
                city.toLowerCase().startsWith(value)
            );

            filteredCities.slice(0, 10).forEach(city => {
                const suggestionItem = document.createElement('div');
                suggestionItem.classList.add('suggestion-item');
                suggestionItem.textContent = city;
                suggestionItem.addEventListener('click', function () {
                    luogoNascitaInput.value = this.textContent;
                    suggestions.innerHTML = '';
                });
                suggestions.appendChild(suggestionItem);
            });
        });

        document.addEventListener('click', function (e) {
            if (e.target !== luogoNascitaInput) {
                suggestions.innerHTML = '';
            }
        });

        // Carica la lista iniziale se una cittadinanza è già selezionata
        if (cittadinanzaSelect.value) {
            loadCities(cittadinanzaSelect.value);
        } else {
            luogoNascitaInput.disabled = true;
            luogoNascitaInput.placeholder = 'Seleziona prima la cittadinanza';
        }
    }
});
