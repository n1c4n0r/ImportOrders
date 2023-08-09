package es.nmc.espublico.importorders.util;

import es.nmc.espublico.importorders.dto.OrderDTO;
import es.nmc.espublico.importorders.dto.PriorityEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UtilTest {
    public static List<OrderDTO> getDataOrderDto(){
        OrderDTO order1 = new OrderDTO();
        order1.setId(1);
        order1.setUuid("abc123");
        order1.setRegion("Europe");
        order1.setCountry("Spain");
        order1.setItemType("Electronics");
        order1.setSalesChannel("Online");
        order1.setPriority(PriorityEnum.L);
        order1.setDate(new Date()); // Use the current date
        order1.setShipDate("08-08-2023"); // Use a specific ship date string
        order1.setUnitsSold(100);
        order1.setUnitPrice(50.0);
        order1.setUnitCost(30.0);
        order1.setTotalRevenue(order1.getUnitsSold() * order1.getUnitPrice());
        order1.setTotalCost(order1.getUnitsSold() * order1.getUnitCost());
        order1.setTotalProfit(order1.getTotalRevenue() - order1.getTotalCost());

        OrderDTO order2 = new OrderDTO();
        order2.setId(2);
        order2.setUuid("def456");
        order2.setRegion("North America");
        order2.setCountry("USA");
        order2.setItemType("Clothing");
        order2.setSalesChannel("Retail");
        order2.setPriority(PriorityEnum.C);
        order2.setDate(new Date()); // Use the current date
        order2.setShipDate("08-10-2023"); // Use a specific ship date string
        order2.setUnitsSold(200);
        order2.setUnitPrice(25.0);
        order2.setUnitCost(15.0);
        order2.setTotalRevenue(order2.getUnitsSold() * order2.getUnitPrice());
        order2.setTotalCost(order2.getUnitsSold() * order2.getUnitCost());
        order2.setTotalProfit(order2.getTotalRevenue() - order2.getTotalCost());

        List<OrderDTO> listOrderDTO = new ArrayList<>();
        listOrderDTO.add(order1);
        listOrderDTO.add(order2);
        return listOrderDTO;
    }
}
