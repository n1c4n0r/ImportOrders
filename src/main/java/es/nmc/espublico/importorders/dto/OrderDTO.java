package es.nmc.espublico.importorders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.nmc.espublico.importorders.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderDTO {
    private long id;
    private String uuid;
    private String region;
    private String country;
    @JsonProperty(value = "item_type")
    private String itemType;
    @JsonProperty(value = "sales_channel")
    private String salesChannel;
    private PriorityEnum priority;
    private Date date;
    @JsonProperty(value = "ship_date")
    private Date shipDate;
    @JsonProperty(value = "units_sold")
    private long unitsSold;
    @JsonProperty(value = "unit_price")
    private double unitPrice;
    @JsonProperty(value = "unit_cost")
    private double unitCost;
    @JsonProperty(value = "total_revenue")
    private double totalRevenue;
    @JsonProperty(value = "total_cost")
    private double totalCost;
    @JsonProperty(value = "total_profit")
    private double totalProfit;

    // Getters and Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public void setDate(String dateString) {
        this.date = Util.convertStringDDMMYYYYtoDate(dateString);
    }


    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }
    public void setShipDate(String dateShipString) { this.shipDate = Util.convertStringDDMMYYYYtoDate(dateShipString);}

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