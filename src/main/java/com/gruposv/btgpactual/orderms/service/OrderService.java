package com.gruposv.btgpactual.orderms.service;

import com.gruposv.btgpactual.orderms.dtos.OrderCreatedEvent;
import com.gruposv.btgpactual.orderms.dtos.OrderResponse;
import com.gruposv.btgpactual.orderms.entity.OrderEntity;
import com.gruposv.btgpactual.orderms.entity.OrderItem;
import com.gruposv.btgpactual.orderms.repostory.OrderRepostory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {


    private final OrderRepostory orderRepostory;
    public OrderService(OrderRepostory orderRepostory) {this.orderRepostory = orderRepostory;}


    public void save(OrderCreatedEvent event){

        var entity = new OrderEntity();
        entity.setOrderId(event.codigoPedido());
        entity.setCustomerId(event.codigoCliente());
        entity.setTotal(getTotal(event));
        entity.setItems(getOrderItems(event));

        orderRepostory.save(entity);
    }
    public Page<OrderResponse> FindAllByCustomerId(Long customerId, PageRequest pageRequest ){
        var orders = orderRepostory.findAllByCustomerId(customerId,pageRequest);
        return orders.map(OrderResponse::fromEntity);
    }

    private BigDecimal getTotal(OrderCreatedEvent event) {
        return event.itens()
                .stream()
                .map(i -> i.preco().multiply(BigDecimal.valueOf(i.quantidade())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

    }

    private static List<OrderItem> getOrderItems(OrderCreatedEvent event) {
        return event.itens().stream().map(i -> new OrderItem(i.produto(), i.quantidade(), i.preco()))
                .toList();
    }

}
