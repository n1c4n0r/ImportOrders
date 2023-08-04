package es.nmc.espublico.importorders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
   // @Query("SELECT o FROM Orders o ORDER BY o.Id")
    List<Order> findAllOrderByOrderById();
}

