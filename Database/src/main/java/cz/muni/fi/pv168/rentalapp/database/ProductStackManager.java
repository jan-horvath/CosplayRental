package cz.muni.fi.pv168.rentalapp.database;

import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductStackManager {
    private final DataSource dataSource;
    private JdbcTemplate jdbc;

    public ProductStackManager(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbc = new JdbcTemplate(dataSource);
    }

    public List<ProductStack> getAllStoreProductStacks() {
        return jdbc.query("SELECT * FROM storeproductstacks", storeProductStackMapper);
    }

    public ProductStack getStoreProductStackById(long id) {
        return jdbc.queryForObject("SELECT * FROM storeproductstacks WHERE id = ?", storeProductStackMapper, id);
    }

    public void updateStoreProductStack(ProductStack ps) {
        jdbc.update("UPDATE storeproductstacks set name=?, size=?, price=?, stacksize=? where id=?",
                ps.getName(), ps.getSize().toString(), ps.getPrice(), ps.getStackSize(), ps.getId());
    }

    // used in OrderManager in getOrderedPSByOrderId() method
    public RowMapper<ProductStack> orderedProductStackMapper = new RowMapper<ProductStack>() {
        @Override
        public ProductStack mapRow(ResultSet rs, int i) throws SQLException {
            long orderedPSid = rs.getLong("id");
            long storeId = rs.getLong("storeid");
            int stackSize = rs.getInt("stacksize");
            ProductStack storePS = getStoreProductStackById(storeId);
            return new ProductStack(orderedPSid, storeId, storePS.getName(), storePS.getSize(), storePS.getPrice(), stackSize);
        }
    };

    private RowMapper<ProductStack> storeProductStackMapper = new RowMapper<ProductStack>() {
        @Override
        public ProductStack mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            ProductStack.Size size = ProductStack.Size.valueOf(rs.getString("size"));
            double price = rs.getDouble("price");
            int stacksize = rs.getInt("stacksize");
            return new ProductStack(id, id, name, size, price, stacksize);
        }
    };
}
