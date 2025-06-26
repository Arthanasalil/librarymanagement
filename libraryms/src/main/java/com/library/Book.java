package com.library;

public class Book {
    public String id;
    public String title;
    public String author;
    public boolean isBorrowed;
    public String borrowedBy;

    public Book(String id, String title, String author, boolean isBorrowed, String borrowedBy) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isBorrowed = isBorrowed;
        this.borrowedBy = borrowedBy;
    }

    @Override
    public String toString() {
        return id + " - " + title + " by " + author + (isBorrowed ? " [Borrowed by: " + borrowedBy + "]" : " [Available]");
    }
}
