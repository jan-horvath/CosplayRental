package cz.muni.fi.pv168.rentalapp.database;

import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack;

import javax.sql.DataSource;

public class ProductStackManager {
    // create, (getById), update (concerning storeProductStacks), delete (orderedProductStack)
    private final DataSource dataSource;

    public ProductStackManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

//    public ProductStack getStoreProductStack(long id) {
//
//    }
}
