package cz.muni.fi.pv168.rentalapp.database;

import com.zaxxer.hikari.HikariDataSource;
import cz.muni.fi.pv168.rentalapp.database.entities.Order;
import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DataSourceCreator {

    public static DataSource getDataSource() throws IOException {
        HikariDataSource ds = new HikariDataSource();

        //load connection properties from a file
        Properties p = new Properties();
        p.load(DataSourceCreator.class.getResourceAsStream("/jdbc.properties"));

        //set connection
        ds.setDriverClassName(p.getProperty("jdbc.driver"));
        ds.setJdbcUrl(p.getProperty("jdbc.url"));
        ds.setUsername(p.getProperty("jdbc.user"));
        ds.setPassword(p.getProperty("jdbc.password"));

        //populate db with tables and data
        new ResourceDatabasePopulator(
                new ClassPathResource("/schema-javadb.sql", DataSourceCreator.class),
                new ClassPathResource("/test-data.sql", DataSourceCreator.class))
                .execute(ds);
        return ds;
    }

    /**
     * Runs database layer. For tests, mainly.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, DatabaseException, SQLException {
        DataSource dataSource = getDataSource();
        OrderManager orderManager = new OrderManager(dataSource);
        ProductStackManager productStackManager = new ProductStackManager(dataSource);

        // SQL tables: IDs begin with 1
        // if orderId does not exist, getOrderById() returns null

        List<ProductStack> prodStack = new ArrayList<>(Arrays.asList(
                new ProductStack(1 , 1, "Asterix helmet", ProductStack.Size.NA, 15.80, 1),
                new ProductStack(1, 2, "Poseidon trident", ProductStack.Size.NA, 21.90, 1)
        ));
        LocalDate date = LocalDate.of(2020, 11, 6);

        Order order = orderManager.insertOrder(prodStack, "email", "Radka", "777777", date);
        long orderId = order.getId();
//        System.out.println(orderManager.getAllOrders().size());
//        System.out.println(productStackManager.getAllOrderedProductStacks().size());
//        orderManager.deleteOrder(orderId);
//        System.out.println(orderManager.getAllOrders().size());
//        System.out.println(productStackManager.getAllOrderedProductStacks().size());

        //System.out.println(productStackManager.getAllStoreProductStacks());

        ProductStack productStackById = productStackManager.getProductStackById(1);
        System.out.println(productStackById);
        productStackById.setStackSize(5);
        productStackManager.updateStoreProductStack(productStackById);
        System.out.println(productStackManager.getProductStackById(1));
    }
}

