package com.homihq.db2rest.jdbc.rest.create;

import com.homihq.db2rest.core.dto.CreateResponse;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface CreateRestApi {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(VERSION + "/{dbName}/{tableName}")
    CreateResponse save(@PathVariable String dbName,
                        @RequestHeader(name="Content-Profile", required = false) String schemaName,
                        @PathVariable String tableName,
                        @RequestParam(name = "columns", required = false) List<String> includeColumns,
                        @RequestParam(name = "sequences", required = false) List<String> sequences,
                        @RequestBody Map<String, Object> data,
                        @RequestParam(name = "tsIdEnabled", required = false, defaultValue = "false") boolean tsIdEnabled);
}
