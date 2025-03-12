package com.gruposv.btgpactual.orderms.service;

import com.gruposv.btgpactual.orderms.dtos.OrderCreatedEvent;
import com.gruposv.btgpactual.orderms.dtos.OrderResponse;
import com.gruposv.btgpactual.orderms.entity.OrderEntity;
import com.gruposv.btgpactual.orderms.entity.OrderItem;
import com.gruposv.btgpactual.orderms.repostory.OrderRepostory;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class OrderService {


    private final OrderRepostory orderRepostory;
    private final MongoTemplate mongoTemplate;





    public OrderService(OrderRepostory orderRepostory, MongoTemplate mongoTemplate) {this.orderRepostory = orderRepostory;
        this.mongoTemplate = mongoTemplate;
    }


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

    public BigDecimal findTotalOnOdersByCustomerId(Long customerId){
    var aggregations  = newAggregation(
            match(Criteria.where("customerId").is(customerId)),
            group().sum("total").as("total")
    );
        var response = mongoTemplate.aggregate(aggregations,"tb_orders", Document.class);
        return new BigDecimal(response.getUniqueMappedResult().get("total").toString());
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
