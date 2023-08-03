package es.nmc.espublico.importorders.dto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResumenConteos {
    private Map<String, Long> conteoPorRegion;
    private Map<String, Long> conteoPorCountry;
    private Map<String, Long> conteoPorItemType;
    private Map<String, Long> conteoPorSalesChannel;
    private Map<String, Long> conteoPorOrderPriority;

    // Agrega más mapas para otros campos que necesites contar

    public ResumenConteos() {
        this.conteoPorRegion = new ConcurrentHashMap<>();
        this.conteoPorCountry = new ConcurrentHashMap<>();
        this.conteoPorItemType = new ConcurrentHashMap<>();
        this.conteoPorSalesChannel = new ConcurrentHashMap<>();
        this.conteoPorOrderPriority = new ConcurrentHashMap<>();
        // Inicializa los mapas para otros campos si es necesario
    }

    // Getters y setters para los mapas de conteo de cada campo
    public Map<String, Long> getConteoPorItemType() {
        return conteoPorItemType;
    }

    public void setConteoPorItemType(Map<String, Long> conteoPorItemType) {
        this.conteoPorItemType = conteoPorItemType;
    }

    public Map<String, Long> getConteoPorRegion() {
        return conteoPorRegion;
    }

    public void setConteoPorRegion(Map<String, Long> conteoPorRegion) {
        this.conteoPorRegion = conteoPorRegion;
    }

    public Map<String, Long> getConteoPorCountry() {
        return conteoPorCountry;
    }

    public void setConteoPorCountry(Map<String, Long> conteoPorCountry) {
        this.conteoPorCountry = conteoPorCountry;
    }

    public Map<String, Long> getConteoPorSalesChannel() {
        return conteoPorSalesChannel;
    }

    public void setConteoPorSalesChannel(Map<String, Long> conteoPorSalesChannel) {
        this.conteoPorSalesChannel = conteoPorSalesChannel;
    }

    public Map<String, Long> getConteoPorOrderPriority() {
        return conteoPorOrderPriority;
    }

    public void setConteoPorOrderPriority(Map<String, Long> conteoPorOrderPriority) {
        this.conteoPorOrderPriority = conteoPorOrderPriority;
    }
// ... (omitiendo los detalles para mantener el ejemplo simple)
}
