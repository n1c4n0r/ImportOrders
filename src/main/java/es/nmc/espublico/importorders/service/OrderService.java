package es.nmc.espublico.importorders.service;

import es.nmc.espublico.importorders.dto.OrderDTO;
import es.nmc.espublico.importorders.dto.ResumenConteos;
import es.nmc.espublico.importorders.repository.Order;
import es.nmc.espublico.importorders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<ResumenConteos> processOrdersInBatch(List<OrderDTO> orderDTOs) {
        long startTime = System.currentTimeMillis(); // Registro del tiempo inicial

        List<Order> orders = orderDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        orderRepository.saveAll(orders);

        long endTime = System.currentTimeMillis(); // Registro del tiempo final
        long totalTime = endTime - startTime;
        System.out.println("Tiempo de procesamiento de lote: " + totalTime + " ms");


        // Obtener los conteos por diferentes campos de este lote de órdenes
        ResumenConteos result = new ResumenConteos();
        /*result.setConteoPorRegion(orders.stream()
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

*/
        return CompletableFuture.completedFuture(result);
    }

/*    public Map<String, Long> obtenerConteoPorRegion(List<Order> orders) {
        return orders.parallelStream()
                .collect(Collectors.groupingByConcurrent(Order::getRegion, ConcurrentHashMap::new, Collectors.counting()));
    }*/

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
}

