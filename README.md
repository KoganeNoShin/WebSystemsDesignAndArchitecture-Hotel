# 🏨 WebSystemsDesignAndArchitecture-Hotel

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-MVC-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)

## 📄 Descrizione del Progetto
Progetto finale per il corso di **Web Systems Design and Architecture** (A.A. 2025/2026).
Una piattaforma Enterprise Full-Stack che digitalizza l'intera esperienza alberghiera, trasformando il soggiorno in un'esperienza "Phygital" (Fisico + Digitale).

Il sistema non è un semplice CRUD, ma un motore di gestione complesso che orchestra:
* **Booking Engine Pubblico** con calcolo prezzi dinamico.
* **Automazione dei Processi** (No-Show, Housekeeping) tramite Schedulers.
* **Integrazione IoT** per il controllo domotico della stanza.
* **Compliance Legale** con generazione report XML per la Pubblica Amministrazione.



[Image of hotel management system architecture diagram]


---

### 👨‍💻 **Diego Corona**
**Ruolo: Guest Experience, Public Frontend & Automation Core.**
Responsabile dell'intera interfaccia pubblica e cliente, oltre che del motore di automazione temporale:
* **Public Booking Engine:** Sviluppo Full-Stack del flusso di prenotazione, algoritmi di ricerca disponibilità e calcolo preventivi in tempo reale.
* **Area Riservata & Smart Check-in:** Gestione dashboard cliente, validazione documenti, logiche di pagamento e calcolo penali.
* **Automation Core (Schedulers):** Progettazione e implementazione dei `Cron Jobs` critici (Backend & Integration) per la gestione automatica degli stati:
    * *No-Show Killer:* Cancellazione automatica prenotazioni non onorate.
    * *Auto-Housekeeping:* Trigger automatico pulizia camere.
* **Frontend Logic:** Sviluppo degli script JavaScript lato client (`dashboard.js`, `autocomplete.js`) per l'interattività asincrona.

### 👨‍💻 **Simone Comitini**
**Ruolo: Admin Backend, Staff Operations & Infrastructure.**
Responsabile della gestione interna, amministrativa e del database:
* **Admin Panel:** Sviluppo del pannello di controllo per la gestione Sedi, Dipendenti e Camere.
* **Staff Workflow:** Implementazione della dashboard operativa per il personale (Housekeeping e gestione stati).
* **Legal Reporting:** Modulo di generazione XML (JAXB) per i report Questura e Tassa di Soggiorno.

---

## 🏗️ Architettura Tecnica

### Backend (Spring Boot 3)
L'applicazione segue una **Clean Layered Architecture**:
* **Hybrid Controller Pattern:**
    * `controller.web`: Gestisce le viste Server-Side (Thymeleaf) per la navigazione classica.
    * `controller.api`: Endpoint REST per chiamate AJAX asincrone e integrazioni IoT.
* **DTO Mapping:** Utilizzo di **MapStruct 1.5.5** per il mapping Entity-DTO, garantendo disaccoppiamento e performance.
* **Security:** RBAC (`Spring Security 6`) con routing dinamico post-login (`CustomAuthenticationSuccessHandler`).

### Frontend & UX
* **Dynamic Fetching:** Utilizzo di **Vanilla JS** e Fetch API per operazioni asincrone (es. modali dettagli prenotazione) senza refresh pagina.
* **Smart Autocomplete:** Sistema di suggerimento città basato su file JSON statici per migliorare la UX in fase di check-in.

---

## ✨ Funzionalità Chiave

### 🤖 Automation Core (Diego Corona)
Il sistema esegue task automatici in background per garantire l'integrità operativa:
1.  **No-Show Killer (`0 0 12 * * *`):** Alle 12:00, il `BookingCleanupScheduler` analizza i check-in mancanti e **cancella** automaticamente le prenotazioni, liberando la disponibilità.
2.  **Auto-Housekeeping (`0 0 11 * * *`):** Alle 11:00, il `RoomScheduler` forza lo stato delle camere in uscita su `DA_PULIRE`, aggiornando in tempo reale le dashboard dello staff.

### 💰 Smart Billing Engine
Logica di fatturazione avanzata nel `CheckOutController`:
* **Early Checkout Penalty:** Calcolo automatico della **penale del 50%** sulle notti residue in caso di partenza anticipata.
* **Tassa di Soggiorno Dinamica:** Applicazione automatica delle tariffe comunali con gestione esenzione **Under 12**.

### 🏠 IoT & Domotica (Guest Area)
Interfaccia di controllo remoto della stanza (Simulazione IoT):
* Controllo **Luci** e **Tapparelle** (Toggle On/Off).
* Regolazione **Temperatura** con validazione range (16°C - 24°C).

### 👮 Legal Reporting
Generazione automatica dei flussi dati per le autorità:
* **Schede Alloggiati (Questura):** Export dati anagrafici a norma.
* **Tassa di Soggiorno:** Report contabile periodico.

## ⚖️ Licenza e Copyright

**Copyright © 2026 - Diego Corona, Simone Comitini. Tutti i diritti riservati.**

Questo progetto è stato sviluppato esclusivamente a scopo accademico.

⚠️ **ATTENZIONE:**
L'utilizzo, la copia, la modifica, la ridistribuzione o la vendita del codice sorgente presente in questa repository, sia in forma totale che parziale, sono **severamente vietati** senza esplicita autorizzazione scritta da parte degli autori.

Ogni violazione dei diritti d'autore sarà perseguita a norma di legge.

---
**Contatti:**
* [Diego Corona - LinkedIn](https://www.linkedin.com/in/diegocorona03/)
* [Simone Comitini - LinkedIn](https://www.linkedin.com/in/simone-comitini-20274938b/)
