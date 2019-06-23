package com.crio.qeats.repositoryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.crio.qeats.QEatsApplication;
import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Item;
import com.crio.qeats.models.CartEntity;
import com.crio.qeats.utils.FixtureHelpers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Provider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;


@SpringBootTest(classes = {QEatsApplication.class})
class CartRepositoryServiceTest {

  private static final String FIXTURES = "fixtures/exchanges";

  private List<CartEntity> initialSetOfCarts = new ArrayList<>();

  @Autowired
  private CartRepositoryService cartRepositoryService;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  private Cart cartEntityToCart(CartEntity cartEntity) {
    ModelMapper modelMapper = modelMapperProvider.get();
    return modelMapper.map(cartEntity, Cart.class);
  }

  @BeforeEach
  public void setup() throws IOException {
    initialSetOfCarts = listOfCarts();

    for (CartEntity cartEntity : initialSetOfCarts) {
      mongoTemplate.save(cartEntity, "carts");
    }
  }

  @AfterEach
  public void teardown() {
    mongoTemplate.dropCollection("carts");
  }

  @Test
  public void createCartCreatesEmptyCartWithUserIdSet(@Autowired MongoTemplate mongoTemplate) {
    Cart emptyCart = new Cart();

    emptyCart.setUserId("arun");
    emptyCart.setRestaurantId("");

    cartRepositoryService.createCart(emptyCart);

    Optional<Cart> cartByUserId = cartRepositoryService.findCartByUserId("arun");

    assertEquals("arun", cartByUserId.get().getUserId());
    assertEquals("", cartByUserId.get().getRestaurantId());
  }

  @Test
  public void findCartByIdReturnsCartForValidCartId(@Autowired MongoTemplate mongoTemplate) {
    Cart cartById = cartRepositoryService.findCartByCartId("1");

    assertEquals("1", cartById.getId());
  }

  @Test
  public void restaurantIdIsEmptyAfterCreationAndSetWhenAddingFirstItem(
      @Autowired MongoTemplate mongoTemplate)
      throws IOException {
    Cart emptyCart = new Cart();

    emptyCart.setUserId("arun");
    emptyCart.setRestaurantId("");

    cartRepositoryService.createCart(emptyCart);

    Optional<Cart> cartByUserId = cartRepositoryService.findCartByUserId("arun");

    assertEquals("", cartByUserId.get().getRestaurantId());

    Item itemToBeAdded = dosai();
    cartRepositoryService.addItem(itemToBeAdded, cartByUserId.get().getId(),
        "10");

    Optional<Cart> cartAfterAddingItem = cartRepositoryService.findCartByUserId("arun");

    assertEquals("10", cartAfterAddingItem.get().getRestaurantId());
    assertEquals(75, cartAfterAddingItem.get().getTotal());

    cartRepositoryService.removeItem(itemToBeAdded, cartByUserId.get().getId(),
        "10");
    Optional<Cart> cartAfterRemovingItem = cartRepositoryService.findCartByUserId("arun");

    assertEquals("", cartAfterRemovingItem.get().getRestaurantId());
    assertEquals(0, cartAfterRemovingItem.get().getTotal());
  }

  @Test
  public void returnsCartIfUserHasActiveCart(@Autowired MongoTemplate mongoTemplate) {
    Optional<Cart> cart = cartRepositoryService.findCartByUserId("Bunny");

    assertEquals("Bunny", cart.get().getUserId());
    assertEquals(cart.get().toString(), cartEntityToCart(initialSetOfCarts.get(0)).toString());
  }

  @Test
  public void addingItemToCartShouldIncreaseCartTotal() throws Exception {
    Optional<Cart> cart = cartRepositoryService.findCartByUserId("Bunny");

    assert (cart.isPresent());
    assertEquals(225, cart.get().getTotal());

    cartRepositoryService
        .addItem(dosai(), cart.get().getId(), cart.get().getRestaurantId());

    Optional<Cart> cartAfterAddingItem = cartRepositoryService.findCartByUserId("Bunny");

    assert (cartAfterAddingItem.isPresent());
    assertEquals(300, cartAfterAddingItem.get().getTotal());
    assertEquals(2, cartAfterAddingItem.get().getItems().size());
  }


  @Test
  public void deletingItemFromCartShouldDecreaseCartTotal(@Autowired MongoTemplate mongoTemplate) {
    Optional<Cart> cart = cartRepositoryService.findCartByUserId("Bunny");

    assertEquals(225, cart.get().getTotal());

    cartRepositoryService
        .removeItem(cart.get().getItems().get(0), cart.get().getId(),
            cart.get().getRestaurantId());
    Optional<Cart> cartAfterItemRemoval = cartRepositoryService.findCartByUserId("Bunny");

    assertEquals(0, cartAfterItemRemoval.get().getItems().size());
    assertEquals(0, cartAfterItemRemoval.get().getTotal());
  }


  private Item dosai() throws IOException {
    String fixture =
        FixtureHelpers.fixture(FIXTURES + "/item_dosai.json");

    return objectMapper.readValue(fixture, Item.class);
  }

  private List<CartEntity> listOfCarts() throws IOException {
    String fixture =
        FixtureHelpers.fixture(FIXTURES + "/initial_data_set_carts.json");

    return objectMapper.readValue(fixture, new TypeReference<List<CartEntity>>() {
    });
  }

}