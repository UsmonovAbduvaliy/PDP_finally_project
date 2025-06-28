package com.example.pdp_project.dto.request;

import lombok.Value;


@Value
public class MessageDto {
    Integer from;
    Integer to;
    String content;
}
