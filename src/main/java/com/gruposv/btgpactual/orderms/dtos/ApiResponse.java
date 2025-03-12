package com.gruposv.btgpactual.orderms.dtos;

import java.util.List;
import java.util.Map;

public record ApiResponse<T>(Map<String, Object> summary, List<T> data, PaginationResponse pagination) {
}
