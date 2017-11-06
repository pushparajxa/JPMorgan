package com.jpmorgan.salesprocessor.impl;

import com.jpmorgan.salesprocessor.api.Sale;
import com.jpmorgan.salesprocessor.api.SalesMessageProcessor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SalesMessageProcessorImplTest {

    @Test
    public void sendMessageWithJustASale() throws Sale.InvalidSaleValueException {
        SalesMessageProcessorImpl processor = new SalesMessageProcessorImpl();
        processor.sendMessage(new Sale("Apple",2));
        Assert.assertTrue(processor.getRecords().containsKey("Apple"));
        Assert.assertTrue(processor.getRecords().get("Apple").size() ==1);
        Assert.assertTrue(processor.getRecords().get("Apple").get(0).getRepetition() == 1);
        Assert.assertEquals(processor.getRecords().get("Apple").get(0).getSale(),new Sale("Apple",2));
        Assert.assertEquals(processor.getAdjustments().size(),0);
        Assert.assertTrue(processor.getTotalSales().get("Apple")==1);
        Assert.assertTrue(processor.getTotalValue().get("Apple")==2);
    }


    @Test
    public void sendMessageWithASaleAndRepetition() throws Sale.InvalidSaleValueException {
        SalesMessageProcessorImpl processor = new SalesMessageProcessorImpl();
        processor.sendMessage(new Sale("Apple",2),2);
        Assert.assertTrue(processor.getRecords().containsKey("Apple"));
        Assert.assertTrue(processor.getRecords().get("Apple").size() ==1);
        Assert.assertTrue(processor.getRecords().get("Apple").get(0).getRepetition() == 2);
        Assert.assertEquals(processor.getRecords().get("Apple").get(0).getSale(),new Sale("Apple",2));
        Assert.assertEquals(processor.getAdjustments().size(),0);
        Assert.assertTrue(processor.getTotalSales().get("Apple")==2);
        Assert.assertTrue(processor.getTotalValue().get("Apple")==4);
    }

    @Test
    public void sendMessageWithASaleAndADDAdjustment() throws Sale.InvalidSaleValueException {
        SalesMessageProcessorImpl processor = new SalesMessageProcessorImpl();
        //Send a simple Sale
        processor.sendMessage(new Sale("Apple",2));

        processor.sendMessage(new Sale("Apple",2),SalesMessageProcessor.AdjustmentOperation.ADD,10);
        Assert.assertTrue(processor.getRecords().containsKey("Apple"));
        Assert.assertEquals(processor.getAdjustments().size(),1);
        Assert.assertTrue(processor.getTotalSales().get("Apple")==2);
        Assert.assertTrue(processor.getTotalValue().get("Apple")==24);
    }


    @Test
    public void sendMessageWithASaleAndSUBTRACTAdjustment() throws Sale.InvalidSaleValueException {
        SalesMessageProcessorImpl processor = new SalesMessageProcessorImpl();
        //Send a simple Sale
        processor.sendMessage(new Sale("Apple",2));

        processor.sendMessage(new Sale("Apple",2),SalesMessageProcessor.AdjustmentOperation.SUBTRACT,1);
        Assert.assertTrue(processor.getRecords().containsKey("Apple"));
        Assert.assertEquals(processor.getAdjustments().size(),1);
        Assert.assertTrue(processor.getTotalSales().get("Apple")==2);
        Assert.assertTrue(processor.getTotalValue().get("Apple")==2);
    }


    @Test
    public void sendMessageWithASaleAndMULTIPLYAdjustment() throws Sale.InvalidSaleValueException {
        SalesMessageProcessorImpl processor = new SalesMessageProcessorImpl();
        //Send a simple Sale
        processor.sendMessage(new Sale("Apple",2));

        processor.sendMessage(new Sale("Apple",2),SalesMessageProcessor.AdjustmentOperation.MULTIPLY,5);
        Assert.assertTrue(processor.getRecords().containsKey("Apple"));
        Assert.assertEquals(processor.getAdjustments().size(),1);
        Assert.assertTrue(processor.getTotalSales().get("Apple")==2);
        Assert.assertTrue(processor.getTotalValue().get("Apple")==20);
    }

    //Above tests did the Unit Testing. Below tests do functional testing

    @Test
    public void test10Sales() throws Sale.InvalidSaleValueException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(arrayOutputStream,true));
        SalesMessageProcessorImpl processor = new SalesMessageProcessorImpl();
        for(int i=0;i<10;i++){
            processor.sendMessage(new Sale("Apple",1));
        }
        Assert.assertTrue(
                arrayOutputStream.toString().contains( "Product Type="+"Apple"+
                        ".Total sales="+10+". Total Value="+10)
        );
    }


    @Test
    public void test50SalesWithNoAdjustments() throws Sale.InvalidSaleValueException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(arrayOutputStream,true));
        SalesMessageProcessorImpl processor = new SalesMessageProcessorImpl();

        for(int i=0;i<50;i++){
            processor.sendMessage(new Sale("Apple",1));
        }
        Assert.assertTrue( arrayOutputStream.toString().contains( "Product Type="+"Apple"+
                        ".Total sales="+10+". Total Value="+10) );
        Assert.assertTrue( arrayOutputStream.toString().contains( "Product Type="+"Apple"+
                ".Total sales="+20+". Total Value="+20) );
        Assert.assertTrue( arrayOutputStream.toString().contains( "Product Type="+"Apple"+
                ".Total sales="+30+". Total Value="+30) );
        Assert.assertTrue( arrayOutputStream.toString().contains( "Product Type="+"Apple"+
                ".Total sales="+40+". Total Value="+40) );
        Assert.assertTrue( arrayOutputStream.toString().contains( "Product Type="+"Apple"+
                ".Total sales="+50+". Total Value="+50) );

       Assert.assertTrue(arrayOutputStream.toString().contains( SalesMessageProcessorImpl.PAUSE_APPLICATION));
       Assert.assertFalse(arrayOutputStream.toString().contains( SalesMessageProcessorImpl.ADJUSTMENT_LOG));
    }

    @Test
    public void test50SalesWithAdjustment() throws Sale.InvalidSaleValueException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(arrayOutputStream,true));
        SalesMessageProcessorImpl processor = new SalesMessageProcessorImpl();

        for(int i=0;i<49;i++){
            processor.sendMessage(new Sale("Apple",1));
        }
        //Send Adjustment Message
        processor.sendMessage(new Sale("Apple",1),SalesMessageProcessor.AdjustmentOperation.ADD,10);

        Assert.assertTrue( arrayOutputStream.toString().contains( "Product Type="+"Apple"+
                ".Total sales="+10+". Total Value="+10) );
        Assert.assertTrue( arrayOutputStream.toString().contains( "Product Type="+"Apple"+
                ".Total sales="+20+". Total Value="+20) );
        Assert.assertTrue( arrayOutputStream.toString().contains( "Product Type="+"Apple"+
                ".Total sales="+30+". Total Value="+30) );
        Assert.assertTrue( arrayOutputStream.toString().contains( "Product Type="+"Apple"+
                ".Total sales="+40+". Total Value="+40) );
        Assert.assertTrue( arrayOutputStream.toString().contains( "Product Type="+"Apple"+
                ".Total sales="+50+". Total Value="+550) );

        Assert.assertTrue(arrayOutputStream.toString().contains( SalesMessageProcessorImpl.PAUSE_APPLICATION));
        Assert.assertTrue(arrayOutputStream.toString().contains( SalesMessageProcessorImpl.ADJUSTMENT_LOG));
    }

}
