package org.example.msa1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewUpdateDTO {

    @NotBlank
    private String text;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;
}