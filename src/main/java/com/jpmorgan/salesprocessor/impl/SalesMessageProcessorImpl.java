package com.jpmorgan.salesprocessor.impl;

import com.jpmorgan.salesprocessor.api.Sale;
import com.jpmorgan.salesprocessor.api.SalesMessageProcessor;

import java.util.*;

public class SalesMessageProcessorImpl implements SalesMessageProcessor {

    protected static final String PAUSE_APPLICATION = "The Application is Pausing. It will not accept new messages.";
    protected static final String ADJUSTMENT_LOG = "Logging Adjustments done for each product type.";


    private Hashtable<String,List<SaleRecord>> records = new Hashtable<>();
    private Hashtable<String,Long> totalValue = new Hashtable<>();
    private Hashtable<String,Long> totalSales = new Hashtable<>();
    private Hashtable<String,List<AdjustmentRecord>> adjustments = new Hashtable<>();

    private long messageCount = 0;
    private boolean acceptMessages = true;


    @Override
    public void sendMessage(Sale sale) {
        if(!acceptMessages)
            return;
        messageCount++;
        handleSale(sale,1);
        checkMessageCount();
    }

    @Override
    public void sendMessage(Sale sale,int repetitions) {
        if(!acceptMessages)
            return;
        messageCount++;
        handleSale(sale,repetitions);
        checkMessageCount();
    }

    @Override
    public void sendMessage(Sale sale,AdjustmentOperation adjustmentOperation,int adjustmentValue) {
        if(!acceptMessages)
            return;
        messageCount++;
        handleSale(sale,1);
        handleAdjustment(sale.getType(),adjustmentOperation,adjustmentValue);
        checkMessageCount();
    }


    private void handleAdjustment(String productType,AdjustmentOperation adjustmentOperation,int adjustmentValue) {
        AdjustmentOperation operation = adjustmentOperation;
        ListIterator<SaleRecord> iterator = records.get(productType).listIterator();
        long totalModification = 0;

        while(iterator.hasNext()) {
            SaleRecord saleRecord = iterator.next();
            Sale sale = saleRecord.getSale();
            long value = sale.getValue();

            switch(operation) {
                case ADD:
                    sale.setValue(value+adjustmentValue);
                    totalModification += saleRecord.getRepetition()*adjustmentValue;
                    break;
                case MULTIPLY:
                    sale.setValue(value*adjustmentValue);
                    totalModification += saleRecord.getRepetition()*(adjustmentValue-1)*value;
                    break;
                case SUBTRACT:
                    sale.setValue(value-adjustmentValue);
                    totalModification -= saleRecord.getRepetition()*adjustmentValue;
            }
        }
        //Alter the totalvalue of sales
        totalValue.put(productType,totalValue.get(productType)+totalModification);

        insertAdjustmentRecord(productType,operation,adjustmentValue);
    }

    private void insertAdjustmentRecord(String productType,AdjustmentOperation adjustmentOperation,int adjustmentValue) {
        AdjustmentRecord adjustmentRecord = new AdjustmentRecord(adjustmentOperation,adjustmentValue);

        if(adjustments.containsKey(productType)) {
            adjustments.get(productType).add(adjustmentRecord);
        }else {
            LinkedList<AdjustmentRecord> adjustmentRecords = new LinkedList<>();
            adjustmentRecords.add(adjustmentRecord);
            adjustments.put(productType,adjustmentRecords);
        }
    }

    private void handleSale(Sale sale,int repetitions) {
        String productType = sale.getType();
        SaleRecord saleRecord = new SaleRecord(sale,repetitions);

        if(records.containsKey(productType)) {
            records.get(productType).add(saleRecord);
            totalValue.put(productType,totalValue.get(productType)+repetitions*sale.getValue());
            totalSales.put(productType,totalSales.get(productType)+repetitions);
        }else {
            LinkedList<SaleRecord> saleRecords = new LinkedList<>();
            saleRecords.add(saleRecord);
            records.put(productType,saleRecords);
            totalValue.put(productType,repetitions*sale.getValue());
            totalSales.put(productType,new Long(repetitions));
        }
    }

    private void checkMessageCount() {
        if(messageCount%10==0) {
            for(String productType : totalSales.keySet()) {
                System.out.println("Product Type="+productType+
                        ".Total sales="+totalSales.get(productType)+". Total Value="+totalValue.get(productType));
            }
        }
        if(messageCount%50==0) {
            acceptMessages = false;
            System.out.println(PAUSE_APPLICATION);
            if(adjustments.isEmpty()) {
                return;
            }
            else{
                System.out.println(ADJUSTMENT_LOG);
                for(String productTYpe : adjustments.keySet()) {
                    System.out.println("Product Type: "+productTYpe);
                    for(AdjustmentRecord record : adjustments.get(productTYpe)) {
                        System.out.println(" Operation ="+record.getOperation()+". Value = "+record.getValue());
                    }
                }
            }
        }
    }

    protected Hashtable<String,List<SaleRecord>> getRecords() {
        return records;
    }

    protected Hashtable<String,Long> getTotalValue() {
        return totalValue;
    }

    protected Hashtable<String,Long> getTotalSales() {
        return totalSales;
    }

    protected Hashtable<String,List<AdjustmentRecord>> getAdjustments() {
        return adjustments;
    }

    protected long getMessageCount() {
        return messageCount;
    }

    protected boolean isAcceptMessages() {
        return acceptMessages;
    }


    protected void setRecords(Hashtable<String,List<SaleRecord>> records) {
        this.records = records;
    }

    protected void setTotalValue(Hashtable<String,Long> totalValue) {
        this.totalValue = totalValue;
    }

    protected void setTotalSales(Hashtable<String,Long> totalSales) {
        this.totalSales = totalSales;
    }

    protected void setAdjustments(Hashtable<String,List<AdjustmentRecord>> adjustments) {
        this.adjustments = adjustments;
    }

    protected void setMessageCount(long messageCount) {
        this.messageCount = messageCount;
    }

    protected void setAcceptMessages(boolean acceptMessages) {
        this.acceptMessages = acceptMessages;
    }


}
