function openDetails(id) {
    const modal = document.getElementById('detailsModal');
    const body = document.getElementById('modalBody');

    modal.style.display = "flex";
    document.body.style.overflow = "hidden";
    body.innerHTML = '<p style="text-align: center;">Caricamento...</p>';

    fetch(`/api/cliente/booking/${id}`)
        .then(response => response.json())
        .then(data => {
            let html = `
                <div class="detail-section">
                    <h3>Soggiorno</h3>
                    <div class="detail-row"><span>Sede:</span> <strong>${data.sedeNome}</strong></div>
                    <div class="detail-row"><span>Camera:</span> <strong>${data.cameraNumero} (${data.cameraTipologia})</strong></div>
                    <div class="detail-row"><span>Periodo:</span> <strong>${data.checkin} - ${data.checkout}</strong></div>
                    <div class="detail-row"><span>Stato:</span> <strong>${data.stato}</strong></div>
                </div>

                <div class="detail-section">
                    <h3>Costi & Servizi</h3>
                    <div class="detail-row"><span>Camera:</span> <strong>€ ${data.costoCamera.toFixed(2)}</strong></div>
                    <div class="detail-row"><span>Servizi Extra:</span> <strong>€ ${data.costoServizi.toFixed(2)}</strong></div>
                    <div class="detail-row"><span>Multimedia:</span> <strong>€ ${data.costoMultimedia.toFixed(2)}</strong></div>
                    <div class="detail-row" style="margin-top: 10px; border-top: 1px solid #555; padding-top: 5px;">
                        <span style="font-size: 1.1rem;">Totale:</span>
                        <strong style="color: var(--primary-gold); font-size: 1.2rem;">€ ${data.costoTotale.toFixed(2)}</strong>
                    </div>
                </div>

                <div class="detail-section">
                    <h3>Servizi Inclusi</h3>
                    <div style="margin-top: 10px;">
                        ${data.servizi.length > 0 ? '<ul>' + data.servizi.map(s => `<li>${s}</li>`).join('') + '</ul>' : '<p style="color:#777; font-style:italic;">Nessun servizio extra.</p>'}
                    </div>
                </div>
            `;

            if (data.ospiti && data.ospiti.length > 0) {
                html += `
                    <div class="detail-section" style="border-bottom: none;">
                        <h3>Ospiti Registrati</h3>
                        <ul class="guest-list">
                            ${data.ospiti.map(o => `
                                <li class="guest-item">
                                    <strong>${o.nome} ${o.cognome}</strong><br>
                                    <span style="font-size: 0.8rem; color: #aaa;">${o.cittadinanza}, nato il ${o.dataNascita}</span>
                                </li>
                            `).join('')}
                        </ul>
                    </div>
                `;
            } else {
                html += `<p style="color: #aaa; font-style: italic;">Check-in non ancora effettuato.</p>`;
            }

            body.innerHTML = html;
        })
        .catch(err => {
            console.error(err);
            body.innerHTML = '<p style="color: #ff4d4d; text-align: center;">Errore nel caricamento dei dettagli.</p>';
        });
}

function closeDetails() {
    document.getElementById('detailsModal').style.display = "none";
    document.body.style.overflow = "auto";
}

let currentPrenotazioneId = null;
const notesModal = document.getElementById('notesModal');
const notesHistory = document.getElementById('notes-history');
const noteForm = document.getElementById('noteForm');

function openNotes(prenotazioneId) {
    currentPrenotazioneId = prenotazioneId;
    notesModal.style.display = "flex";
    document.body.style.overflow = "hidden";

    document.getElementById('filterFrom').value = '';
    document.getElementById('filterTo').value = '';
    document.getElementById('filterLimit').value = '10';

    loadNotes();
}

function filterNotes() {
    loadNotes();
}

function loadNotes() {
    notesHistory.innerHTML = '<p>Caricamento storico...</p>';

    const from = document.getElementById('filterFrom').value;
    const to = document.getElementById('filterTo').value;
    const limit = document.getElementById('filterLimit').value;

    let url = `/api/cliente/note/${currentPrenotazioneId}?limit=${limit}`;
    if (from) url += `&from=${from}`;
    if (to) url += `&to=${to}`;

    fetch(url)
        .then(res => res.json())
        .then(notes => {
            notesHistory.innerHTML = '';
            if (notes.length === 0) {
                notesHistory.innerHTML = '<p style="color:#777; text-align:center;">Nessuna nota trovata.</p>';
            } else {
                notes.forEach(note => {
                    const noteEl = document.createElement('div');
                    noteEl.className = 'note-item';
                    noteEl.innerHTML = `<div class="note-date">${note.data}</div><div class="note-text">${note.testo}</div>`;
                    notesHistory.appendChild(noteEl);
                });
            }
        })
        .catch(err => {
            console.error(err);
            notesHistory.innerHTML = '<p style="color: #ff4d4d;">Errore nel caricamento.</p>';
        });
}

function closeNotes() {
    notesModal.style.display = "none";
    document.body.style.overflow = "auto";
}

function sendNote(event) {
    event.preventDefault();
    const testo = document.getElementById('newNoteText').value;
    if (!testo.trim()) return;

    const formData = new FormData();
    formData.append('prenotazioneId', currentPrenotazioneId);
    formData.append('testo', testo);

    fetch('/api/cliente/note/add', {
        method: 'POST',
        body: formData
    })
    .then(res => res.json())
    .then(newNote => {
        loadNotes();
        document.getElementById('newNoteText').value = '';
    });
}

window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        closeDetails();
        closeNotes();
    }
}
