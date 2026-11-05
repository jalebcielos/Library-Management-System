package mainpackage;

public class Book extends LibraryItem {

    private String author;
    private String category;
    private int year;
    private String cover;
    private int totalStock;
    private int availableStock;

    public Book(int itemID, String title, String author, String category, int year, int totalStock) {

        super(itemID, title, totalStock > 0);
        this.author = author;
        this.category = category;
        this.year = year;
        this.totalStock = totalStock;
        this.availableStock = totalStock;

    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void restoreAvailableStock(int available) {

        availableStock = Math.max(0, Math.min(available, totalStock));
        setAvailability(availableStock > 0);

    }

    public boolean setStock(int newTotal) {

        int copiesOut = totalStock - availableStock;

        if (newTotal < copiesOut) {

            return false;

        }

        totalStock = newTotal;
        availableStock = newTotal - copiesOut;
        setAvailability(availableStock > 0);
        return true;

    }

    @Override
    public void displayDetails() {

        System.out.println("Book ID: " + getItemID());
        System.out.println("Title: " + getTitle());
        System.out.println("Author: " + author);
        System.out.println("Category: " + category);
        System.out.println("Year: " + year);
        System.out.println("Copies left: " + availableStock + "/" + totalStock);

    }

    public boolean borrowBook() {

        if (availableStock > 0) {

            availableStock--;
            setAvailability(availableStock > 0);
            return true;

        }

        return false;

    }

    public boolean returnBook() {

        if (availableStock < totalStock) {

            availableStock++;
            setAvailability(true);
            return true;

        }

        return false;

    }

    public String getAuthor() {

        return author;

    }

    public String getCategory() {

        return category;

    }

    public int getYear() {

        return year;

    }

    public int getStock() {

        return totalStock;

    }

    public int getAvailableStock() {

        return availableStock;

    }

    public int getBorrowedCount() {

        return totalStock - availableStock;

    }

    public String getCover() {

        return cover;

    }

    public void setCover(String cover) {

        this.cover = cover;

    }
}
