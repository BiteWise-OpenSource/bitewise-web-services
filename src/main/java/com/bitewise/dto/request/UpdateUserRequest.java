package com.bitewise.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    private String firstName;

    private String lastName;

    private Integer age;

    private Double weight;

    private Double height;

    private String gender;
}
