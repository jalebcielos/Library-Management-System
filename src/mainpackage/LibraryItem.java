
package mainpackage;

public abstract class LibraryItem {
    
    private int itemID;
    private String title;
    private boolean isAvailable;

    public LibraryItem(int itemID, String title, boolean isAvailable) {
        
        this.itemID = itemID;
        this.title = title;
        this.isAvailable = isAvailable;
        
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public abstract void displayDetails();   

    public int getItemID() {
        
        return itemID;
        
    }

    public String getTitle() {
        
        return title;
        
    }

    public boolean isAvailable() {
        
        return isAvailable;
        
    }
    
    public void setAvailability(boolean isAvailable) {
        
        this.isAvailable = isAvailable;
    
    }
        
}
