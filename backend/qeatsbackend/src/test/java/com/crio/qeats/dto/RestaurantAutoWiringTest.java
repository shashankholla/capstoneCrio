package com.crio.qeats.dto;

import com.crio.qeats.QEatsApplication;
import com.crio.qeats.dto.config.ObjectMapperBeanConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

// TODO: CRIO_TASK_MODULE_DEBUG - Pass serializeAndDeserializeRestaurantJson()
// Use Spring's Dependency Injection to pass the test case.
// You should not alter the code INSIDE the RestaurantAutoWiringTest class.
// Annotations are the way to go for this module.
@SpringBootTest
@ContextConfiguration(classes = ObjectMapperBeanConfig.class)
class RestaurantAutoWiringTest {
  //////////// NO_CODE_CHANGES_ALLOWED WITHIN THE RestaurantAutoWiringTest CLASS ////////////
  @Test
  public void serializeAndDeserializeRestaurantJson(@Autowired ObjectMapper objectMapper)
      throws IOException, JSONException {
    final String jsonString =
        "{\n"
            + "  \"restaurantId\": \"10\",\n"
            + "  \"name\": \"A2B\",\n"
            + "  \"city\": \"Hsr Layout\",\n"
            + "  \"imageUrl\": \"www.google.com\",\n"
            + "  \"latitude\": 20.027,\n"
            + "  \"longitude\": 30.0,\n"
            + "  \"opensAt\": \"18:00\",\n"
            + "  \"closesAt\": \"23:00\",\n"
            + "  \"attributes\": [\n"
            + "    \"Tamil\",\n"
            + "    \"South Indian\"\n"
            + "  ]\n"
            + "}";

    // Setting up a restaurant object for testing. The following ensures that restaurant
    // object can deserialize the right restaurant json.
    Restaurant restaurant = new Restaurant();
    // restaurant = new ObjectMapper().readValue(jsonString, Restaurant.class);
    restaurant = objectMapper.readValue(jsonString, Restaurant.class);

    // Deserialize the Json string from Restaurant class to ensure its done cleanly.
    String actualJsonString = "";
    actualJsonString = new ObjectMapper().writeValueAsString(restaurant);
    JSONAssert.assertEquals(jsonString, actualJsonString, true);
  }
  //////////// NO_CODE_CHANGES_ALLOWED WITHIN THE RestaurantAutoWiringTest CLASS ////////////
}
