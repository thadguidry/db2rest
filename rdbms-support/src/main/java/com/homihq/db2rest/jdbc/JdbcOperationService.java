package com.homihq.db2rest.jdbc;

import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import com.homihq.db2rest.core.dto.CreateBulkResponse;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.core.dto.CountResponse;
import com.homihq.db2rest.core.dto.ExistsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JdbcOperationService implements DbOperationService {


    @Override
    public int update(NamedParameterJdbcTemplate namedParameterJdbcTemplate, Map<String, Object> paramMap, String sql) {
        return namedParameterJdbcTemplate.update(sql, paramMap);
    }

    @Override
    public List<Map<String, Object>> read(NamedParameterJdbcTemplate namedParameterJdbcTemplate, Map<String, Object> paramMap, String sql) {
        return namedParameterJdbcTemplate.queryForList(sql, new MapSqlParameterSource(paramMap));
    }

    @Override
    public Map<String, Object> findOne(NamedParameterJdbcTemplate namedParameterJdbcTemplate, String sql, Map<String, Object> paramMap) {
        return namedParameterJdbcTemplate.queryForMap(sql, paramMap);
    }

    @Override
    public ExistsResponse exists(NamedParameterJdbcTemplate namedParameterJdbcTemplate, Map<String, Object> paramMap, String sql) {
        List<String> queryResult = namedParameterJdbcTemplate.query(sql,
                paramMap,
                (rs, rowNum) -> rs.getString(1)
        );

        if (queryResult.isEmpty()) return new ExistsResponse(false);
        return new ExistsResponse(true);
    }

    @Override
    public CountResponse count(NamedParameterJdbcTemplate namedParameterJdbcTemplate,Map<String, Object> paramMap, String sql) {
        Long itemCount = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Long.class);
        return new CountResponse(itemCount);
    }

    @Override
    public Object queryCustom(NamedParameterJdbcTemplate namedParameterJdbcTemplate, boolean single, String sql, Map<String, Object> params) {
        return single ?
                namedParameterJdbcTemplate.queryForMap(sql, params) :
                namedParameterJdbcTemplate.queryForList(sql, params);
    }

    @Override
    public int delete(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            Map<String, Object> params, String sql) {
        return namedParameterJdbcTemplate.update(sql,
                params);
    }

    @Override
    public CreateResponse create(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            Map<String, Object> data, String sql, DbTable dbTable) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int row = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(data),
                keyHolder, dbTable.getKeyColumnNames()
        );


        return new CreateResponse(row, keyHolder.getKeys());
    }

    @Override
    public CreateBulkResponse batchUpdate(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            List<Map<String, Object>> dataList, String sql, DbTable dbTable) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(dataList.toArray());

        int[] updateCounts;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        updateCounts = namedParameterJdbcTemplate.batchUpdate(sql, batch, keyHolder, dbTable.getKeyColumnNames());

        return new CreateBulkResponse(updateCounts, keyHolder.getKeyList());
    }


    public CreateBulkResponse batchUpdate(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            List<Map<String, Object>> dataList, String sql) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(dataList.toArray());

        int[] updateCounts = namedParameterJdbcTemplate.batchUpdate(sql, batch);

        return new CreateBulkResponse(updateCounts, null);
    }
}
