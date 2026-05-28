package pl.pkasiewicz.lottogame.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.pkasiewicz.lottogame.LottoGameApplication;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto.TicketResponseDto;
import pl.pkasiewicz.lottogame.resultannouncer.infrastructure.api.dto.ResultAnnouncementDto;

import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {LottoGameApplication.class, IntegrationTestConfig.class})
@ActiveProfiles("integration")
@Testcontainers
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    public AdjustableClock clock;

    @RegisterExtension
    public static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    static final PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("lotto_test")
                .withUsername("test")
                .withPassword("test");
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("lotto.number-generator.api.url", () -> wireMockServer.baseUrl() + "/api/v1.0/random");
    }

    protected TicketResponseDto submitTicket(List<Integer> numbers) throws Exception {
        String jsonArray = numbers.toString();
        String requestBody = String.format("{\"numbers\": %s}", jsonArray);

        String contentAsString = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").exists())
                .andExpect(jsonPath("$.numbers", hasSize(6)))
                .andExpect(jsonPath("$.drawDate").exists())
                .andReturn()
                .getResponse().getContentAsString();

        return objectMapper.readValue(contentAsString, TicketResponseDto.class);
    }

    protected void mockWinningNumbers(List<Integer> numbers) {
        String jsonArray = numbers.toString();

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/v1.0/random"))
                .withQueryParam("count", WireMock.equalTo("6"))
                .withQueryParam("min", WireMock.equalTo("1"))
                .withQueryParam("max", WireMock.equalTo("99"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonArray))
        );
    }

    protected void verifyResults(TicketResponseDto ticket, int expectedHitCount, String expectedStatus) throws Exception {
        String resultJson = mockMvc.perform(get("/api/results/{ticketId}", ticket.ticketId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ResultAnnouncementDto result = objectMapper.readValue(resultJson, ResultAnnouncementDto.class);

        assertAll(
                () -> assertThat(result.status()).isEqualTo(expectedStatus),
                () -> assertThat(result.result().ticketId()).isEqualTo(ticket.ticketId()),
                () -> assertThat(result.result().userNumbers()).isEqualTo(ticket.numbers()),
                () -> assertThat(result.result().drawDate()).isEqualTo(ticket.drawDate()),
                () -> assertThat(result.result().hitCount()).isEqualTo(expectedHitCount)
        );
    }
}
