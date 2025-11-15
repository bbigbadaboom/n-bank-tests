package Common.Extensions;

import API.Models.BaseModel;
import Common.Anotations.Mock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class FraudCheckWireMockExtension implements BeforeEachCallback, AfterEachCallback {
    
    private WireMockServer wireMockServer;
    
    @Override
    public void beforeEach(ExtensionContext context) throws JsonProcessingException {
        // Find the FraudCheckMock annotation on the test method or class
        Mock mockConfig = context.getTestMethod()
                .map(method -> method.getAnnotation(Mock.class))
                .orElseGet(() -> context.getTestClass()
                        .map(clazz -> clazz.getAnnotation(Mock.class))
                        .orElse(null));
        
        if (mockConfig != null) {
            setupWireMock(mockConfig);
        }
    }

    private void setupWireMock(Mock config) throws JsonProcessingException {
        wireMockServer = new WireMockServer(
                WireMockConfiguration.wireMockConfig().port(config.port())
        );
        wireMockServer.start();
        WireMock.configureFor("0.0.0.0", config.port());

        // 1. Создаем пустой объект нужного класса
        BaseModel response = createResponse(config);

        // 2. Сериализуем в JSON
        String body = new ObjectMapper().writeValueAsString(response);

        // 3. Мокаем endpoint
        stubFor(post(urlPathMatching(config.endpoint()))
                .willReturn(aResponse()
                        .withStatus(config.statusCode())
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));
    }

    private BaseModel createResponse(Mock config) {
        try {
            BaseModel model = config.responseClass().getDeclaredConstructor().newInstance();

            // применяем overrides
            for (String override : config.overrides()) {
                String[] parts = override.split("=");
                String field = parts[0].trim();
                String value = parts[1].trim();

                Field f = model.getClass().getDeclaredField(field);
                f.setAccessible(true);

                Object typedValue = castValue(f.getType(), value);
                f.set(model, typedValue);
            }

            return model;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create response", e);
        }
    }

    private Object castValue(Class<?> type, String value) {
        if (type.equals(int.class) || type.equals(Integer.class)) return Integer.parseInt(value);
        if (type.equals(double.class) || type.equals(Double.class)) return Double.parseDouble(value);
        if (type.equals(boolean.class) || type.equals(Boolean.class)) return Boolean.parseBoolean(value);
        return value; // String or unknown
    }

    
    @Override
    public void afterEach(ExtensionContext context) {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    public String getBaseUrl() {
        if (wireMockServer != null) {
            return "http://host.docker.internal:" + wireMockServer.port();
        }
        return null;
    }}
