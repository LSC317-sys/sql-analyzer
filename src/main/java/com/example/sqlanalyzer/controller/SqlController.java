package com.example.sqlanalyzer.controller;

import com.example.sqlanalyzer.dto.ApiResult;
import com.example.sqlanalyzer.dto.ExplainRequest;
import com.example.sqlanalyzer.entity.SqlRecord;
import com.example.sqlanalyzer.service.ExplainService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    /**
     * GET /api/sql/export/{id}
     * 导出分析报告
     */
    @GetMapping("/export/{id}")
    public ApiResult exportReport(@PathVariable Long id) {
        try {
            SqlRecord record = explainService.getById(id);
            if (record == null) {
                return ApiResult.error("记录不存在");
            }

            // 生成报告内容
            StringBuilder report = new StringBuilder();
            report.append("# SQL 执行计划分析报告\n\n");
            report.append("## 原始 SQL\n```\n");
            report.append(record.getSqlText());
            report.append("\n```\n\n");
            report.append("## 执行计划\n```\n");
            report.append(record.getExplainResult());
            report.append("\n```\n\n");
            report.append("## 优化建议\n");
            report.append(record.getSuggestions());
            report.append("\n\n---\n");
            report.append("分析时间: ").append(record.getCreatedAt());

            Map<String, Object> data = new HashMap<>();
            data.put("report", report.toString());
            data.put("filename", "sql-report-" + id + ".md");

            return ApiResult.success(data);
        } catch (Exception e) {
            return ApiResult.error("导出失败: " + e.getMessage());
        }
    }
}