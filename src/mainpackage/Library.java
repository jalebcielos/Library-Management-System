package mainpackage;

import users.Librarian;
import users.Member;
import users.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Library {

    public static final int MIN_USERNAME = 5;
    public static final int MIN_PASSWORD = 8;
    public static final int LOAN_DAYS = 7;
    public static final double FINE_PER_DAY = 5.00;

    private ArrayList<Book> allBooks = new ArrayList<>();
    private ArrayList<Member> allMembers = new ArrayList<>();
    private ArrayList<User> accounts = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public void load() {

        if (FileManager.usersFileExists()) {

            for (User u : FileManager.readUsers()) {

                accounts.add(u);

                if (u instanceof Member) {

                    allMembers.add((Member) u);

                }

            }

        } else {

            accounts.add(new Librarian("EMP-GFU-2026-001", "Adrian Navarro", "EMP2026001", "adrian123"));
            accounts.add(new Librarian("EMP-GFU-2026-002", "Samantha Dela Cruz", "EMP2026002", "samantha123"));
            accounts.add(new Librarian("EMP-GFU-2026-003", "Gabriel Morales", "EMP2026003", "gabriel123"));
            Member member1 = new Member("2026100001", "John Santos", "2026100001", "john123");
            Member member2 = new Member("2026100002", "Maria Reyes", "2026100002", "maria123");
            Member member3 = new Member("2026100003", "Mark Cruz", "2026100003", "maria123");
            Member member4 = new Member("2026100004", "Anna Garcia", "2026100004", "maria123");
            Member member5 = new Member("2026100005", "James Lopez", "2026100005", "maria23");
            Member[] starterMember = {member1, member2, member3, member4, member5};
            for (int i = 0; i < starterMember.length; i++) {

                accounts.add(starterMember[i]);
                allMembers.add(starterMember[i]);
            }

            saveUsers();

        }

        if (FileManager.booksFileExists()) {

            allBooks.addAll(FileManager.readBooks());

        } else {

            Book book1 = new Book(101, "The Metamorphosis", "Franz Kafka", "Novella", 1915, 15);
            book1.setCover("src/bookcover/The Metamorphosis.jpg");
            allBooks.add(book1);

            Book book2 = new Book(102, "Crime and Punishment", "Fyodor Dostoevsky", "Psychological Novel", 1866, 20);
            book2.setCover("src/bookcover/Crime and Punishment.jpg");
            allBooks.add(book2);

            Book book3 = new Book(103, "Demian", "Hermann Hesse", "Psychological Novel", 1919, 18);
            book3.setCover("src/bookcover/Demian.jpg");
            allBooks.add(book3);

            Book book4 = new Book(104, "The Wings", "Kim Hae-gyeong", "Short Story", 1936, 12);
            book4.setCover("src/bookcover/The Wings.jpg");
            allBooks.add(book4);

            Book book5 = new Book(105, "Moby-Dick", "Herman Melville", "Adventure Novel", 1851, 20);
            book5.setCover("src/bookcover/Moby-Dick.jpg");
            allBooks.add(book5);

            Book book6 = new Book(106, "Wuthering Heights", "Emily Bronte", "Gothic Novel", 1847, 17);
            book6.setCover("src/bookcover/Wuthering Heights.jpg");
            allBooks.add(book6);

            Book book7 = new Book(107, "Don Quixote", "Miguel de Cervantes", "Satirical Novel", 1605, 20);
            book7.setCover("src/bookcover/Don Quixote.jpg");
            allBooks.add(book7);

            Book book8 = new Book(108, "Dream of the Red Chamber", "Cao Xueqin", "Psychological Novel", 1791, 14);
            book8.setCover("src/bookcover/Dream of the Red Chamber.jpg");
            allBooks.add(book8);

            Book book9 = new Book(109, "Hell Screen", "Ryunosuke Akutagawa", "Short Story", 1918, 10);
            book9.setCover("src/bookcover/Hell Screen.jpg");
            allBooks.add(book9);

            Book book10 = new Book(110, "The Stranger", "Albert Camus", "Philosophical Novel", 1942, 16);
            book10.setCover("src/bookcover/The Stranger.jpg");
            allBooks.add(book10);

            Book book11 = new Book(111, "The Odyssey", "Homer", "Epic Poetry", 1935, 11);
            book11.setCover("src/bookcover/The Odyssey.jpg");
            allBooks.add(book11);

            saveBooks();

        }

        transactions.addAll(FileManager.readTransactions());

        for (Transaction t : transactions) {

            Member m = findMember(t.getMemberID());
            Book b = findBook(t.getBookID());

            if (m == null || b == null) {

                continue;

            }

            if (t.isBorrow()) {

                m.restoreBorrowed(b, t.getDueDate());

            } else {

                m.releaseBorrowed(b);

            }

        }

        saveUsers();
        saveBooks();
        FileManager.writeTransactions(transactions);

    }

    public void saveBooks() {

        FileManager.writeBooks(allBooks);

    }

    public void saveUsers() {

        FileManager.writeUsers(accounts);

    }

    public static String usernameProblem(String username) {

        if (username.length() < MIN_USERNAME) {

            return "Username must be at least " + MIN_USERNAME + " characters!";

        }

        if (username.contains(" ") || username.contains("|")) {

            return "Username can't contain spaces or the | character!";

        }

        return null;

    }

    public static String passwordProblem(String password) {

        if (password.length() < MIN_PASSWORD) {

            return "Password must be at least " + MIN_PASSWORD + " characters!";

        }

        if (password.contains("|")) {

            return "Password can't contain the | character!";

        }

        return null;

    }

    public static long daysLate(LocalDate dueDate) {

        if (dueDate == null) {

            return 0;

        }

        return Math.max(0, ChronoUnit.DAYS.between(dueDate, LocalDate.now()));

    }

    public static double fineFor(LocalDate dueDate) {

        return daysLate(dueDate) * FINE_PER_DAY;

    }

    public static String dateText(LocalDate date) {

        return date == null ? "-" : date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));

    }

    public static String moneyText(double amount) {

        return "PHP " + String.format("%.2f", amount);

    }

    public void addBook(Book book) {

        allBooks.add(book);
        saveBooks();

    }

    public void removeBook(Book book) {

        allBooks.remove(book);
        saveBooks();

    }

    public Book findBook(int id) {

        for (Book b : allBooks) {

            if (b.getItemID() == id) {

                return b;

            }
        }

        return null;
    }
    
    public boolean bookExists(String title, String author, String category, int year, int ignoreID) {

    for (Book b : allBooks) {

        if (b.getItemID() == ignoreID) {

            continue;

        }

        if (b.getTitle().trim().equalsIgnoreCase(title.trim())
                && b.getAuthor().trim().equalsIgnoreCase(author.trim())
                && b.getCategory().trim().equalsIgnoreCase(category.trim())
                && b.getYear() == year) {

            return true;

        }

    }

    return false;

}

    public int nextBookID() {

        int max = 99;

        for (Book b : allBooks) {

            max = Math.max(max, b.getItemID());

        }

        return max + 1;

    }

    public ArrayList<Book> getBooks() {

        return allBooks;

    }

    public void addMember(Member member) {

        allMembers.add(member);
        accounts.add(member);
        saveUsers();

    }

    public void removeMember(Member member) {

        allMembers.remove(member);
        accounts.remove(member);
        saveUsers();

    }

    public Member findMember(String id) {

        for (Member m : allMembers) {

            if (m.getUserID().equals(id)) {

                return m;

            }
        }

        return null;

    }

    public ArrayList<Member> getMembers() {

        return allMembers;

    }

    public User login(String username, String password) {

        for (User u : accounts) {

            if (u.checkLogin(username, password)) {

                return u;

            }
        }

        return null;

    }

    public boolean usernameExists(String username) {

        for (User u : accounts) {

            if (u.getUsername().equals(username)) {

                return true;

            }
        }

        return false;

    }

    public boolean borrowBook(Member member, Book book) {

        if (member.borrowBook(book)) {

            record(new Transaction(member, book, "Borrowed", member.getDueDate(book), 0));
            return true;

        }

        return false;

    }

    public boolean returnBook(Member member, Book book) {

        LocalDate dueDate = member.getDueDate(book);
        double fine = fineFor(dueDate);

        if (member.returnBook(book)) {

            record(new Transaction(member, book, "Returned", dueDate, fine));
            return true;

        }

        return false;

    }

    private void record(Transaction t) {

        transactions.add(t);
        FileManager.appendTransaction(t);
        saveBooks();

    }

    public ArrayList<Transaction> getTransactions() {

        return transactions;

    }

    public ArrayList<Transaction> getTransactionsOf(String memberID) {

        ArrayList<Transaction> result = new ArrayList<>();

        for (Transaction t : transactions) {

            if (t.getMemberID().equals(memberID)) {

                result.add(t);

            }
        }

        return result;

    }
}
