package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order (XXToOne 관계 성능 최적화)
 * Ordrer -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

//    @GetMapping("/api/v1/simple-orders")
//    public List<Order> ordersV1() {
//        List<Order> all = orderRepository.findAll(new OrderSearch());
//        for (Order order : all) {
//            order.getMember().getName(); // Lazy 강제 초기화
//            order.getDelivery().getAddress(); // Lazy 강제 초기화
//        }
//        return all;
//    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        return orderRepository.findAll(new OrderSearch()).stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/v3/simple-orders")    // 페치 조인
    public List<SimpleOrderDto> ordersV3() {
        return orderRepository.findAllWithMemberDelivery().stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/v4/simple-orders") // 거의 재사용성이 없음 대신 성능면에서 v3보다느 나음
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrdersDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화 - 영속성 컨택스트에서 찾고 없으면 DB에 쿼리 날림
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화 - 영속성 컨택스트에서 찾고 없으면 DB에 쿼리 날림
        }
    }
}
