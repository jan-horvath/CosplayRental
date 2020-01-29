package cz.muni.fi.pv168.rentalapp.business;

import cz.muni.fi.pv168.rentalapp.database.DatabaseException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimeSimulator {
    private List<Runnable> callbacks;
    private LocalDate date;

    public TimeSimulator() {
        date = LocalDate.now();
        callbacks = new ArrayList<>();
    }

    public void addCallback(Runnable r) {
        callbacks.add(r);
    }

    public LocalDate getTime() {
        return date;
    }

    private void executeCallbacks() {
        for (Runnable callback : callbacks) {
            callback.run();
        }
    }

    public void advanceOneDay() {
        date = date.plusDays(1);
        executeCallbacks();

    }

    public void advanceOneWeek() {
        for (int i = 0; i< 7; ++i) {
            advanceOneDay();
        }
    }

    public void advanceFourWeeks() {
        for (int i = 0; i < 4; i++) {
            advanceOneWeek();
        }
    }

}
