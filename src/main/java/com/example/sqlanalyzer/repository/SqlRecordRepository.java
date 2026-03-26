package com.example.sqlanalyzer.repository;

import com.example.sqlanalyzer.entity.SqlRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SqlRecordRepository extends JpaRepository<SqlRecord, Long> {

    /** 查询最近的20条记录 */
    List<SqlRecord> findTop20ByOrderByCreatedAtDesc();
}
