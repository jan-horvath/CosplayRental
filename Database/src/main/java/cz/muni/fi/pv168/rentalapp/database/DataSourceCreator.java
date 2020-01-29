package cz.muni.fi.pv168.rentalapp.database;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

public class DataSourceCreator {

    public static DataSource getDataSource() throws IOException {
        HikariDataSource ds = new HikariDataSource();

        Properties p = new Properties();
        p.load(DataSourceCreator.class.getResourceAsStream("/jdbc.properties"));

        ds.setDriverClassName(p.getProperty("jdbc.driver"));
        ds.setJdbcUrl(p.getProperty("jdbc.url"));
        ds.setUsername(p.getProperty("jdbc.user"));
        ds.setPassword(p.getProperty("jdbc.password"));

        new ResourceDatabasePopulator(
                new ClassPathResource("/schema-javadb.sql", DataSourceCreator.class),
                new ClassPathResource("/test-data.sql", DataSourceCreator.class))
                .execute(ds);
        return ds;
    }

    /**
     * Runs database layer. Mainly for tests.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        getDataSource();
    }
}

