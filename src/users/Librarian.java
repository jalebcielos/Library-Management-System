package users;

import mainpackage.*;

public class Librarian extends User {

    public Librarian(String userID, String name, String username, String password) {

        super(userID, name, username, password);

    }

    public void addBook(Library library, Book book) {

        library.addBook(book);

    }

    public boolean removeBook(Library library, int bookID) {

        Book book = library.findBook(bookID);

        if (book != null && book.getBorrowedCount() == 0) {

            library.removeBook(book);
            return true;

        }

        return false;

    }

    public void addMember(Library library, Member member) {

        library.addMember(member);

    }

    @Override
    public String getRole() {

        return "Librarian";

    }

}
