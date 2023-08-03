package es.nmc.espublico.importorders.dto;

import java.util.List;
import java.util.Map;

public class PageOrderDTO {
    private long page;
    private List<OrderDTO> content;
    private Map<String, LinkDTO> links;

    // Getters and Setters

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public List<OrderDTO> getContent() {
        return content;
    }

    public void setContent(List<OrderDTO> content) {
        this.content = content;
    }

    public Map<String, LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(Map<String, LinkDTO> links) {
        this.links = links;
    }
}
