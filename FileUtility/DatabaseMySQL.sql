CREATE TABLE `utente` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `username` varchar(255) UNIQUE NOT NULL,
  `password` varchar(255) NOT NULL,
  `nome` varchar(255) NOT NULL,
  `cognome` varchar(255) NOT NULL
);

CREATE TABLE `cliente` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `cittadinanza` varchar(255),
  `luogo` varchar(255),
  `dataNascita` varchar(255),
  `tipoDocumento` varchar(255),
  `numDocumento` varchar(255),
  `ref_utente` integer UNIQUE NOT NULL
);

CREATE TABLE `dipendente` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `ruolo` ENUM ('CLIENTE', 'STAFF', 'AMMINISTRATORE') NOT NULL,
  `ref_sede` integer,
  `ref_utente` integer UNIQUE NOT NULL
);

CREATE TABLE `sede` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `location` varchar(255) NOT NULL,
  `tassaSoggiorno` varchar(255) NOT NULL
);

CREATE TABLE `camera` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `ref_sede` integer NOT NULL,
  `numero` varchar(255) NOT NULL,
  `postiLetto` integer NOT NULL,
  `prezzoBase` float NOT NULL,
  `tipologia` varchar(255) NOT NULL DEFAULT 'Standard',
  `status` ENUM ('LIBERA', 'OCCUPATA', 'DA_PULIRE') NOT NULL,
  `luce` boolean NOT NULL DEFAULT 0,
  `tapparelle` boolean NOT NULL DEFAULT 0,
  `temperatura` float NOT NULL DEFAULT 18
);

CREATE TABLE `service` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `nome` varchar(255) UNIQUE NOT NULL,
  `costo` float NOT NULL
);

CREATE TABLE `sede_service` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `ref_sede` integer,
  `ref_service` integer
);

CREATE TABLE `multimedia` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `costo` float NOT NULL,
  `immagine` varchar(255),
  `descrizione` varchar(255) COMMENT 'length=1000',
  `voto` double NOT NULL
);

CREATE TABLE `prenotazione` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `ref_cliente` integer NOT NULL,
  `ref_camera` integer NOT NULL,
  `dataInizio` date NOT NULL,
  `dataFine` date NOT NULL,
  `costo` float NOT NULL,
  `numeroOspiti` integer NOT NULL DEFAULT 1,
  `stato` ENUM ('CONFERMATA', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLATA') NOT NULL
);

CREATE TABLE `prenotazione_service` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `ref_prenotazione` integer,
  `ref_service` integer
);

CREATE TABLE `prenotazione_multimedia` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `ref_prenotazione` integer,
  `ref_multimedia` integer
);

CREATE TABLE `ospite` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `cognome` varchar(255) NOT NULL,
  `cittadinanza` varchar(255) NOT NULL,
  `luogo` varchar(255) NOT NULL,
  `dataNascita` date NOT NULL,
  `ref_prenotazione` integer NOT NULL
);

CREATE TABLE `nota` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `testo` varchar(255) NOT NULL COMMENT 'length=1000',
  `dataCreazione` timestamp NOT NULL,
  `ref_prenotazione` integer NOT NULL
);

ALTER TABLE `cliente` ADD FOREIGN KEY (`ref_utente`) REFERENCES `utente` (`id`);

ALTER TABLE `dipendente` ADD FOREIGN KEY (`ref_sede`) REFERENCES `sede` (`id`);

ALTER TABLE `dipendente` ADD FOREIGN KEY (`ref_utente`) REFERENCES `utente` (`id`);

ALTER TABLE `camera` ADD FOREIGN KEY (`ref_sede`) REFERENCES `sede` (`id`);

ALTER TABLE `sede_service` ADD FOREIGN KEY (`ref_sede`) REFERENCES `sede` (`id`);

ALTER TABLE `sede_service` ADD FOREIGN KEY (`ref_service`) REFERENCES `service` (`id`);

ALTER TABLE `prenotazione` ADD FOREIGN KEY (`ref_cliente`) REFERENCES `cliente` (`id`);

ALTER TABLE `prenotazione` ADD FOREIGN KEY (`ref_camera`) REFERENCES `camera` (`id`);

ALTER TABLE `prenotazione_service` ADD FOREIGN KEY (`ref_prenotazione`) REFERENCES `prenotazione` (`id`);

ALTER TABLE `prenotazione_service` ADD FOREIGN KEY (`ref_service`) REFERENCES `service` (`id`);

ALTER TABLE `prenotazione_multimedia` ADD FOREIGN KEY (`ref_prenotazione`) REFERENCES `prenotazione` (`id`);

ALTER TABLE `prenotazione_multimedia` ADD FOREIGN KEY (`ref_multimedia`) REFERENCES `multimedia` (`id`);

ALTER TABLE `ospite` ADD FOREIGN KEY (`ref_prenotazione`) REFERENCES `prenotazione` (`id`);

ALTER TABLE `nota` ADD FOREIGN KEY (`ref_prenotazione`) REFERENCES `prenotazione` (`id`);
