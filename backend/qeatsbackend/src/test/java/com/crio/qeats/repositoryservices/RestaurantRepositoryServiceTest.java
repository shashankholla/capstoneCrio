/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.crio.qeats.QEatsApplication;
import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.globals.GlobalConstants;
import com.crio.qeats.models.RestaurantEntity;
import com.crio.qeats.utils.FixtureHelpers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalTime;
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

// TODO: CRIO_TASK_MODULE_NOSQL
// Pass all the RestaurantRepositoryService test cases.
// Make modifications to the tests if necessary.
@SpringBootTest(classes = {QEatsApplication.class})
public class RestaurantRepositoryServiceTest {

  private static final String FIXTURES = "fixtures/exchanges";
  List<RestaurantEntity> allRestaurants = new ArrayList<>();
  @Autowired
  private RestaurantRepositoryService restaurantRepositoryService;
  @Autowired
  private MongoTemplate mongoTemplate;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @BeforeEach
  void setup() throws IOException {
    allRestaurants = listOfRestaurants();

    for (RestaurantEntity restaurantEntity : allRestaurants) {
      mongoTemplate.save(restaurantEntity, "restaurants");
    }
  }

  @AfterEach
  void teardown() {
    mongoTemplate.dropCollection("restaurants");
    GlobalConstants.destroyCache();
  }

  @Test
  void restaurantsCloseByAndOpenNow(@Autowired MongoTemplate mongoTemplate) {
    assertNotNull(mongoTemplate);
    assertNotNull(restaurantRepositoryService);

    List<Restaurant> allRestaurantsCloseBy = restaurantRepositoryService
        .findAllRestaurantsCloseBy(20.0, 30.0, LocalTime.of(18, 01), 3.0);

    ModelMapper modelMapper = modelMapperProvider.get();
    assertEquals(2, allRestaurantsCloseBy.size());
    assertEquals("11", allRestaurantsCloseBy.get(0).getRestaurantId());
    assertEquals("12", allRestaurantsCloseBy.get(1).getRestaurantId());
  }

  @Test
  void noRestaurantsNearBy(@Autowired MongoTemplate mongoTemplate) {
    assertNotNull(mongoTemplate);
    assertNotNull(restaurantRepositoryService);

    List<Restaurant> allRestaurantsCloseBy = restaurantRepositoryService
        .findAllRestaurantsCloseBy(20.9, 30.0, LocalTime.of(18, 00), 3.0);

    ModelMapper modelMapper = modelMapperProvider.get();
    assertEquals(0, allRestaurantsCloseBy.size());
  }

  @Test
  void tooEarlyNoRestaurantIsOpen(@Autowired MongoTemplate mongoTemplate) {
    assertNotNull(mongoTemplate);
    assertNotNull(restaurantRepositoryService);

    List<Restaurant> allRestaurantsCloseBy = restaurantRepositoryService
        .findAllRestaurantsCloseBy(20.0, 30.0, LocalTime.of(17, 59), 3.0);

    ModelMapper modelMapper = modelMapperProvider.get();
    assertEquals(0, allRestaurantsCloseBy.size());
  }

  @Test
  void tooLateNoRestaurantIsOpen(@Autowired MongoTemplate mongoTemplate) {
    assertNotNull(mongoTemplate);
    assertNotNull(restaurantRepositoryService);

    List<Restaurant> allRestaurantsCloseBy = restaurantRepositoryService
        .findAllRestaurantsCloseBy(20.0, 30.0, LocalTime.of(23, 01), 3.0);

    ModelMapper modelMapper = modelMapperProvider.get();
    assertEquals(0, allRestaurantsCloseBy.size());
  }

  @Test
  void findRestaurantsByName(@Autowired MongoTemplate mongoTemplate) {
    assertNotNull(mongoTemplate);
    assertNotNull(restaurantRepositoryService);

    String searchFor = "A2B";
    List<Restaurant> foundRestaurantsList = restaurantRepositoryService
        .findRestaurantsByName(20.8, 30.1, searchFor,
            LocalTime.of(20, 0), 5.0);

    assertEquals(2, foundRestaurantsList.size());
  }

  @Test
  void foundRestaurantsExactMatchesFirst(@Autowired MongoTemplate mongoTemplate) {
    assertNotNull(mongoTemplate);
    assertNotNull(restaurantRepositoryService);

    String searchFor = "A2B";
    List<Restaurant> foundRestaurantsList = restaurantRepositoryService
        .findRestaurantsByName(20.8, 30.1, searchFor,
            LocalTime.of(20, 0), 5.0);

    assertEquals(2, foundRestaurantsList.size());
    assertEquals("A2B", foundRestaurantsList.get(0).getName());
    assertEquals("A2B Adyar Ananda Bhavan", foundRestaurantsList.get(1).getName());
  }

  void searchedAttributesIsSubsetOfRetrievedRestaurantAttributes() {
    // TODO
  }

  void searchedAttributesIsCaseInsensitive() {
    // TODO
  }

  private List<RestaurantEntity> listOfRestaurants() throws IOException {
    String fixture =
        FixtureHelpers.fixture(FIXTURES + "/initial_data_set_restaurants.json");

    return objectMapper.readValue(fixture, new TypeReference<List<RestaurantEntity>>() {
    });
  }
}
