package es.nmc.espublico.importorders.service;

import com.opencsv.CSVWriter;
import es.nmc.espublico.importorders.dto.OrderDTO;
import es.nmc.espublico.importorders.dto.ResumenConteos;
import es.nmc.espublico.importorders.repository.Order;
import es.nmc.espublico.importorders.repository.OrderRepository;
import es.nmc.espublico.importorders.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<ResumenConteos> processOrdersInBatch(List<OrderDTO> orderDTOs) {
        long startTime = System.currentTimeMillis(); // Registro del tiempo inicial
        try {
            List<Order> orders = orderDTOs.stream()
                    .map(this::convertToEntity)
                    .toList();
            orderRepository.saveAll(orders);

            long endTime = System.currentTimeMillis(); // Registro del tiempo final
            long totalTime = endTime - startTime;
            logger.info("Tiempo de procesamiento de lote: {} ms", totalTime);


            // Obtener los conteos por diferentes campos de este lote de órdenes
            ResumenConteos result = new ResumenConteos();
            result.setConteoPorRegion(orders.stream()
                    .map(Order::getRegion)
                    .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting())));
            result.setConteoPorCountry(orders.stream()
                    .map(Order::getCountry)
                    .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting())));
            result.setConteoPorItemType(orders.stream()
                    .map(Order::getItemType)
                    .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting())));
            result.setConteoPorSalesChannel(orders.stream()
                    .map(Order::getSalesChannel)
                    .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting())));
            result.setConteoPorOrderPriority(orders.stream()
                    .collect(Collectors.groupingByConcurrent(
                            order -> order.getPriority().name(), // Convierte la clave en String (nombre del PriorityEnum)
                            Collectors.counting()
                    )));


            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            e.printStackTrace();
            // O bien, utiliza algún framework de logging para registrar el error en un archivo de logs
            logger.error("Error al procesar lote de órdenes", e);
            return CompletableFuture.completedFuture(new ResumenConteos());
        }
    }

    private Order convertToEntity(OrderDTO orderDTO) {
        Order order = new Order();
        order.setId(orderDTO.getId());
        order.setRegion(orderDTO.getRegion());
        order.setCountry(orderDTO.getCountry());
        order.setItemType(orderDTO.getItemType());
        order.setSalesChannel(orderDTO.getSalesChannel());
        order.setPriority(orderDTO.getPriority());
        order.setDate(orderDTO.getDate());
        order.setShipDate(orderDTO.getShipDate());
        order.setUnitsSold(orderDTO.getUnitsSold());
        order.setUnitPrice(orderDTO.getUnitPrice());
        order.setUnitCost(orderDTO.getUnitCost());
        order.setTotalRevenue(orderDTO.getTotalRevenue());
        order.setTotalCost(orderDTO.getTotalCost());
        order.setTotalProfit(orderDTO.getTotalProfit());
        return order;
    }
    public void leerExportar() {
        List<Order> orders = orderRepository.findAllOrderByOrderById();
        exportToCsv(orders, "C:\\ImportOrders\\importOrders.csv");
    }
    public void exportToCsv(List<Order> orders, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Define el encabezado del CSV
            String[] header = {
                    "Order ID",
                    "Order Priority",
                    "Order Date",
                    "Region",
                    "Country",
                    "Item Type",
                    "Sales Channel",
                    "Ship Date",
                    "Units Sold",
                    "Unit Price",
                    "Unit Cost",
                    "Total Revenue",
                    "Total Cost",
                    "Total Profit"
            };
            writer.writeNext(header);

            // Escribe los datos de cada orden en el CSV
            for (Order order : orders) {
                String[] data = {
                        String.valueOf(order.getId()),
                        order.getPriority().name(),
                        Util.formatDateToString(order.getDate()),
                        order.getRegion(),
                        order.getCountry(),
                        order.getItemType(),
                        order.getSalesChannel(),
                        Util.formatDateToString(order.getShipDate()),
                        String.valueOf(order.getUnitsSold()),
                        String.valueOf(order.getUnitPrice()),
                        String.valueOf(order.getUnitCost()),
                        String.valueOf(order.getTotalRevenue()),
                        String.valueOf(order.getTotalCost()),
                        String.valueOf(order.getTotalProfit())
                };
                writer.writeNext(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Ha ocurrido un error en la exportación");
        }
    }
}


