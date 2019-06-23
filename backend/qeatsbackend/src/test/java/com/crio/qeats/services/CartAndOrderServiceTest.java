package com.crio.qeats.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.crio.qeats.QEatsApplication;
import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Item;
import com.crio.qeats.dto.Order;
import com.crio.qeats.exceptions.ItemNotFoundInRestaurantMenuException;
import com.crio.qeats.exceptions.ItemNotFromSameRestaurantException;
import com.crio.qeats.exchanges.CartModifiedResponse;
import com.crio.qeats.repositoryservices.CartRepositoryService;
import com.crio.qeats.repositoryservices.OrderRepositoryService;
import com.crio.qeats.utils.FixtureHelpers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {QEatsApplication.class})
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class CartAndOrderServiceTest {

  private static final String FIXTURES = "fixtures/exchanges";

  @InjectMocks
  private CartAndOrderServiceImpl cartAndOrderService;

  @MockBean
  private CartRepositoryService cartRepositoryService;

  @MockBean
  private OrderRepositoryService orderRepositoryService;

  @MockBean
  private MenuService menuService;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this);

    objectMapper = new ObjectMapper();
  }

  private Cart createEmptyCartForUserId(String userId) {
    Cart cart = new Cart();
    cart.setUserId(userId);
    cart.setRestaurantId("");

    return cart;
  }

  @Test
  void userDoesNotHaveAnActiveCartThenCreateAnEmptyCartAndReturn() {
    Cart expectedCart = createEmptyCartForUserId("arun");

    when(cartRepositoryService.findCartByUserId(any(String.class)))
        .thenReturn(Optional.empty());
    when(cartRepositoryService.findCartByCartId(any()))
        .thenReturn(expectedCart);

    doReturn(expectedCart.getRestaurantId()).when(cartRepositoryService)
        .createCart(any(Cart.class));

    Cart actualCart = cartAndOrderService.findOrCreateCart("arun");

    assertEquals(expectedCart.getRestaurantId(), actualCart.getRestaurantId());
    assertEquals(expectedCart.getUserId(), actualCart.getUserId());

    ArgumentCaptor<Cart> argumentCartCaptor = ArgumentCaptor.forClass(Cart.class);
    verify(cartRepositoryService, times(1))
        .createCart(argumentCartCaptor.capture());
    assertEquals(expectedCart.toString(), argumentCartCaptor.getValue().toString());
    assertEquals(expectedCart.toString(), argumentCartCaptor.getValue().toString());

    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    verify(cartRepositoryService, times(1))
        .findCartByUserId(argumentCaptor.capture());
    assertEquals("arun", argumentCaptor.getValue());
    verify(cartRepositoryService, times(1))
        .findCartByCartId(any());
  }

  @Test
  void validUserIdGetsCart() throws IOException {

    Cart cart = loadSampleCart();

    when(cartRepositoryService.findCartByUserId("arun")).thenReturn(Optional.of(cart));

    Cart actualCart = cartAndOrderService.findOrCreateCart("arun");

    assertEquals(cart.toString(), actualCart.toString());
  }

  @Test
  void addItemToCart() throws Exception {
    Cart cart = loadSampleCart();
    Item item = loadSampleItem();

    when(menuService.findItem(any(String.class), any(String.class))).thenReturn(item);
    when(cartRepositoryService.findCartByCartId(any(String.class))).thenReturn(cart);

    cartAndOrderService
        .addItemToCart(item.getItemId(), cart.getRestaurantId(), cart.getRestaurantId());

    ArgumentCaptor<String> itemId = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> restaurantId = ArgumentCaptor.forClass(String.class);

    verify(menuService, times(1)).findItem(
        itemId.capture(), restaurantId.capture());

    ArgumentCaptor<Item> itemArgumentCaptor = ArgumentCaptor.forClass(Item.class);
    ArgumentCaptor<String> cartId = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> cartRestaurantId = ArgumentCaptor.forClass(String.class);

    verify(cartRepositoryService, times(1)).addItem(
        itemArgumentCaptor.capture(), cartId.capture(), cartRestaurantId.capture());

    assertEquals(item.getItemId(), itemId.getValue());
    assertEquals(cart.getRestaurantId(), restaurantId.getValue());
  }

  @Test
  void addItemToCartInvalidResponse() throws Exception {
    Cart cart = loadSampleCart();
    Item item = loadSampleItem();
    when(cartRepositoryService.findCartByCartId(any(String.class))).thenReturn(cart);

    CartModifiedResponse response = cartAndOrderService
        .addItemToCart(item.getItemId(), cart.getRestaurantId(), "12");


    assertEquals(102, response.getCartResponseType());
  }


  @Test
  void removeItemFromCart() throws Exception {
    Cart cart = loadSampleCart();
    Item item = loadSampleItem();

    when(menuService.findItem(any(String.class), any(String.class))).thenReturn(item);
    cartAndOrderService
        .removeItemFromCart(item.getItemId(), cart.getRestaurantId(), cart.getRestaurantId());

    ArgumentCaptor<String> itemId = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> restaurantId = ArgumentCaptor.forClass(String.class);

    verify(menuService, times(1)).findItem(
        itemId.capture(), restaurantId.capture());

    ArgumentCaptor<Item> itemArgumentCaptor = ArgumentCaptor.forClass(Item.class);
    ArgumentCaptor<String> cartId = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> cartRestaurantId = ArgumentCaptor.forClass(String.class);

    verify(cartRepositoryService, times(1)).removeItem(
        itemArgumentCaptor.capture(), cartId.capture(), cartRestaurantId.capture());

    assertEquals(item.getItemId(), itemId.getValue());
    assertEquals(cart.getRestaurantId(), restaurantId.getValue());
  }


  @Test
  void placeOrderTest() throws IOException {
    Cart cart = loadSampleCart();
    when(cartRepositoryService.findCartByCartId(any(String.class))).thenReturn(cart);

    cartAndOrderService.postOrder(cart.getRestaurantId());

    ArgumentCaptor<Cart> cartArgumentCaptor = ArgumentCaptor.forClass(Cart.class);
    verify(orderRepositoryService, times(1))
        .placeOrder(cartArgumentCaptor.capture());

    assertEquals(cart.toString(), cartArgumentCaptor.getValue().toString());
    // ArgumentCaptor<String> cartIdCapture = ArgumentCaptor.forClass(String.class);
    // verify(cartRepositoryService, times(1)).clear(cartIdCapture.capture());

    // assertEquals(cartIdCapture.getValue(), cart.getRestaurantId());
  }


  private Item loadSampleItem() throws IOException {
    String fixture =
        FixtureHelpers.fixture(FIXTURES + "/item_dosai.json");

    return objectMapper.readValue(fixture, Item.class);
  }

  private Cart loadSampleCart() throws IOException {
    String fixture =
        FixtureHelpers.fixture(FIXTURES + "/get_cart_response.json");

    return objectMapper.readValue(fixture, Cart.class);
  }

  private List<Order> loadOrderList() throws IOException {
    String fixture =
        FixtureHelpers.fixture(FIXTURES + "/initial_data_set_orders.json");

    return objectMapper.readValue(fixture,
        new TypeReference<List<Order>>() {
        });
  }

}
