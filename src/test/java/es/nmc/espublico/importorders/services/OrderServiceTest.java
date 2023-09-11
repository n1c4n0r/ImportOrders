package es.nmc.espublico.importorders.services;

import es.nmc.espublico.importorders.dto.OrderDTO;
import es.nmc.espublico.importorders.dto.ResumenConteos;
import es.nmc.espublico.importorders.repository.Order;
import es.nmc.espublico.importorders.repository.OrderRepository;
import es.nmc.espublico.importorders.service.OrderServiceImpl;
import es.nmc.espublico.importorders.util.UtilTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc

class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc  mockMvc;

    @Test
     void testProcessOrdersInBatch() {
        List<OrderDTO> orderDTOs = UtilTest.getDataOrderDto();

        List<Order> mockOrders = orderDTOs.stream()
                .map(orderDTO -> new Order()) // Create mock Order objects
                .collect(Collectors.toList());

        when(orderRepository.saveAll(anyList())).thenReturn(mockOrders);

        CompletableFuture<ResumenConteos> resultFuture = orderService.processOrdersInBatch(orderDTOs);
        ResumenConteos result = resultFuture.join();

        Assertions.assertNotNull(result);
    }
    @Test
     void testProcessOrdersInBatch_DataAccessException() throws InterruptedException, ExecutionException, TimeoutException {
        // Configuración del comportamiento esperado para lanzar DataAccessException
        when(orderRepository.saveAll(any())).thenThrow(new DataAccessException("Simulated DB error") {});

        // Llamada al método a probar
        CompletableFuture<ResumenConteos> future = orderService.processOrdersInBatch(List.of(/* ... */));

        // Espera a que la tarea asíncrona se complete (máximo 5 segundos)
        ResumenConteos resumenConteos = future.get(5, TimeUnit.SECONDS);

        // Verificación de resultados
        verify(orderRepository).saveAll(any());

        // Realiza aquí las aserciones que correspondan
        Assertions.assertNotNull(resumenConteos);
        Assertions.assertFalse(future.isCompletedExceptionally());
        Assertions.assertEquals(0, resumenConteos.getConteoPorRegion().size());
        Assertions.assertEquals(0, resumenConteos.getConteoPorCountry().size());
        Assertions.assertEquals(0, resumenConteos.getConteoPorItemType().size());
        Assertions.assertEquals(0, resumenConteos.getConteoPorSalesChannel().size());
        Assertions.assertEquals(0, resumenConteos.getConteoPorOrderPriority().size());

    }
}

