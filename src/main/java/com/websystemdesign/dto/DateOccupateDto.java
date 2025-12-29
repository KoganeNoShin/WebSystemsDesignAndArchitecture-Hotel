package com.websystemdesign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DateOccupateDto {
    private LocalDate from;
    private LocalDate to;
}
