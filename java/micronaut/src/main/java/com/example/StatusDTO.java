package com.example;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

record StatusDTO(String status,
                 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
                 LocalDateTime now) {
}
