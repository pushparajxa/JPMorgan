package com.jpmorgan.salesprocessor.impl;

import com.jpmorgan.salesprocessor.api.Sale;

 class SaleRecord {
    private Sale sale;
    private int repetition;
    SaleRecord(Sale sale, int repetition){
        this.sale = sale;
        this.repetition = repetition;
    }

     public Sale getSale() {
         return sale;
     }


     public int getRepetition() {
         return repetition;
     }

 }
