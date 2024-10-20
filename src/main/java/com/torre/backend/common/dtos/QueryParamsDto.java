package com.torre.backend.common.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryParamsDto {
    private String filter = "";
    private String sort = "DESC";
    private String order = "id";
    private Integer page = 0;
    private Integer limit = 10;
}
