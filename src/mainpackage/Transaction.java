package mainpackage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import users.Member;

public class Transaction {

    private String memberID;
    private String memberName;
    private int bookID;
    private String bookTitle;
    private String action;
    private LocalDateTime time;
    private LocalDate dueDate;
    private double fine;

    public Transaction(Member member, Book book, String action, LocalDate dueDate, double fine) {

        this.memberID = member.getUserID();
        this.memberName = member.getName();
        this.bookID = book.getItemID();
        this.bookTitle = book.getTitle();
        this.action = action;
        this.time = LocalDateTime.now();
        this.dueDate = dueDate;
        this.fine = fine;

    }

    public Transaction(LocalDateTime time, String memberID, String memberName, int bookID, String bookTitle, String action, LocalDate dueDate, double fine) {

        this.time = time;
        this.memberID = memberID;
        this.memberName = memberName;
        this.bookID = bookID;
        this.bookTitle = bookTitle;
        this.action = action;
        this.dueDate = dueDate;
        this.fine = fine;

    }

    public boolean isBorrow() {

        return action.equals("Borrowed");

    }

    public String getMemberID() {

        return memberID;

    }

    public String getMemberName() {

        return memberName;

    }

    public int getBookID() {

        return bookID;

    }

    public String getBookTitle() {

        return bookTitle;

    }

    public String getAction() {

        return action;

    }

    public LocalDateTime getTime() {

        return time;

    }

    public LocalDate getDueDate() {

        return dueDate;

    }

    public double getFine() {

        return fine;

    }

    public String getTimeText() {

        return time.format(DateTimeFormatter.ofPattern("MMM dd, yyyy  hh:mm a"));

    }

    public String getDueText() {

        return dueDate == null ? "-" : Library.dateText(dueDate);

    }

    public String getFineText() {

        return fine > 0 ? Library.moneyText(fine) : "-";

    }

}
