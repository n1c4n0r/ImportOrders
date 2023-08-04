package es.nmc.espublico.importorders.controller;


import es.nmc.espublico.importorders.dto.PageOrderDTO;
import es.nmc.espublico.importorders.dto.ResumenConteos;
import es.nmc.espublico.importorders.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

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
            try {
                String url = generateUrl("/orders", currentPage, MAX_PER_PAGE);
                ResponseEntity<PageOrderDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), PageOrderDTO.class);
                PageOrderDTO pageOrder = responseEntity.getBody();
                if(currentPage==100){
                    break;
                }
                if (pageOrder == null || pageOrder.getContent().isEmpty()) {
                    // No hay más órdenes, salimos del bucle
                    break;
                }

                // Procesar y guardar las órdenes en la base de datos de forma asíncrona
                CompletableFuture<ResumenConteos> future = orderService.processOrdersInBatch(pageOrder.getContent());
                futures.add(future);

                totalOrdersProcessed += pageOrder.getContent().size();
                currentPage++;
            } catch(Exception e) {
                e.printStackTrace();
                // O bien, utiliza algún framework de logging para registrar el error en un archivo de logs
                // logger.error("Error al procesar la página " + currentPage, e);
            }
        }

        // Utilizar CompletableFuture.allOf para esperar a que todas las tareas finalicen
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allFutures.join(); // Esperar a que todas las tareas asíncronas hayan terminado

        // Realizar el merge de los conteos de todos los hilos
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
        logger.info("-----Resumen Region-----");
        conteoTotalPorRegion.forEach((region, conteo) -> logger.info(region + ": " + conteo));
        logger.info("-----Resumen Country-----");
        conteoTotalPorCountry.forEach((country, conteo) -> logger.info(country + ": " + conteo));
        logger.info("-----Resumen ItemType-----");
        conteoTotalPorItemType.forEach((itemType, conteo) -> logger.info(itemType + ": " + conteo));
        logger.info("-----Resumen SalesChannel-----");
        conteoTotalPorSalesChannel.forEach((salesChannel, conteo) -> logger.info(salesChannel + ": " + conteo));
        logger.info("-----OrderPriority-----");
        conteoTotalPorOrderPriority.forEach((orderPriority, conteo) -> logger.info(orderPriority + ": " + conteo));
        orderService.leerExportar();


        long endTime = System.currentTimeMillis(); // Registro del tiempo final
        long totalTime = endTime - startTime;

        return "Se han procesado y guardado en la base de datos " + totalOrdersProcessed + " órdenes. Tiempo total: " + totalTime + " ms";
    }
}