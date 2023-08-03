package es.nmc.espublico.importorders.controller;


import es.nmc.espublico.importorders.dto.PageOrderDTO;
import es.nmc.espublico.importorders.dto.ResumenConteos;
import es.nmc.espublico.importorders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@RestController
public class OrderController {
    private RestTemplate restTemplate;
    private HttpHeaders headers;
    private OrderService orderService;
    private final int MAX_PER_PAGE = 1000;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
        this.restTemplate = new RestTemplate();
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_JSON);
    }

    private String generateUrl(String path, int page, int maxPerPage) {
        return "https://kata-espublicotech.g3stiona.com/v1" + path + "?page=" + page + "&max-per-page=" + maxPerPage;
    }

    @GetMapping("/orders")
    public String importOrders() throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis(); // Registro del tiempo inicial
        int currentPage = 1;
        int totalOrdersProcessed = 0;

        // Lista para almacenar los futuros generados por cada lote de órdenes
        List<CompletableFuture<ResumenConteos>> futures = new ArrayList<>();

        // Bucle para procesar cada página de órdenes
        while (true) {
            String url = generateUrl("/orders", currentPage, MAX_PER_PAGE);
            ResponseEntity<PageOrderDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), PageOrderDTO.class);
            PageOrderDTO pageOrder = responseEntity.getBody();

            if (pageOrder == null || pageOrder.getContent().isEmpty()) {
                // No hay más órdenes, salimos del bucle
                break;
            }

            // Procesar y guardar las órdenes en la base de datos de forma asíncrona
            CompletableFuture<ResumenConteos> future = orderService.processOrdersInBatch(pageOrder.getContent());
            futures.add(future);

            totalOrdersProcessed += pageOrder.getContent().size();
            currentPage++;
        }

        // Utilizar CompletableFuture.allOf para esperar a que todas las tareas finalicen
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allFutures.join(); // Esperar a que todas las tareas asíncronas hayan terminado

      /*  // Realizar el merge de los conteos de todos los hilos
        Map<String, Long> conteoTotalPorRegion = new ConcurrentHashMap<>();
        Map<String, Long> conteoTotalPorCountry = new ConcurrentHashMap<>();
        Map<String, Long> conteoTotalPorItemType = new ConcurrentHashMap<>();
        Map<String, Long> conteoTotalPorSalesChannel = new ConcurrentHashMap<>();
        Map<String, Long> conteoTotalPorOrderPriority = new ConcurrentHashMap<>();

        futures.forEach(future -> {
            try {
                // Puedes obtener los diferentes mapas de conteo desde la instancia de OrderCountResult
                Map<String, Long> conteoPorRegion = future.get().getConteoPorRegion();
                Map<String, Long> conteoPorCountry = future.get().getConteoPorCountry();
                Map<String, Long> conteoPorItemType = future.get().getConteoPorItemType();
                Map<String, Long> conteoPorSalesChannel = future.get().getConteoPorSalesChannel();
                Map<String, Long> conteoPorOrderPriority = future.get().getConteoPorOrderPriority();

                conteoPorRegion.forEach((region, conteo) -> conteoTotalPorRegion.merge(region, conteo, Long::sum));
                conteoPorCountry.forEach((country, conteo) -> conteoTotalPorCountry.merge(country, conteo, Long::sum));
                conteoPorItemType.forEach((itemType, conteo) -> conteoTotalPorItemType.merge(itemType, conteo, Long::sum));
                conteoPorSalesChannel.forEach((salesChannel, conteo) -> conteoTotalPorSalesChannel.merge(salesChannel, conteo, Long::sum));
                conteoPorOrderPriority.forEach((priorityEnum, conteo) -> conteoTotalPorOrderPriority.merge(priorityEnum, conteo, Long::sum));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Imprimir el conteo total por regiones
        System.out.println("-----Resumen Region-----");
        conteoTotalPorRegion.forEach((region, conteo) -> System.out.println(region + ": " + conteo));
        System.out.println("-----Resumen Country-----");
        conteoTotalPorCountry.forEach((country, conteo) -> System.out.println(country + ": " + conteo));
        System.out.println("-----Resumen ItemType-----");
        conteoTotalPorItemType.forEach((itemType, conteo) -> System.out.println(itemType + ": " + conteo));
        System.out.println("-----Resumen SalesChannel-----");
        conteoTotalPorSalesChannel.forEach((salesChannel, conteo) -> System.out.println(salesChannel + ": " + conteo));
        System.out.println("-----OrderPriority-----");
        conteoTotalPorOrderPriority.forEach((orderPriority, conteo) -> System.out.println(orderPriority + ": " + conteo));

*/
        long endTime = System.currentTimeMillis(); // Registro del tiempo final
        long totalTime = endTime - startTime;

        return "Se han procesado y guardado en la base de datos " + totalOrdersProcessed + " órdenes. Tiempo total: " + totalTime + " ms";
    }
}