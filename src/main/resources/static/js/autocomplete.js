document.addEventListener('DOMContentLoaded', function () {

    const countryToJSON = {
        "Italiana": "italia.json",
        "Francese": "francia.json",
        "Tedesca": "germania.json",
        "Spagnola": "spagna.json",
        "Statunitense": "usa.json"
    };

    function setupAutocomplete(cittadinanzaElement, luogoElement) {
        if (!cittadinanzaElement || !luogoElement) return;

        let cityList = [];
        const suggestions = document.createElement('div');
        suggestions.classList.add('suggestions-box');
        suggestions.style.position = 'absolute';
        suggestions.style.backgroundColor = '#2c2c2c';
        suggestions.style.border = '1px solid #444';
        suggestions.style.width = '100%';
        suggestions.style.maxHeight = '150px';
        suggestions.style.overflowY = 'auto';
        suggestions.style.zIndex = '1000';

        luogoElement.parentNode.style.position = 'relative';
        luogoElement.parentNode.appendChild(suggestions);

        function loadCities(country) {
            const jsonFile = countryToJSON[country];
            if (jsonFile) {
                luogoElement.disabled = false;
                luogoElement.placeholder = 'Inizia a digitare...';
                fetch(`/json/${jsonFile}`)
                    .then(response => response.json())
                    .then(data => {
                        cityList = data;
                    })
                    .catch(error => console.error(`Errore nel caricamento di ${jsonFile}:`, error));
            } else {
                cityList = [];
                if (country && country !== 'Seleziona...') {
                    luogoElement.disabled = false;
                    luogoElement.placeholder = 'Inserisci manualmente';
                } else {
                    luogoElement.disabled = true;
                    luogoElement.value = '';
                    luogoElement.placeholder = 'Seleziona prima la cittadinanza';
                }
            }
        }

        cittadinanzaElement.addEventListener('change', function() {
            const selectedCountry = this.value;
            luogoElement.value = '';
            loadCities(selectedCountry);
        });

        luogoElement.addEventListener('input', function () {
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
                suggestionItem.style.padding = '10px';
                suggestionItem.style.cursor = 'pointer';
                suggestionItem.style.color = '#fff';

                suggestionItem.addEventListener('mouseover', () => {
                    suggestionItem.style.backgroundColor = '#c5a059';
                    suggestionItem.style.color = '#000';
                });
                suggestionItem.addEventListener('mouseout', () => {
                    suggestionItem.style.backgroundColor = 'transparent';
                    suggestionItem.style.color = '#fff';
                });

                suggestionItem.addEventListener('click', function () {
                    luogoElement.value = this.textContent;
                    suggestions.innerHTML = '';
                });
                suggestions.appendChild(suggestionItem);
            });
        });

        document.addEventListener('click', function (e) {
            if (e.target !== luogoElement) {
                suggestions.innerHTML = '';
            }
        });

        if (cittadinanzaElement.value) {
            loadCities(cittadinanzaElement.value);
        } else {
            luogoElement.disabled = true;
        }
    }

    const singleCit = document.getElementById('cittadinanza');
    const singleLuogo = document.getElementById('luogoNascita');
    if (singleCit && singleLuogo) {
        setupAutocomplete(singleCit, singleLuogo);
    }

    const citInputs = document.querySelectorAll('.input-cittadinanza');
    const luogoInputs = document.querySelectorAll('.input-luogo');

    for (let i = 0; i < citInputs.length; i++) {
        if (luogoInputs[i]) {
            setupAutocomplete(citInputs[i], luogoInputs[i]);
        }
    }
});
