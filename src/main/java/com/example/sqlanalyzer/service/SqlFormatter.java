package com.example.sqlanalyzer.service;

import org.springframework.stereotype.Component;

/**
 * SQL 格式化工具
 */
@Component
public class SqlFormatter {

    /**
     * 格式化 SQL（简单实现）
     */
    public String format(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return sql;
        }

        // 统一换行
        sql = sql.trim().replaceAll("\\s+", " ");

        // 关键字换行
        String[] keywords = {
                "SELECT", "FROM", "WHERE", "AND", "OR",
                "ORDER BY", "GROUP BY", "HAVING", "LIMIT",
                "LEFT JOIN", "RIGHT JOIN", "INNER JOIN", "JOIN",
                "ON", "UNION"
        };

        for (String keyword : keywords) {
            sql = sql.replaceAll("(?i)\\s+" + keyword + "\\s+", "\n" + keyword + " ");
        }

        // SELECT 后的字段换行（简化版）
        if (sql.toUpperCase().startsWith("SELECT")) {
            sql = sql.replaceFirst("(?i)SELECT\\s+", "SELECT\n    ");
        }

        return sql.trim();
    }

    /**
     * 高亮 SQL 关键字（返回带标记的纯文本）
     */
    public String highlight(String sql) {
        if (sql == null) return "";
    
        // 用标记符号替代 HTML
        String[] keywords = {
            "SELECT", "FROM", "WHERE", "AND", "OR", "NOT",
            "ORDER BY", "GROUP BY", "HAVING", "LIMIT", "OFFSET",
            "LEFT JOIN", "RIGHT JOIN", "INNER JOIN", "JOIN", "ON",
            "INSERT", "UPDATE", "DELETE", "CREATE", "ALTER", "DROP",
            "UNION", "DISTINCT", "AS", "IN", "LIKE", "BETWEEN",
            "IS NULL", "IS NOT NULL", "ASC", "DESC"
        };
    
        for (String keyword : keywords) {
            sql = sql.replaceAll("(?i)\\b" + keyword + "\\b", "【" + keyword + "】");
        }
    
        return sql;
    }
}
