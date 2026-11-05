package users;

import java.time.LocalDate;
import java.util.*;
import mainpackage.*;

public class Member extends User {

    private ArrayList<Book> borrowedBooks;
    private HashMap<Book, LocalDate> dueDates;

    public Member(String userID, String name, String username, String password) {

        super(userID, name, username, password);
        borrowedBooks = new ArrayList<>();
        dueDates = new HashMap<>();

    }

    @Override
    public void displayDetails() {

        super.displayDetails();
        System.out.println("Borrowed Books: " + borrowedBooks.size());

    }

    public boolean borrowBook(Book book) {

        if (borrowedBooks.contains(book)) {

            return false;

        }

        if (book.borrowBook()) {

            borrowedBooks.add(book);
            dueDates.put(book, LocalDate.now().plusDays(Library.LOAN_DAYS));
            return true;

        }

        return false;
    }

    public boolean returnBook(Book book) {

        if (borrowedBooks.contains(book) && book.returnBook()) {

            borrowedBooks.remove(book);
            dueDates.remove(book);
            return true;

        }

        return false;

    }

    public void restoreBorrowed(Book book, LocalDate dueDate) {

        if (!borrowedBooks.contains(book)) {

            borrowedBooks.add(book);

        }

        dueDates.put(book, dueDate);

    }

    public void releaseBorrowed(Book book) {

        borrowedBooks.remove(book);
        dueDates.remove(book);

    }

    public LocalDate getDueDate(Book book) {

        return dueDates.get(book);

    }

    @Override
    public String getRole() {

        return "Member";

    }

    public ArrayList<Book> getBooks() {

        return borrowedBooks;

    }

}
