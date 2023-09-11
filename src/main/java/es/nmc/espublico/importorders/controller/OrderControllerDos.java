package es.nmc.espublico.importorders.controller;

import es.nmc.espublico.importorders.dto.PageOrderDTO;
import es.nmc.espublico.importorders.dto.ResumenConteos;
import es.nmc.espublico.importorders.service.OrderService;
import org.springframework.core.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;



import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class OrderControllerDos {
    private static final String PARAMS = "{} : {}";
    private RestTemplate restTemplate;
    private HttpHeaders headers;
    private final OrderService orderService;
    private static final int DEFAULT_MAX_PER_PAGE = 100;
    private static final Logger logger = LoggerFactory.getLogger(OrderControllerDos.class);

    @Autowired
    public OrderControllerDos(OrderService orderService, RestTemplate restTemplate) {
        this.orderService = orderService;
        this.restTemplate = restTemplate;

        // Configurar tiempo de espera
        ClientHttpRequestFactory factory = restTemplate.getRequestFactory();
        if (factory instanceof SimpleClientHttpRequestFactory) {
            SimpleClientHttpRequestFactory simpleFactory = (SimpleClientHttpRequestFactory) factory;
            //simpleFactory.setConnectTimeout(5000); // Ejemplo: 5 segundos
            //simpleFactory.setReadTimeout(10000);   // Ejemplo: 10 segundos
        }

        this.headers = new HttpHeaders();
        this.headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
    }

    @GetMapping("/ordersTwo")
    public String importOrders(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "maxPerPage", defaultValue = "100") int maxPerPage
    ) {
        long startTime = System.currentTimeMillis(); // Registro del tiempo inicial

        int totalOrdersProcessed = 0;
        boolean continueProcessing = true;

        // Lista para almacenar los futuros generados por cada lote de órdenes
        List<CompletableFuture<ResumenConteos>> futures = new ArrayList<>();
        try {
            totalOrdersProcessed = getTotalOrdersProcessed(page, maxPerPage, totalOrdersProcessed, continueProcessing, futures);

            // Esperar a que todas las tareas finalicen (allof)
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.join();

            mergeThreadsAndPrint(futures);

            long endTime = System.currentTimeMillis(); // Registro del tiempo final
            long totalTime = endTime - startTime;

            return "Se han procesado y guardado en la base de datos " + totalOrdersProcessed + " órdenes. Tiempo total: " + totalTime + " ms";
        } catch (Exception e) {
            logger.error("Error al importar orders", e);
            return "Error al importar orders";
        }
    }

    private int getTotalOrdersProcessed(int currentPage, int maxPerPage, int totalOrdersProcessed, boolean continueProcessing, List<CompletableFuture<ResumenConteos>> futures) {
        // Bucle para procesar cada página de órdenes
        while (continueProcessing) {
            try {
                logger.info("Se va a procesar la página " + currentPage + ". URL: " + generateUrl("/orders", currentPage, maxPerPage));

                URI url = generateUrl("/orders", currentPage, maxPerPage);
                ResponseEntity<PageOrderDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), PageOrderDTO.class);
                PageOrderDTO pageOrder = responseEntity.getBody();
                if (pageOrder == null || pageOrder.getContent().isEmpty()) {
                    logger.info("Se ha procesado la página " + currentPage + ". URL: " + generateUrl("/orders", currentPage, maxPerPage));
                    // No hay más órdenes, salimos del bucle
                    continueProcessing = false;
                } else {
                    // Procesar y guardar las órdenes en la base de datos de forma asíncrona
                    CompletableFuture<ResumenConteos> future = orderService.processOrdersInBatch(pageOrder.getContent());
                    futures.add(future);

                    totalOrdersProcessed += pageOrder.getContent().size();
                    currentPage++;
                }
            } catch (HttpStatusCodeException hce) {
                logger.error("Error al procesar la respuesta en la página " + currentPage + ". URL: " + generateUrl("/orders", currentPage, maxPerPage), hce);
                continueProcessing = false;
            } catch (RestClientException re) {
                logger.error("Error al procesar la respuesta en la página " + currentPage + " Url: " + generateUrl("/orders", currentPage, maxPerPage), re);
                continueProcessing = false;
            } catch (Exception e) {
                logger.error("Error al procesar la página " + currentPage + " Url: " + generateUrl("/orders", currentPage, maxPerPage), e);
                continueProcessing = false;
            }
        }
        return totalOrdersProcessed;
    }

    private void mergeThreadsAndPrint(List<CompletableFuture<ResumenConteos>> futures) {
        // Realizar el merge de los conteos de todos los hilos
        Map<String, Long> conteoTotalPorRegion = new ConcurrentHashMap<>();
        Map<String, Long> conteoTotalPorCountry = new ConcurrentHashMap<>();
        Map<String, Long> conteoTotalPorItemType = new ConcurrentHashMap<>();
        Map<String, Long> conteoTotalPorSalesChannel = new ConcurrentHashMap<>();
        Map<String, Long> conteoTotalPorOrderPriority = new ConcurrentHashMap<>();

        futures.forEach(future -> {
            try {
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

            } catch (InterruptedException ie) {
                logger.error("Error interrupción en los hilos  del resumen ", ie);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.error("Error al procesar el resumen ", e);
            }
        });

        // Imprimir el conteo total por regiones
        logger.info("-----Resumen Region-----");
        conteoTotalPorRegion.forEach((region, conteo) -> logger.info(PARAMS, region, conteo));
        logger.info("-----Resumen Country-----");
        conteoTotalPorCountry.forEach((country, conteo) -> logger.info(PARAMS, country, conteo));
        logger.info("-----Resumen ItemType-----");
        conteoTotalPorItemType.forEach((itemType, conteo) -> logger.info(PARAMS, itemType, conteo));
        logger.info("-----Resumen SalesChannel-----");
        conteoTotalPorSalesChannel.forEach((salesChannel, conteo) -> logger.info(PARAMS, salesChannel, conteo));
        logger.info("-----OrderPriority-----");
        conteoTotalPorOrderPriority.forEach((orderPriority, conteo) -> logger.info(PARAMS, orderPriority, conteo));
        orderService.leerExportar();
    }

    private URI generateUrl(String path, int page, int maxPerPage) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("https://kata-espublicotech.g3stiona.com/v1" + path)
                .queryParam("page", page)
                .queryParam("max-per-page", maxPerPage);

        return uriBuilder.build().toUri();
    }



    @GetMapping("/downloadCSV")
    public ResponseEntity<Resource> downloadCSV() {
        // Ruta al archivo CSV
        String filePath = "C:\\ImportOrders\\ImportOrders.csv";

        // Objeto File
        File file = new File(filePath);

        // Recurso FileSystemResource
        Resource resource = new FileSystemResource(file);

        // Cabeceras de respuesta para la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ImportOrders.csv");

        try {
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error al procesar la descarga del archivo", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
