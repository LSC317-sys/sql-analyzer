package com.example.sqlanalyzer.dto;

import lombok.Data;

@Data
public class ApiResult {
    private int code;
    private String msg;
    private Object data;

    public static ApiResult success(Object data) {
        ApiResult result = new ApiResult();
        result.setCode(200);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static ApiResult error(String msg) {
        ApiResult result = new ApiResult();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}
