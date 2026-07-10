package com.bitewise.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String role;

    private Integer age;

    private Double weight;

    private Double height;

    private String gender;

    private LocalDateTime createdAt;
}
