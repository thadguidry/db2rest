package com.homihq.db2rest.rest.mysql;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.MySQLBaseIntegrationTest;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(71)
@TestWithResources
class UpdateTwoTablesSameNameDiffSchemaTest extends MySQLBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());


    @GivenJsonResource("/testdata/UPDATE_EMPLOYEE_REQUEST.json")
    Map<String,Object> UPDATE_EMPLOYEE_REQUEST;

    @Test
    @DisplayName("Update employee diff schema")
    void updateEmployee() throws Exception {

        mockMvc.perform(patch(VERSION + "/mysqldb/employee")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Content-Profile", "sakila")
                        .param("filter", "emp_id==1")
                        .content(objectMapper.writeValueAsString(UPDATE_EMPLOYEE_REQUEST))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                //.andDo(print())
                .andDo(document("mysql-update-emp-sakila"));


        mockMvc.perform(patch( VERSION + "/mysqldb/employee")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Content-Profile", "wakila")
                        .param("filter", "emp_id==1")
                        .content(objectMapper.writeValueAsString(UPDATE_EMPLOYEE_REQUEST))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                //.andDo(print())
                .andDo(document("mysql-update-emp-wakila"));
    }




}
