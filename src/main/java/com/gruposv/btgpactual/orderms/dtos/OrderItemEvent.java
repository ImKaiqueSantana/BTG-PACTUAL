package com.gruposv.btgpactual.orderms.dtos;

import java.math.BigDecimal;

public record OrderItemEvent(String produto, Integer quantidade, BigDecimal preco) {
}
