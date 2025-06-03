package com.alejandroacg.choicebound.utils;

import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.Map;

public class ConditionEvaluator {
    public static boolean evaluate(String condition, Map<String, Integer> values) {
        try {
            Expression expression = new Expression(condition);

            for (Map.Entry<String, Integer> entry : values.entrySet()) {
                expression.with(entry.getKey(), BigDecimal.valueOf(entry.getValue()));
            }

            return expression.eval().intValue() != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
