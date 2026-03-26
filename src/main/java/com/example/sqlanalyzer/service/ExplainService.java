package com.example.sqlanalyzer.service;

import com.example.sqlanalyzer.entity.SqlRecord;
import com.example.sqlanalyzer.repository.SqlRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExplainService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SqlRecordRepository sqlRecordRepository;

    @Autowired
    private SqlFormatter sqlFormatter;

    /**
     * 执行 SQL 分析
     */
    public Map<String, Object> analyzeSql(String sql, String ipAddress) {
        System.out.println("【DEBUG】收到的SQL: [" + sql + "]");
        System.out.println("【DEBUG】SQL长度: " + sql.length());

        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 安全检查
            String lowerSql = sql.toLowerCase().trim();
            System.out.println("【DEBUG】安全检查通过");

            if (lowerSql.startsWith("drop") ||
                    lowerSql.startsWith("delete") ||
                    lowerSql.startsWith("update") ||
                    lowerSql.startsWith("insert") ||
                    lowerSql.startsWith("alter") ||
                    lowerSql.startsWith("truncate")) {
                throw new RuntimeException("⚠️ 仅支持 SELECT 查询");
            }

            // 2. 执行 EXPLAIN
            String explainSql = "EXPLAIN " + sql;
            System.out.println("【DEBUG】执行SQL: " + explainSql);

            List<Map<String, Object>> explainResults = jdbcTemplate.queryForList(explainSql);
            System.out.println("【DEBUG】查询成功，返回 " + explainResults.size() + " 行");

            // 3. 格式化
            String formattedResult = formatExplainResult(explainResults);
            result.put("explain", formattedResult);
            result.put("formattedSql", sqlFormatter.format(sql));
            result.put("highlightedSql", sqlFormatter.highlight(sql));

            // 4. 建议
            List<String> suggestions = generateSuggestions(sql, explainResults);
            result.put("suggestions", suggestions);

            // 5. 保存
            SqlRecord record = new SqlRecord();
            record.setSqlText(sql);
            record.setExplainResult(formattedResult);
            record.setSuggestions(String.join("\n", suggestions));
            record.setCreatedAt(LocalDateTime.now());
            record.setIpAddress(ipAddress);
            SqlRecord saved = sqlRecordRepository.save(record);
            result.put("id", saved.getId());

            System.out.println("【DEBUG】分析完成！");
            return result;

        } catch (Exception e) {
            System.out.println("【ERROR】分析失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("分析失败: " + e.getMessage());
        }
    }

    /**
     * 格式化执行计划为表格
     */
    private String formatExplainResult(List<Map<String, Object>> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("| id | select_type | table | type | possible_keys | key | rows | Extra |\n");
        sb.append("|----|-------------|-------|------|---------------|-----|------|-------|\n");

        for (Map<String, Object> row : results) {
            sb.append(String.format("| %s | %s | %s | %s | %s | %s | %s | %s |\n",
                    row.get("id") != null ? row.get("id") : "",
                    row.get("select_type") != null ? row.get("select_type") : "",
                    row.get("table") != null ? row.get("table") : "",
                    row.get("type") != null ? row.get("type") : "",
                    row.get("possible_keys") != null ? row.get("possible_keys") : "",
                    row.get("key") != null ? row.get("key") : "",
                    row.get("rows") != null ? row.get("rows") : "",
                    row.get("Extra") != null ? row.get("Extra") : ""
            ));
        }
        return sb.toString();
    }

    /**
     * 生成优化建议
     */
    private List<String> generateSuggestions(String sql, List<Map<String, Object>> results) {
        List<String> suggestions = new ArrayList<>();
        String lowerSql = sql.toLowerCase();

        for (Map<String, Object> row : results) {
            String type = String.valueOf(row.get("type") != null ? row.get("type") : "");
            String key = String.valueOf(row.get("key") != null ? row.get("key") : "NULL");
            String extra = String.valueOf(row.get("Extra") != null ? row.get("Extra") : "");
            String rows = String.valueOf(row.get("rows") != null ? row.get("rows") : "0");

            // 全表扫描
            if ("ALL".equals(type)) {
                suggestions.add("⚠️ [全表扫描] 建议在 WHERE 条件字段上添加索引");
            }

            // 无索引
            if ("NULL".equals(key)) {
                suggestions.add("⚠️ [未使用索引] 考虑为查询条件字段创建索引");
            }

            // 文件排序
            if (extra.contains("Using filesort")) {
                suggestions.add("⚠️ [文件排序] ORDER BY 字段建议建立索引，避免使用函数");
            }

            // 临时表
            if (extra.contains("Using temporary")) {
                suggestions.add("⚠️ [临时表] 建议优化 SQL 结构，减少不必要字段");
            }

            // 扫描行数过多
            try {
                long rowCount = Long.parseLong(rows);
                if (rowCount > 10000) {
                    suggestions.add("⚠️ [大结果集] 扫描行数过多(" + rowCount + ")，建议添加索引或优化条件");
                }
            } catch (Exception e) {}
        }

        // SELECT * 建议
        if (lowerSql.contains("select *")) {
            suggestions.add("💡 [最佳实践] 避免使用 SELECT *，指定需要的字段");
        }
        // ========== 更多优化建议 ==========

// LIKE 查询优化
        if (lowerSql.contains("like") && lowerSql.contains("'%")) {
            suggestions.add("⚠️ [模糊查询] 前缀模糊查询 LIKE '%xxx' 无法使用索引");
        }

// OR 条件优化
        if (lowerSql.contains(" or ")) {
            suggestions.add("💡 [条件优化] 考虑用 UNION ALL 替代 OR，可能更高效");
        }

// NOT IN 优化
        if (lowerSql.contains("not in")) {
            suggestions.add("💡 [条件优化] NOT IN 效率较低，建议改用 NOT EXISTS 或 LEFT JOIN");
        }

// 子查询优化
        if (lowerSql.contains("select") && lowerSql.indexOf("select") != lowerSql.lastIndexOf("select")) {
            suggestions.add("💡 [子查询] 存在嵌套查询，考虑改用 JOIN 提升性能");
        }

// DISTINCT 优化
        if (lowerSql.contains("distinct")) {
            suggestions.add("💡 [去重] DISTINCT 会产生临时表，确保必要或改用 GROUP BY");
        }

// LIMIT 建议
        if (!lowerSql.contains("limit") && lowerSql.contains("select")) {
            suggestions.add("💡 [分页] 建议添加 LIMIT 防止返回过多数据");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("✅ 执行计划良好，未检测到明显性能问题");
        }

        return suggestions;
    }

    /**
     * 获取历史记录
     */
    public List<SqlRecord> getHistory() {
        return sqlRecordRepository.findTop20ByOrderByCreatedAtDesc();
    }

    public SqlRecord getById(Long id) {
        return sqlRecordRepository.findById(id).orElse(null);
    }
}