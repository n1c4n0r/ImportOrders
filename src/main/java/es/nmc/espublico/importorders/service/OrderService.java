package es.nmc.espublico.importorders.service;

import es.nmc.espublico.importorders.dto.OrderDTO;
import es.nmc.espublico.importorders.dto.ResumenConteos;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface OrderService {
    CompletableFuture<ResumenConteos> processOrdersInBatch(List<OrderDTO> orderDTOs);

    void leerExportar();
}