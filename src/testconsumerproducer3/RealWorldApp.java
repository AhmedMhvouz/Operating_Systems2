package testconsumerproducer3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author hussein
 */
public class RealWorldApp {

  protected static int maxSize = 20;
  static class Computer_Customer extends TestConsumerProducer3.Consumer {
    public Computer_Customer(List < Integer > goods) {
      super(goods);
    }

    @Override
    public void consume() {
      synchronized(goods) {
        Random rand = new Random();
        while (goods.size() <= 0) {
          try {
            goods.wait();
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        goods.remove(0);
        goods.notifyAll();
      }
    }
    @Override
    public void run() {
      while (true) {
        consume();
        try {
          Random rand = new Random();
          Thread.currentThread().sleep(rand.nextInt(2) * 1000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  static class Human_Customer extends TestConsumerProducer3.Consumer {
    public Human_Customer(List < Integer > goods) {
      super(goods);
    }
    @Override
    public void consume() {
      synchronized(goods) {

        while (goods.size() <= 0) {
          try {
            goods.wait();
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        goods.notifyAll();
      }
    }
    @Override
    public void run() {

      long startTime = System.currentTimeMillis();
      consume();
      long finishTime = System.currentTimeMillis();
      long orderTime = (finishTime - startTime) / 1000;
      System.out.println("Your cake is ready! order completed in " +
        orderTime + " seconds.");

    }
  }
  static class Baker extends TestConsumerProducer3.Producer {
    public Baker(List < Integer > goods) {
      super(goods);
    }
    public void produce() {
      synchronized(goods) {
        while (goods.size() >= maxSize) {
          try {
            goods.wait();
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        for (int i = 0; i < 5; i++) goods.add(1);
        goods.notifyAll();
      }
    }

    @Override
    public void run() {
      while (true) {
        produce();
        try {
          Thread.currentThread().sleep(10000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    List < Integer > goods = new ArrayList < > ();
    Computer_Customer computer_customer = new Computer_Customer(goods);

    Baker baker = new Baker(goods);
    Thread computerWorker = new Thread(computer_customer);
    Thread bakerWorker = new Thread(baker);
    System.out.println("Welcome to X Bakery!");
    bakerWorker.start();
    computerWorker.start();
    Scanner scn = new Scanner(System.in);

    while (true) {
      System.out.println("\nTo order a cake enter 1." +
        "\nTo exit enter 0.");
      int input = scn.nextInt();
      if (input == 1) {

        System.out.println("Working on your order...");
        Human_Customer human_customer = new Human_Customer(goods);
        Thread humanWorker = new Thread(human_customer);

        humanWorker.start();
        humanWorker.join();
      } else if (input == 0) {
        System.out.println("\nBye.");
        System.exit(0);
      } else {
        System.out.println("\nInvalid Input, please try again");
      }

    }
  }
}