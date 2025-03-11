package com.gruposv.btgpactual.orderms.dtos;

import java.util.List;

public record ApiResponse<T>(List<T> data, PaginationResponse pagination) {
}
