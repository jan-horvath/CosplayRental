package cz.muni.fi.pv168.rentalapp.database;

import com.zaxxer.hikari.HikariDataSource;
import cz.muni.fi.pv168.rentalapp.database.entities.Order;
import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.IOException;
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
    public static void main(String[] args) throws IOException, DatabaseOrderException {
        DataSource dataSource = getDataSource();
        OrderManager orderManager = new OrderManager(dataSource);
        // SQL tables: IDs begin with 1
        // if id does not exist, getOrderById() returns null
        List<Order> orders = orderManager.getAllOrders();
        for (Order o : orders) {
            System.out.println(o.getFullName());
            for (ProductStack ps : o.getProductStacks()) {
                System.out.println(ps);
            }
        }
//        System.out.println(desiredOrder.getFullName());
    }
//        List<Order> orders = orderManager.getAllOrders();
}

