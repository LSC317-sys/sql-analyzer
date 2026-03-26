package com.example.sqlanalyzer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * SQL执行记录实体
 */
@Data
@Entity
@Table(name = "sql_record")
public class SqlRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 原始SQL语句 */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String sqlText;

    /** 执行计划结果 */
    @Column(columnDefinition = "TEXT")
    private String explainResult;

    /** 优化建议 */
    @Column(columnDefinition = "TEXT")
    private String suggestions;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 请求IP */
    private String ipAddress;
}
