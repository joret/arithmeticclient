package com.bloomberg.client.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Operation {
    private Character operation;
    private List<Double> numbers;

    public String toFallbackJsonObject(){
        FallbackExpression result = new FallbackExpression(null);

        if (numbers != null && numbers.size() == 2 && !Character.isWhitespace(operation)){
            var expressionList = new ArrayList<String>();
            expressionList.add( "" + numbers.get(0) + operation + numbers.get(1));
            result = new FallbackExpression(expressionList);

        }
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            String str = objectMapper.writeValueAsString(result);

            return str;
        } catch(Exception e){
            return "";
        }
    }
}
