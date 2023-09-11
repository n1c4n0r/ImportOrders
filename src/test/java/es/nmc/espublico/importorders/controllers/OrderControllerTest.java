package es.nmc.espublico.importorders.controllers;
import es.nmc.espublico.importorders.dto.PageOrderDTO;
import es.nmc.espublico.importorders.dto.PriorityEnum;
import es.nmc.espublico.importorders.dto.ResumenConteos;
import es.nmc.espublico.importorders.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private RestTemplate restTemplate;

    private static final String MENSAJE = "Se han procesado y guardado en la base de datos 0 órdenes.";

    @Test
    void testImportOrders_Success() throws Exception {
        // Simulamos una respuesta exitosa del servicio REST
        ResponseEntity<PageOrderDTO> responseEntity = new ResponseEntity<>(new PageOrderDTO(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(PageOrderDTO.class)))
                .thenReturn(responseEntity);

        // Simulamos un CompletableFuture con un resultado exitoso
        ResumenConteos resumenConteos = new ResumenConteos();
        Map<String, Long> mapa = new HashMap<>();
        mapa.put(PriorityEnum.C.name(),2L);
        resumenConteos.setConteoPorRegion(mapa);
        CompletableFuture<ResumenConteos> future = CompletableFuture.completedFuture(resumenConteos);
        when(orderService.processOrdersInBatch(anyList())).thenReturn(future);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/orders"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Aserciones para verificar que se invocaron los métodos simulados
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(PageOrderDTO.class));
        Assertions.assertEquals(future.get().getConteoPorRegion(),mapa);

    }

    @Test
    void testImportOrders_Failure() throws Exception {
        // Simulamos una excepción al llamar al servicio REST
        when(restTemplate.exchange(anyString(), any(), any(), eq(PageOrderDTO.class)))
                .thenThrow(new RuntimeException("Error en el servicio REST"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/orders"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Aserciones para verificar que se invocaron los métodos simulados
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(PageOrderDTO.class));

        // Se verifica que en mensaje, no se ha insertado nada.
         Assertions.assertTrue( result.getResponse().getContentAsString().contains(MENSAJE));
    }
}
