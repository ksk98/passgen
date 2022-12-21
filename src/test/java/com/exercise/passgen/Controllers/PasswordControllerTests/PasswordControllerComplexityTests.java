package com.exercise.passgen.Controllers.PasswordControllerTests;

import com.exercise.passgen.enums.Complexity;
import com.exercise.passgen.models.schemas.PasswordDTO;
import com.exercise.passgen.models.schemas.PasswordGenerationRequestDTO;
import com.exercise.passgen.models.schemas.PasswordGenerationResponseDTO;
import com.exercise.passgen.util.JSONParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PasswordControllerComplexityTests {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    private final String COMPLEXITY_URI = "/password/complexity", GENERATION_URI = "/password/generate";

    @Test
    public void successfulGenerationAndComplexityChecks() throws Exception {
        // Generates a batch for every existing complexity and then validates every password (all via the controller)
        int batchSize = 3;

        for (Complexity complexity: Complexity.class.getEnumConstants()) {
            assertSuccessfulGenerationAndComplexityCheck(complexity, batchSize);
        }
    }

    @Test
    public void successfulComplexityChecksWithoutPersistence() throws Exception {
        // Variable length with lower, upper and special case
        assertAndGetComplexityCheckResponse("!es7PASSWORD!!222", Complexity.ULTRA);
        assertAndGetComplexityCheckResponse("!es7PASSWORD!!22", Complexity.HIGH);
        assertAndGetComplexityCheckResponse("!es7PASS!", Complexity.HIGH);
        assertAndGetComplexityCheckResponse("!es7PASS", Complexity.MEDIUM);
        assertAndGetComplexityCheckResponse("!es7PA", Complexity.MEDIUM);
        assertAndGetComplexityCheckResponse("!es7P", Complexity.LOW);

        // Constant length with varied cases
        assertAndGetComplexityCheckResponse("tes7PASSWORD11222", Complexity.MEDIUM);
        assertAndGetComplexityCheckResponse("tes7password!!222", Complexity.LOW);
        assertAndGetComplexityCheckResponse("tes7password11222", Complexity.LOW);
    }

    private void assertSuccessfulGenerationAndComplexityCheck(Complexity complexity, int batchSize) throws Exception {
        PasswordGenerationRequestDTO request = generateGenerationRequest(complexity, batchSize);
        PasswordGenerationResponseDTO response = assertAndGetGenerationResponse(request, complexity, batchSize);

        for (String password: response.getPasswords()) {
            PasswordDTO responseObject = assertAndGetComplexityCheckResponse(password, complexity);
            assertNotNull(responseObject.getGenerationDateTime());
        }
    }

    private PasswordDTO assertAndGetComplexityCheckResponse(String password, Complexity complexity) throws Exception {
        // Asserts the:
        // - HTML response code
        // - echoed password
        // - complexity
        // - persistence (by checking if generation time is not null)

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post(COMPLEXITY_URI)
                .contentType(MediaType.TEXT_PLAIN).content(password)).andReturn();
        assertEquals(200, response.getResponse().getStatus());

        PasswordDTO responseObject = JSONParser.mapFromJson(response.getResponse().getContentAsString(), PasswordDTO.class);
        assertEquals(password, responseObject.getPassword());
        assertEquals(complexity, responseObject.getComplexity());

        return responseObject;
    }

    private PasswordGenerationRequestDTO generateGenerationRequest(Complexity complexity, int batchSize) {
        return PasswordGenerationRequestDTO.builder()
                .length(complexity.getMINIMUM_CHARACTERS())
                .lowerCase(true)
                .upperCase(complexity.REQUIRES_LOWER_AND_UPPER)
                .specialCase(complexity.REQUIRES_SPECIAL)
                .amount(batchSize)
                .build();
    }

    private PasswordGenerationResponseDTO assertAndGetGenerationResponse(PasswordGenerationRequestDTO request, Complexity complexity, int batchSize)
            throws Exception {
        // Asserts the:
        // - HTML response code
        // - complexity specified in the response (no validation for individual passwords, this is done later in different method)
        // - response batch size

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post(GENERATION_URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(JSONParser.mapToJson(request))).andReturn();
        assertEquals(201, response.getResponse().getStatus());

        PasswordGenerationResponseDTO responseObject = JSONParser.mapFromJson(
                response.getResponse().getContentAsString(), PasswordGenerationResponseDTO.class);
        assertEquals(batchSize, responseObject.getPasswords().size());
        assertEquals(complexity, responseObject.getComplexity());

        return responseObject;
    }
}
