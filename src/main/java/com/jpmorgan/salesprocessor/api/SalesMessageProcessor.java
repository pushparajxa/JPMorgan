package com.jpmorgan.salesprocessor.api;

public interface SalesMessageProcessor {

    enum AdjustmentOperation{
        ADD,SUBTRACT, MULTIPLY
    }

    void sendMessage(Sale sale);

    void sendMessage(Sale sale,int repetitions);

    void sendMessage(Sale sale, AdjustmentOperation adjustmentOperation,int adjustmentValue);

}
