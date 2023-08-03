package es.nmc.espublico.importorders.dto;

public class LinkDTO {
    private String self;
    public LinkDTO() {
        // Constructor vacío necesario para la deserialización por Jackson
    }

    public LinkDTO(String self) {
        this.self = self;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String description) {
        this.self = description;
    }
}