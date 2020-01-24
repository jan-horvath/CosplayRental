package cz.muni.fi.pv168.rentalapp.database;

public class DatabaseOrderException extends Exception {
    public DatabaseOrderException(String message, Throwable t) {
        super(message, t);
    }
}
