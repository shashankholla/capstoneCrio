package com.crio.qeats.repositoryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.crio.qeats.QEatsApplication;
import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Order;
import com.crio.qeats.models.OrderEntity;
import com.crio.qeats.utils.FixtureHelpers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest(classes = {QEatsApplication.class})
class OrderRepositoryServiceTest {

  private static final String FIXTURES = "fixtures/exchanges";

  private List<OrderEntity> initialSetOfOrders = new ArrayList<>();

  @Autowired
  private OrderRepositoryService orderRepositoryService;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  private Order orderEntityToOrder(OrderEntity orderEntity) {
    ModelMapper modelMapper = modelMapperProvider.get();
    return modelMapper.map(orderEntity, Order.class);
  }

  @BeforeEach
  public void setup() throws IOException {
    initialSetOfOrders = listOfOrders();

    for (OrderEntity orderEntity : initialSetOfOrders) {
      mongoTemplate.save(orderEntity, "orders");
    }
  }

  @AfterEach
  public void teardown() {
    mongoTemplate.dropCollection("orders");
  }


  private List<OrderEntity> listOfOrders() throws IOException {
    String fixture =
        FixtureHelpers.fixture(FIXTURES + "/initial_data_set_orders.json");

    return objectMapper.readValue(fixture, new TypeReference<List<OrderEntity>>() {
    });
  }

  private Cart loadSampleCart() throws IOException {
    String fixture =
        FixtureHelpers.fixture(FIXTURES + "/initial_data_set_carts.json");

    List<Cart> allCarts = objectMapper.readValue(fixture, new TypeReference<List<Cart>>() {
    });

    return allCarts.get(0);
  }

  @Test
  void placeOrderTest(@Autowired MongoTemplate mongoTemplate) throws IOException {
    Cart cart = loadSampleCart();
    assertNotNull(mongoTemplate);
    assertNotNull(orderRepositoryService);

    Order placedOrder = orderRepositoryService.placeOrder(cart);

    assertEquals(cart.getTotal(), placedOrder.getTotal());
  }
}