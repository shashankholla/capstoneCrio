/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Order;
import com.crio.qeats.models.OrderEntity;
import com.crio.qeats.repositories.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Provider;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderRepositoryServiceImpl implements OrderRepositoryService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @Override
  public Order placeOrder(Cart cart) {
    ModelMapper modelMapper = modelMapperProvider.get();

    OrderEntity order = modelMapper.map(cart, OrderEntity.class);
    orderRepository.save(order);
    return modelMapper.map(order, Order.class);
  }

  @Override
  public List<Order> getOrdersByRestaurant(String restaurantId) {
    ModelMapper modelMapper = modelMapperProvider.get();
    List<Order> orderList = new ArrayList<>();

    Optional<List<OrderEntity>> optionalOrderEntityList =
        orderRepository.findByRestaurantId(restaurantId);
    if(optionalOrderEntityList.isPresent()){
      List<OrderEntity> orderEntityList = optionalOrderEntityList.get();
      for(OrderEntity orderEntity : orderEntityList){
        orderList.add(modelMapper.map(orderEntity, Order.class));
      }
    }
    return orderList;
  }

  @Override
  public Order updateStatus(String restaurantId, String orderId,
                            String status) {
    ModelMapper modelMapper = modelMapperProvider.get();
    Order order = null;
    Optional<OrderEntity> optionalOrderEntity = orderRepository.findById(orderId);

    if(optionalOrderEntity.isPresent()){
      OrderEntity orderEntity = optionalOrderEntity.get();
      orderEntity.setStatus(status);
      orderRepository.save(orderEntity);
      order = modelMapper.map(orderEntity, Order.class);
    }
    return order;
  }

  @Override
  public Order getOrderById(String orderId) {
    ModelMapper modelMapper = modelMapperProvider.get();
    Order order = null;
    Optional<OrderEntity> optionalOrderEntity = orderRepository.findById(orderId);

    if(optionalOrderEntity.isPresent()){
      OrderEntity orderEntity = optionalOrderEntity.get();
      order = modelMapper.map(orderEntity, Order.class);
    }
    return order;
  }

}