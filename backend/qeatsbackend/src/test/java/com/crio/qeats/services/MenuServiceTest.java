/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.crio.qeats.QEatsApplication;
import com.crio.qeats.dto.Item;
import com.crio.qeats.dto.Menu;
import com.crio.qeats.exceptions.ItemNotFoundInRestaurantMenuException;
import com.crio.qeats.exchanges.GetMenuResponse;
import com.crio.qeats.repositoryservices.MenuRepositoryService;
import com.crio.qeats.utils.FixtureHelpers;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
class MenuServiceTest {

  private static final String FIXTURES = "fixtures/exchanges";

  @InjectMocks
  private MenuServiceImpl menuService;

  @MockBean
  private MenuRepositoryService menuRepositoryServiceMock;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this);

    objectMapper = new ObjectMapper();
  }

  @Test
  void invalidRestaurantIdReturnsNull() {
    when(menuRepositoryServiceMock.findMenu("100")).thenReturn(null);

    GetMenuResponse getMenuResponse = menuService.findMenu("100");

    assertNull(getMenuResponse.getMenu());

    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

    verify(menuRepositoryServiceMock, times(1))
        .findMenu(argumentCaptor.capture());
    assertEquals(argumentCaptor.getValue(), "100");
  }

  @Test
  void validRestaurantIdReturnsRestaurantMenu() throws IOException {
    Menu expectedMenu = loadMenu();
    when(menuRepositoryServiceMock.findMenu("11")).thenReturn(expectedMenu);

    GetMenuResponse getMenuResponse = menuService.findMenu("11");

    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

    verify(menuRepositoryServiceMock, times(1)).findMenu(argumentCaptor.capture());
    assertEquals(argumentCaptor.getValue(), "11");

    assertEquals(getMenuResponse.getMenu().toString(), expectedMenu.toString());
  }

  @Test
  void getItemReturnsItemMatchingItemId() throws IOException {
    Menu expectedMenu = loadMenu();

    when(menuRepositoryServiceMock.findMenu("11")).thenReturn(expectedMenu);

    Item item = menuService.findItem("1", "11");

    ArgumentCaptor<String> restaurantId = ArgumentCaptor.forClass(String.class);
    verify(menuRepositoryServiceMock, times(1))
        .findMenu(restaurantId.capture());

    assertEquals("11", restaurantId.getValue());
    assert (item.getItemId().equals("1"));
  }

  @Test
  void getItemReturnsThrowsExceptionWhenItemMatchingTheIdIsNotFound() {
    Menu emptyMenu = new Menu();

    when(menuRepositoryServiceMock.findMenu(any(String.class))).thenReturn(emptyMenu);

    assertThrows(ItemNotFoundInRestaurantMenuException.class, () -> menuService
        .findItem("1", "10"));

    ArgumentCaptor<String> restaurantId = ArgumentCaptor.forClass(String.class);
    verify(menuRepositoryServiceMock, times(1))
        .findMenu(restaurantId.capture());
    assertEquals("10", restaurantId.getValue());
  }

  @Test
  void findItemTest() throws IOException {
    Menu menu = loadMenu();
    when(menuRepositoryServiceMock.findMenu("11")).thenReturn(menu);

    Item item = menuService.findItem("1", "11");

    assertEquals(menu.getItems().get(0).toString(), item.toString());
  }

  @Test
  void findItemThrowsItemNotFoundExceptionWhenItemIsNotFoundInRestaurant()
      throws IOException {
    Menu menu = loadMenu();
    when(menuRepositoryServiceMock.findMenu("11")).thenReturn(menu);

    Item item = menuService.findItem("1", "11");

    assertEquals(menu.getItems().get(0).toString(), item.toString());

    assertThrows(ItemNotFoundInRestaurantMenuException.class,
        () -> menuService.findItem("2", "11"));
  }

  private Menu loadMenu() throws IOException {
    String fixture =
        FixtureHelpers.fixture(FIXTURES + "/restaurant_menu.json");

    return objectMapper.readValue(fixture, Menu.class);
  }

}