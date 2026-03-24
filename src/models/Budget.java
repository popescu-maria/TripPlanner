package models;

public class Budget {
    private static int counter = 0;
    private int id;
    private double totalBudget;
    private double spentAmount;

    public Budget(double totalBudget) {
        this.id = ++counter;
        this.totalBudget = totalBudget;
        this.spentAmount = 0;
    }

    public void addExpense(double amount) { this.spentAmount += amount; }
    public double getRemainingBudget() { return totalBudget - spentAmount; }
    public boolean isOverBudget() { return spentAmount > totalBudget; }

    @Override
    public String toString() {
        return "Budget: " + totalBudget + " lei | Spent: " + spentAmount + " lei | Remaining: " + getRemainingBudget() + " lei";
    }
}
