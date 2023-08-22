package com.example.kakao.order;

import com.example.kakao._core.util.DummyEntity;
import com.example.kakao.product.Product;
import com.example.kakao.product.ProductJPARepository;
import com.example.kakao.product.option.Option;
import com.example.kakao.product.option.OptionJPARepository;
import com.example.kakao.user.User;
import com.example.kakao.user.UserJPARepository;
import com.example.kakao.cart.Cart;
import com.example.kakao.cart.CartJPARepository;
import com.example.kakao.order.Order;
import com.example.kakao.order.OrderJPARepository;
import com.example.kakao.order.item.Item;
import com.example.kakao.order.item.ItemJPARepository;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;



@DataJpaTest
public class ItemJPARepositoryTest extends DummyEntity {
    @Autowired
    private UserJPARepository userJPARepository;
    @Autowired
    private OrderJPARepository orderJPARepository;
    @Autowired
    private  ProductJPARepository productJPARepository;
    @Autowired
    private  ItemJPARepository itemJPARepository;
    @Autowired
    private  OptionJPARepository optionJPARepository;
    @Autowired
    private  CartJPARepository cartJPARepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        User user = newUser("ssar");
        userJPARepository.save(user);

        Product product = newProduct("기본에 슬라이딩 지퍼백 크리스마스/플라워에디션 에디션 외 주방용품 특가전", 1, 1000);
        productJPARepository.save(product);

        Option option = newOption(product,"01. 슬라이딩 지퍼백 크리스마스에디션 4종", 10000);
        optionJPARepository.save(option);

        Cart cart = newCart(user, option, 5);
        cartJPARepository.save(cart);

        Order order = newOrder(user);
        orderJPARepository.save(order);

        Item item = newItem(cart, order);
        itemJPARepository.save(item);

        em.clear();
    }

    @Test
    public void order_findByOrderId_test() throws JsonProcessingException{
        //given
        int orderId = 1;

        //when
        List<Item> itemList = itemJPARepository.findItemByOrderId(orderId);
        Item item = itemList.get(0);

        //then
        Assertions.assertThat(item.getOrder().getId()).isEqualTo(orderId);
        Assertions.assertThat(item.getOrder().getUser().getUsername()).isEqualTo("ssar");
        Assertions.assertThat(item.getPrice()).isEqualTo(50000);
        Assertions.assertThat(item.getQuantity()).isEqualTo(5);
        Assertions.assertThat(item.getOption().getOptionName()).isEqualTo("01. 슬라이딩 지퍼백 크리스마스에디션 4종");
        Assertions.assertThat(item.getOption().getProduct().getProductName()).isEqualTo("기본에 슬라이딩 지퍼백 크리스마스/플라워에디션 에디션 외 주방용품 특가전");
    }
}