/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.crio.qeats.QEatsApplication;
import com.crio.qeats.dto.Menu;
import com.crio.qeats.models.MenuEntity;
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
class MenuRepositoryServiceTest {

  private static final String FIXTURES = "fixtures/exchanges";

  List<MenuEntity> menuEntities = new ArrayList<>();

  @Autowired
  private MenuRepositoryService menuRepositoryService;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @BeforeEach
  public void setup() throws IOException {
    menuEntities = listOfMenus();

    for (MenuEntity menuEntity : menuEntities) {
      mongoTemplate.save(menuEntity, "menus");
    }
  }

  @AfterEach
  public void teardown() {
    mongoTemplate.dropCollection("menus");
  }

  @Test
  public void getMenuTest(@Autowired MongoTemplate mongoTemplate) {
    assertNotNull(mongoTemplate);
    assertNotNull(menuRepositoryService);

    Menu menu = menuRepositoryService.findMenu("11");
    assertEquals("11", menu.getRestaurantId());

    menu = menuRepositoryService.findMenu("12");
    assertEquals("12", menu.getRestaurantId());

    menu = menuRepositoryService.findMenu("13");
    assertEquals("13", menu.getRestaurantId());

    menu = menuRepositoryService.findMenu("100");
    assertEquals(null, menu);
  }

  private List<MenuEntity> listOfMenus() throws IOException {
    String fixture =
        FixtureHelpers.fixture(FIXTURES + "/initial_data_set_menus.json");

    return objectMapper.readValue(fixture, new TypeReference<List<MenuEntity>>() {
    });
  }

}