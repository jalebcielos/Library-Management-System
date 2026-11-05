package mainpackage;

import java.io.*;
import java.nio.charset.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import users.*;

public class FileManager {

    private static final String DIR = "data";
    private static final String BOOKS = DIR + File.separator + "books.txt";
    private static final String USERS = DIR + File.separator + "users.txt";
    private static final String TRANSACTIONS = DIR + File.separator + "transactions.txt";

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SPLIT = "\\s*\\|\\s*";

    public static boolean booksFileExists() {

        return new File(BOOKS).exists();

    }

    public static boolean usersFileExists() {

        return new File(USERS).exists();

    }

    private static String clean(String text) {

        if (text == null) {

            return "";

        }

        return text.replace("|", "/").replace("\r", " ").replace("\n", " ");

    }

    private static String value(String field, String label) {

        if (field.startsWith(label + ":")) {

            String rest = field.substring(label.length() + 1);

            if (rest.startsWith(" ")) {

                rest = rest.substring(1);

            }

            return rest;

        }

        return field;

    }

    private static boolean skip(String line) {

        return line.trim().isEmpty() || line.trim().startsWith("#");

    }

    private static void writeHeader(BufferedWriter writer, String... lines) throws IOException {

        writer.write("==============================================================================");
        writer.newLine();

        for (String line : lines) {

            writer.write(("# " + line).trim());
            writer.newLine();

        }

        writer.write("==============================================================================");
        writer.newLine();
        writer.newLine();

    }

    private static BufferedReader openReader(String path) throws IOException {

        return new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));

    }

    private static BufferedWriter openWriter(String path, boolean append) throws IOException {

        File file = new File(path);
        File parent = file.getParentFile();

        if (parent != null) {

            parent.mkdirs();

        }

        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8));

    }

    //BOOKS
    public static ArrayList<Book> readBooks() {

        ArrayList<Book> books = new ArrayList<>();

        try (BufferedReader reader = openReader(BOOKS)) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (skip(line)) {

                    continue;

                }

                String[] f = line.trim().split(SPLIT, -1);

                if (f.length < 8) {

                    continue;

                }

                try {

                    Book b = new Book(Integer.parseInt(value(f[0], "Book ID")), value(f[1], "Title"), value(f[2], "Author"), value(f[3], "Category"),
                            Integer.parseInt(value(f[4], "Year")), Integer.parseInt(value(f[5], "Total Copies")));
                    b.restoreAvailableStock(Integer.parseInt(value(f[6], "Copies Available")));

                    String cover = value(f[7], "Cover");
                    b.setCover(cover.isEmpty() || cover.equals("(none)") ? null : cover);
                    books.add(b);

                } catch (NumberFormatException e) {

                    System.err.println("Skipped bad line in books.txt: " + line);

                }

            }

        } catch (IOException e) {

            System.err.println("Could not read books.txt: " + e.getMessage());

        }

        return books;

    }

    public static void writeBooks(ArrayList<Book> books) {

        try (BufferedWriter writer = openWriter(BOOKS, false)) {

            writeHeader(writer,
                    "GREENFIELD UNIVERSITY LIBRARY - BOOK RECORDS");

            for (Book b : books) {

                writer.write("Book ID: " + b.getItemID() + " | Title: " + clean(b.getTitle()) + " | Author: " + clean(b.getAuthor())
                        + " | Category: " + clean(b.getCategory()) + " | Year: " + b.getYear() + " | Total Copies: " + b.getStock()
                        + " | Copies Available: " + b.getAvailableStock()
                        + " | Cover: " + (b.getCover() == null || b.getCover().isEmpty() ? "(none)" : clean(b.getCover())));
                writer.newLine();

            }

        } catch (IOException e) {

            System.err.println("Could not write books.txt: " + e.getMessage());

        }

    }

    //USERS
    public static ArrayList<User> readUsers() {

        ArrayList<User> users = new ArrayList<>();

        try (BufferedReader reader = openReader(USERS)) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (skip(line)) {

                    continue;

                }

                String[] f = line.split(SPLIT, -1);

                if (f.length < 5) {

                    continue;

                }

                String role = value(f[1].trim(), "Role");

                if (role.equals("Librarian")) {

                    users.add(new Librarian(value(f[0].trim(), "User ID"), value(f[2], "Name"), value(f[3], "Username"), value(f[4], "Password")));

                } else if (role.equals("Member")) {

                    users.add(new Member(value(f[0].trim(), "User ID"), value(f[2], "Name"), value(f[3], "Username"), value(f[4], "Password")));

                }

            }

        } catch (IOException e) {

            System.err.println("Could not read users.txt: " + e.getMessage());

        }

        return users;

    }

    public static void writeUsers(ArrayList<User> users) {

        try (BufferedWriter writer = openWriter(USERS, false)) {

            writeHeader(writer,
                    "GREENFIELD UNIVERSITY LIBRARY - USER ACCOUNTS");

            for (User u : users) {

                writer.write("User ID: " + u.getUserID() + " | Role: " + u.getRole() + " | Name: " + clean(u.getName())
                        + " | Username: " + clean(u.getUsername()) + " | Password: " + clean(u.getPassword()));
                writer.newLine();

            }

        } catch (IOException e) {

            System.err.println("Could not write users.txt: " + e.getMessage());

        }

    }

    //TRANSACTIONS
    private static LocalDateTime parseTime(String text) {

        try {

            return LocalDateTime.parse(text, TIME_FORMAT);

        } catch (DateTimeParseException e) {

            return LocalDateTime.parse(text);

        }

    }

    private static void writeTransactionHeader(BufferedWriter writer) throws IOException {

        writeHeader(writer,
                "GREENFIELD UNIVERSITY LIBRARY - BORROW AND RETURN RECORDS");

    }

    private static String transactionLine(Transaction t) {

        return "Date & Time: " + t.getTime().format(TIME_FORMAT) + " | Member ID: " + clean(t.getMemberID()) + " | Member Name: " + clean(t.getMemberName())
                + " | Book ID: " + t.getBookID() + " | Book Title: " + clean(t.getBookTitle()) + " | Action: " + t.getAction()
                + " | Due Date: " + (t.getDueDate() == null ? "-" : t.getDueDate().toString())
                + " | Fine: " + String.format(Locale.US, "%.2f", t.getFine());

    }

    public static ArrayList<Transaction> readTransactions() {

        ArrayList<Transaction> list = new ArrayList<>();

        try (BufferedReader reader = openReader(TRANSACTIONS)) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (skip(line)) {

                    continue;

                }

                String[] f = line.trim().split(SPLIT, -1);

                if (f.length < 6) {

                    continue;

                }

                try {

                    LocalDateTime time = parseTime(value(f[0], "Date & Time"));
                    String action = value(f[5], "Action");
                    LocalDate dueDate = null;
                    double fine = 0;

                    if (f.length >= 8) {

                        String dueText = value(f[6], "Due Date");

                        if (!dueText.isEmpty() && !dueText.equals("-")) {

                            dueDate = LocalDate.parse(dueText);

                        }

                        fine = Double.parseDouble(value(f[7], "Fine"));

                    } else if (action.equals("Borrowed")) {

                        dueDate = time.toLocalDate().plusDays(Library.LOAN_DAYS);

                    }

                    list.add(new Transaction(time, value(f[1], "Member ID"), value(f[2], "Member Name"), Integer.parseInt(value(f[3], "Book ID")), value(f[4], "Book Title"), action, dueDate, fine));

                } catch (Exception e) {

                    System.err.println("Skipped bad line in transactions.txt: " + line);

                }

            }

        } catch (FileNotFoundException e) {


        } catch (IOException e) {

            System.err.println("Could not read transactions.txt: " + e.getMessage());

        }

        return list;

    }

    public static void writeTransactions(ArrayList<Transaction> list) {

        try (BufferedWriter writer = openWriter(TRANSACTIONS, false)) {

            writeTransactionHeader(writer);

            for (Transaction t : list) {

                writer.write(transactionLine(t));
                writer.newLine();

            }

        } catch (IOException e) {

            System.err.println("Could not write transactions.txt: " + e.getMessage());

        }

    }

    public static void appendTransaction(Transaction t) {

        File file = new File(TRANSACTIONS);
        boolean isNew = !file.exists() || file.length() == 0;

        try (BufferedWriter writer = openWriter(TRANSACTIONS, true)) {

            if (isNew) {

                writeTransactionHeader(writer);

            }

            writer.write(transactionLine(t));
            writer.newLine();

        } catch (IOException e) {

            System.err.println("Could not write transactions.txt: " + e.getMessage());

        }

    }

}
