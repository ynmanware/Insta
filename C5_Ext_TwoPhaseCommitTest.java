package raft.two_phase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yogesh.Manware
 *
 */
public class TwoPhaseCommitTest {

  final static Map<String, Double> AxisBankAccounts = new HashMap<>();
  final static Map<String, Double> HDFCBankAccounts = new HashMap<>();
  {
    AxisBankAccounts.put("23444", 1000.00);
    HDFCBankAccounts.put("12345", 1000.00);
  }

  public static void main(String[] args) throws InterruptedException {
    TwoPhaseCommitTest coordinator = new TwoPhaseCommitTest();
    Account a1 = new Account("23444", AxisBankAccounts);
    Account a2 = new Account("12345", HDFCBankAccounts);

    for (int i = 0; i < 11; i++) {
      coordinator.makeTransfer(a1, a2, 100);
    }
  }

  // two phase commit here
  private void makeTransfer(Account a1, Account a2, double amount) {
    // prepare
    boolean status1 = a1.commitPrepare(amount * -1);
    boolean status2 = a2.commitPrepare(amount);

    // check the status
    if (!status1 || !status2) {
      a1.rollback();
      a2.rollback();
    } else {
      a1.commit();
      a2.commit();
    }

    // print balance
    a1.printBalance();
    a2.printBalance();
  }
}


class Account {
  private double balance;
  private String acc;
  private Map<String, Double> bankStorage;

  public Account(String acc, Map<String, Double> storage) {
    this.acc = acc;
    this.bankStorage = storage;
  }

  public void printBalance() {
    System.out.format("Account : %s, Balance: %f", this.acc, this.bankStorage.get(this.acc));
    System.out.println();
  }

  public boolean commitPrepare(final double d) {
    try {
      this.balance = this.bankStorage.get(this.acc) + d;
      if (this.balance < 0) {
        throw new Exception("Insufficient Balance!");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public void commit() {
    System.out.println("Commiting the changes.........");
    this.bankStorage.put(this.acc, this.balance);
  }

  public void rollback() {
    System.out.println("Rolling back the changes.........");
    this.balance = this.bankStorage.get(this.acc);
  }
}
