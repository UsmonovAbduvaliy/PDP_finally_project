package com.example.pdp_project.dto.request;

import lombok.Value;


@Value
public class MessageDto {
    Long from;
    Long to;
    String content;
}
