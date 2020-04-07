package com.bloomberg.client.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FallbackExpression {
    List<String> expr;
}
