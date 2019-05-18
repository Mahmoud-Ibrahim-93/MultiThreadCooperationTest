/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication26;

import java.util.ArrayList;
/**
 *
 * @author Mahmoud Ibrahim
 */import java.util.concurrent.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadCooperation {
     public static Lock lock = new ReentrantLock();
     public static Condition orderReady = lock.newCondition();

    public static void main(String[] args) {
      ExecutorService ex = Executors.newFixedThreadPool(2);
      final ExecutorService ex1 = ex;
      ex1.execute(new ReceiveOrder());
      ex.execute(new ServeOrder());
      ex.shutdown();
    }
private static class ReceiveOrder implements Runnable{
        static int OrderNumber;
        @Override
        public void run() {
            while(true){
                 MealLine.add(OrderNumber);
                 System.out.println("Order is made with number "+ (OrderNumber++));
            }
        }

}
private static class ServeOrder implements Runnable{
        static int x;
        @Override
        public void run() {
              while(true){
                 int x=MealLine.removeOrder();
                 System.out.println("Order is being served "+x);
            }
        }
}

private static class MealLine{
static ArrayList<Integer> meals = new ArrayList<>();
static int Capacity =10;
static Lock lock = new ReentrantLock();
static Condition notFull = lock.newCondition();
static Condition notEmpty = lock.newCondition();

public static void add(int number){
    
    lock.lock();  
    try{
    while(meals.size()==Capacity){
        notFull.await();
    }
        Integer order = new Integer(number);
        meals.add(order);
        if(meals.size()>0)
             notEmpty.signalAll();
        Thread.sleep((int)(10*Math.random()));
    }catch(Exception ex){
    
    } finally{
        //System.out.println("return from meal entry");
        lock.unlock();
    }

}
public static int removeOrder(){
    
    lock.lock();
    Integer q= new Integer(5);
    try{
    while(meals.size()==0){
        notEmpty.await();
    }
        q=meals.get(meals.size()-1);
        meals.remove(meals.size()-1);
        if(meals.size()<Capacity)
            notFull.signalAll();
       Thread.sleep((int)(10*Math.random()));
    }catch(Exception ex){
    } finally{
        //System.out.println("finally of remove Order");
        lock.unlock();
        
    }
    return q.intValue();
}

}

}