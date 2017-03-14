package insta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * @Author Yogesh.Manware
 *
 */

public class C4_ConcurrencyProgramming {
  private static final int MAX_NUMBER = 30;
  private static Scanner sc;

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String args[]) throws Exception {
    sc = new Scanner(System.in);
    System.out.println("How many threads you wish to start? ");

    int threads = sc.nextInt();

    Storage storage = new Storage();

    for (int i = 0; i < threads; i++) {
      Runnable worker = new RandonNumberGenerator(storage);
      new Thread(worker).start();;
    }
  }

  /**
   * @author Yogesh.Manware
   *
   */
  static class RandonNumberGenerator implements Runnable {
    private Storage storage = null;

    /**
     * @param storage
     */
    RandonNumberGenerator(Storage storage) {
      this.storage = storage;
    }

    @Override
    public void run() {
      Random rn = new Random();
      while (true) {
        try {
          this.storage.add(rn.nextInt(10) + 1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * @author Yogesh.Manware
   *
   */
  static class Storage {
    Object lock = new Object();
    private List<Integer> list = new ArrayList<Integer>(MAX_NUMBER);

    void add(int n) throws InterruptedException {
      synchronized (lock) {
        if (list.size() < MAX_NUMBER) {
          list.add(n);
        } else {
          NumberProcessor.processNumbers(list);
          list = new ArrayList<>(MAX_NUMBER);
        }
      }
    }
  }

  /**
   * @author Yogesh.Manware
   *
   */
  static class NumberProcessor {
    private static ExecutorService executor = Executors.newFixedThreadPool(1);

    public static void processNumbers(List<Integer> list) {

      executor.submit(new Runnable() {

        @Override
        public void run() {
          System.out.println("Last 30 Numbers: " + list.toString());
          // process these numbers and reset the list
          Collections.sort(list);

          int total = list.get(0);
          int mostFrequentValue = list.get(0);

          int prev_mostFreqVal = list.get(0);
          int mostFreqVal = list.get(0);
          int count = 1;
          int maxCount = 1;

          for (int i = 1; i < MAX_NUMBER; i++) {
            total += list.get(i);

            if (list.get(i) == prev_mostFreqVal)
              count++;
            else {
              if (count > maxCount) {
                mostFreqVal = prev_mostFreqVal;
                maxCount = count;
              }
              prev_mostFreqVal = list.get(i);
              count = 1;
            }
          }

          mostFrequentValue = count > maxCount ? list.get(MAX_NUMBER - 1) : mostFreqVal;
          System.out.format("Average: %d, Min: %d, Max: %d, Most Frequent Value: %d",
              (total / MAX_NUMBER), list.get(0), list.get(MAX_NUMBER - 1), mostFrequentValue);
          System.out.println("\n------------------------------------------------------");
        }
      });
    }
  }
}
