package com.jpmorgan.salesprocessor.impl;

import com.jpmorgan.salesprocessor.api.SalesMessageProcessor.AdjustmentOperation;

class AdjustmentRecord {
    private AdjustmentOperation operation;
    private int value;

    AdjustmentRecord(AdjustmentOperation operation,int value){
        this.operation = operation;
        this.value = value;
    }


    public int getValue() {
        return value;
    }

    public AdjustmentOperation getOperation() {
        return operation;
    }

}
