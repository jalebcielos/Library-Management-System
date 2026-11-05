package mainpackage;

import loginpage.LoginPage;

public class Main {

    public static void main(String[] args) {

        Library library = new Library();
        library.load();

        new LoginPage(library);

    }

}
