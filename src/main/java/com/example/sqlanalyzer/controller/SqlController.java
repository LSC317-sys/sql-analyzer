package com.example.sqlanalyzer.controller;

import com.example.sqlanalyzer.dto.ApiResult;
import com.example.sqlanalyzer.dto.ExplainRequest;
import com.example.sqlanalyzer.entity.SqlRecord;
import com.example.sqlanalyzer.service.ExplainService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sql")
public class SqlController {

    @Autowired
    private ExplainService explainService;

    /**
     * POST /api/sql/explain
     * 分析 SQL 执行计划
     */
    @PostMapping("/explain")
    public ApiResult explain(@RequestBody ExplainRequest request, HttpServletRequest httpRequest) {
        try {
            String ip = httpRequest.getRemoteAddr();
            Map<String, Object> result = explainService.analyzeSql(request.getSql(), ip);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * GET /api/sql/history
     * 查询历史记录
     */
    @GetMapping("/history")
    public ApiResult history() {
        try {
            List<SqlRecord> records = explainService.getHistory();
            return ApiResult.success(records);
        } catch (Exception e) {
            return ApiResult.error("查询历史失败");
        }
    }
}