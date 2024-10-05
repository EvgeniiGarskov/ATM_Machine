package com.example.atm_machine.Models;

public class Transaction {

    private String name;
    private String operation;
    private String date;
    private int amount;

    public static final String CONST_DEPOSIT = "Внесение наличных";
    public static final String CONST_WITHDRAWAL = "Снятие наличных";
    public static final String CONST_TRANSFER = "Перевод клиенту";
    public static final String CONST_INCOMING_TRANSFER = "Входящий перевод";

    public Transaction(String name, String operation, String date, int amount) {
        this.name = name;
        this.operation = operation;
        this.date = date;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
