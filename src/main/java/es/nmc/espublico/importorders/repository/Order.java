package es.nmc.espublico.importorders.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.nmc.espublico.importorders.dto.PriorityEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders") // Usamos "orders" en lugar de "order" para evitar problemas con palabras reservadas
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uuid;
    private String region;
    private String country;
    private String itemType;
    private String salesChannel;
    private PriorityEnum priority;
    private String date;
    private String shipDate;
    private long unitsSold;
    private double unitPrice;
    private double unitCost;
    private double totalRevenue;
    private double totalCost;
    private double totalProfit;

    // Constructor vacío (necesario para JPA)
    public Order() {}

    // Constructor con todos los atributos
    public Order(String uuid, String region, String country, String itemType, String salesChannel, PriorityEnum priority,
                 String date, String shipDate, long unitsSold, double unitPrice, double unitCost,
                 double totalRevenue, double totalCost, double totalProfit) {
        this.region = region;
        this.uuid = uuid;
        this.country = country;
        this.itemType = itemType;
        this.salesChannel = salesChannel;
        this.priority = priority;
        this.date = date;
        this.shipDate = shipDate;
        this.unitsSold = unitsSold;
        this.unitPrice = unitPrice;
        this.unitCost = unitCost;
        this.totalRevenue = totalRevenue;
        this.totalCost = totalCost;
        this.totalProfit = totalProfit;
    }

    // Getters y setters para todos los atributos

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
    }

    public PriorityEnum getPriority() {
        return priority;
    }

    public void setPriority(PriorityEnum priority) {
        this.priority = priority;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShipDate() {
        return shipDate;
    }

    public void setShipDate(String shipDate) {
        this.shipDate = shipDate;
    }

    public long getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(long unitsSold) {
        this.unitsSold = unitsSold;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
    }
}

