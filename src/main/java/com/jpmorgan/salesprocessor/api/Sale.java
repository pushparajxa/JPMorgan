package com.jpmorgan.salesprocessor.api;

import java.io.Serializable;

public class Sale implements Serializable{
    private String type;
    private long value;

    public Sale(String type ,int value) throws InvalidSaleValueException {
        this.type = type;
        this.value = value;
        validateSale(value);
    }

    private void validateSale(long sale) throws InvalidSaleValueException {
        if(value<0)
            throw new InvalidSaleValueException("Sale Value has to greater than  zero");
    }


    public class InvalidSaleValueException extends Exception{

        public InvalidSaleValueException(String s) {
            super(s);
        }
    }


    public String getType() {
        return type;
    }

    public long getValue() {
        return value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object other){
        Sale otherSale = (Sale)other;
        return otherSale.getType().equals(this.type) && (otherSale.value == this.value);
    }

    @Override
    public int hashCode(){
        return this.getType().hashCode()+(int)this.value;
    }
}
