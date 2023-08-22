package com.example.kakao.cart;

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

@Import(ObjectMapper.class)
@DataJpaTest
class CartJPARepositoryTest extends DummyEntity {

    @Autowired
    private UserJPARepository userJPARepository;
    @Autowired
    private ProductJPARepository productJPARepository;
    @Autowired
    private OptionJPARepository optionJPARepository;
    @Autowired
    private CartJPARepository cartJPARepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setup() {
        User user = userJPARepository.save(newUser("ssar"));

        List<Product> productListPS = productJPARepository.saveAll(productDummyList());
        List<Option> optionList = optionJPARepository.saveAll(optionDummyList(productListPS));

        cartJPARepository.save(newCart(user,optionList.get(4),10));

        em.clear();
    }

    @Test
    public void cart_save_test() { // 6. 장바구니 담기 기능 테스트
        //given
        int optionId = 4;

        //when
        User user = newUser("ssar2");

        Optional<Option> option = optionJPARepository.findById(optionId);
        Cart cart = cartJPARepository.save(newCart(user, option.get(), 10));

        //then
        Assertions.assertThat(cart.getId()).isEqualTo(2);
        Assertions.assertThat(cart.getUser().getUsername()).isEqualTo("ssar2");
        Assertions.assertThat(cart.getOption().getId()).isEqualTo(4);
        Assertions.assertThat(cart.getQuantity()).isEqualTo(10);
    }

    @Test
    public void cart_findByUserId_test() throws JsonProcessingException { // 7. 장바구니 조회 기능 테스트 by userID
        //given
        int userId = 1;

        //when
        List<Cart> cartPSList = cartJPARepository.findCartByUserId(userId);
        Cart cart = cartPSList.get(0);

        //then
        Assertions.assertThat(cart.getId()).isEqualTo(1);
        Assertions.assertThat(cart.getUser().getId()).isEqualTo(userId);
    }

    @Test
    public void cart_update_test() throws JsonProcessingException { // 8. 장바구니 수정 기능 테스트
        //given
        int cartId = 1;

        //when
        Optional<Cart> cart = cartJPARepository.findById(cartId);
        cart.get().update(100, 100);

        em.flush(); // 영속성 컨텍스트에 있는 변경 내용을 데이터베이스에 동기화

        Optional<Cart> newCart = cartJPARepository.findById(cartId);

        //then
        Assertions.assertThat(newCart.get().getPrice()).isEqualTo(100);
        Assertions.assertThat(newCart.get().getQuantity()).isEqualTo(100);
    }

    @Test
    public void cart_delete_test() {
        // Given
        int userId = 1;

        // When
        cartJPARepository.deleteByUserId(userId);
        List<Cart> carts = cartJPARepository.findCartByUserId(1); // 빈 리스트 반환

        // Then
        Assertions.assertThat(carts).isEmpty();
    }
}