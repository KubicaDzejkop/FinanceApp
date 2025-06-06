package com.example.financeapp.ui.models;

import com.example.financeapp.ui.models.Transaction;

public class TransactionListItem {
    public static final int TYPE_DATE_HEADER = 0;
    public static final int TYPE_TRANSACTION = 1;

    private int type;
    private String date;
    private Transaction transaction;


    public static TransactionListItem dateHeader(String date) {
        TransactionListItem item = new TransactionListItem();
        item.type = TYPE_DATE_HEADER;
        item.date = date;
        return item;
    }


    public static TransactionListItem transactionItem(Transaction transaction) {
        TransactionListItem item = new TransactionListItem();
        item.type = TYPE_TRANSACTION;
        item.transaction = transaction;
        return item;
    }

    public int getType() { return type; }
    public String getDate() { return date; }
    public Transaction getTransaction() { return transaction; }
}
