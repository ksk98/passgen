package com.exercise.passgen.Controllers.PasswordControllerTests;

import com.exercise.passgen.PasswordRules;
import com.exercise.passgen.enums.Complexity;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PasswordControllerGenerateTests {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    private final String URI = "/password/generate";

    @Test
    public void SuccessfulGeneration() throws Exception {
        int batchSize = 3;
        Complexity complexity = Complexity.MEDIUM;

        PasswordGenerationRequestDTO request = PasswordGenerationRequestDTO.builder()
                .length(complexity.getMINIMUM_CHARACTERS())
                .lowerCase(true)
                .upperCase(complexity.REQUIRES_LOWER_AND_UPPER)
                .specialCase(complexity.REQUIRES_SPECIAL)
                .amount(batchSize)
                .build();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(JSONParser.mapToJson(request))).andReturn();
        assertEquals(201, response.getResponse().getStatus());

        PasswordGenerationResponseDTO responseObject = JSONParser.mapFromJson(
                response.getResponse().getContentAsString(), PasswordGenerationResponseDTO.class);
        assertEquals(batchSize, responseObject.getPasswords().size());
        assertEquals(complexity, responseObject.getComplexity());
    }

    @Test
    public void BadRequestPasswordLengthNotEnough() throws Exception {
        PasswordGenerationRequestDTO request = PasswordGenerationRequestDTO.builder()
                .length(PasswordRules.MIN_CHARACTERS - 1)
                .lowerCase(true)
                .upperCase(true)
                .specialCase(false)
                .amount(1)
                .build();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(JSONParser.mapToJson(request))).andReturn();
        assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    public void BadRequestPasswordLengthTooLong() throws Exception {
        PasswordGenerationRequestDTO request = PasswordGenerationRequestDTO.builder()
                .length(PasswordRules.MAX_CHARACTERS + 1)
                .lowerCase(true)
                .upperCase(true)
                .specialCase(false)
                .amount(1)
                .build();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(JSONParser.mapToJson(request))).andReturn();
        assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    public void BadRequestPasswordBatchTooBig() throws Exception {
        PasswordGenerationRequestDTO request = PasswordGenerationRequestDTO.builder()
                .length(PasswordRules.MIN_CHARACTERS)
                .lowerCase(true)
                .upperCase(true)
                .specialCase(false)
                .amount(PasswordRules.MAX_PASSWORDS_AT_ONCE + 1)
                .build();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(JSONParser.mapToJson(request))).andReturn();
        assertEquals(400, response.getResponse().getStatus());
    }
}
