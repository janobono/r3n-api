package sk.r3n.example.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sk.r3n.example.api.service.so.HotelInputSO;
import sk.r3n.example.api.service.so.HotelSO;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(
        properties = {
                "spring.flyway.clean-disabled=false"
        }
)
public class HotelControllerIT {

    @Container
    public static PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>
            ("postgres:13-alpine")
            .withDatabaseName("app")
            .withUsername("app")
            .withPassword("app");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) throws Exception {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
    }

    @Autowired
    public MockMvc mvc;

    @Autowired
    public Flyway flyway;

    @Autowired
    public WebApplicationContext webApplicationContext;

    @Autowired
    public ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        flyway.clean();
        flyway.migrate();
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private static final String BASE_URL = "/hotels";

    @Test
    public void fullTest() throws Exception {
        Map<Long, HotelSO> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            HotelSO hotelSO = createHotel(i);
            map.put(hotelSO.id(), hotelSO);
        }

        int i = 0;
        for (Long id : map.keySet()) {
            HotelSO hotelSO = updateHotel(id, i);
            map.put(id, hotelSO);
            i++;
        }

        for (Long id : map.keySet()) {
            HotelSO hotelSO = getHotel(id);
            assertThat(hotelSO).usingRecursiveComparison().isEqualTo(map.get(id));
        }

        Page<HotelSO> page = getHotels(null, null);
        assertThat(page.getTotalElements()).isEqualTo(10L);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getNumberOfElements()).isEqualTo(10);
        page.get().forEach(hotelSO -> {
            if (map.containsKey(hotelSO.id()))
                assertThat(hotelSO).usingRecursiveComparison().isEqualTo(map.get(hotelSO.id()));
        });

        page = getHotels(0, 10);
        assertThat(page.getTotalElements()).isEqualTo(10L);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getNumberOfElements()).isEqualTo(10);
        page.get().forEach(hotelSO -> {
            if (map.containsKey(hotelSO.id()))
                assertThat(hotelSO).usingRecursiveComparison().isEqualTo(map.get(hotelSO.id()));
        });

        for (Long id : map.keySet()) {
            deleteHotel(id);
        }
    }

    private HotelSO createHotel(int i) throws Exception {
        HotelInputSO hotelInputSO = new HotelInputSO("CreatedName" + i, "CreatedNote" + i);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(hotelInputSO))).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
        HotelSO hotelSO = mapFromJson(mvcResult.getResponse().getContentAsString(), HotelSO.class);
        assertThat(hotelInputSO).usingRecursiveComparison().ignoringFields("id").isEqualTo(hotelSO);
        return hotelSO;
    }

    private HotelSO updateHotel(Long id, int i) throws Exception {
        HotelSO hotelSO = new HotelSO(id, "UpdatedName" + i, "UpdatedNote" + i);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(hotelSO))).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        HotelSO result = mapFromJson(mvcResult.getResponse().getContentAsString(), HotelSO.class);
        assertThat(hotelSO).usingRecursiveComparison().isEqualTo(result);
        return result;
    }

    private void deleteHotel(Long id) throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + id)).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        mvcResult = mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + id)).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private HotelSO getHotel(Long id) throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + id)).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        return mapFromJson(mvcResult.getResponse().getContentAsString(), HotelSO.class);
    }

    private Page<HotelSO> getHotels(Integer page, Integer size) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(BASE_URL);
        if (Objects.nonNull(page)) {
            builder.param("page", page.toString());
        }
        if (Objects.nonNull(size)) {
            builder.param("size", size.toString());
        }
        MvcResult mvcResult = mvc.perform(builder).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        return mapPagedResponse(mvcResult.getResponse().getContentAsString(), HotelSO.class);
    }

    private String mapToJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    private <T> T mapFromJson(String json, Class<T> clazz)
            throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    private <T> List<T> mapListFromJson(String json, Class<T> paramClazz) throws Exception {
        return getListFromNode(objectMapper.readTree(json), paramClazz);
    }

    private <T> Page<T> mapPagedResponse(String json, Class<T> paramClazz)
            throws IOException {
        JsonNode parent = objectMapper.readTree(json);
        return new PageImpl<>(
                getListFromNode(parent.get("content"), paramClazz),
                PageRequest.of(
                        parent.get("pageable").get("pageNumber").asInt(),
                        parent.get("pageable").get("pageSize").asInt()),
                parent.get("totalElements").asLong());
    }

    private <T> List<T> getListFromNode(JsonNode node, Class<T> clazz) throws IOException {
        List<T> content = new ArrayList<>();
        for (JsonNode val : node) {
            content.add(objectMapper.readValue(val.traverse(), clazz));
        }
        return content;
    }
}
