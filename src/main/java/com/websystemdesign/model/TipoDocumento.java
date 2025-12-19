package com.websystemdesign.model;

public enum TipoDocumento {
    CARTA_DI_IDENTITA("Carta di Identità"),
    PASSAPORTO("Passaporto"),
    PATENTE("Patente");

    private final String descrizione;

    TipoDocumento(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
