package kz.project.mymarket.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class OrderDTO {

    private Long id;
    private Long userId;
    private String userFullname;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;
}