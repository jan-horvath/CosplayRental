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
    // create, (getById), update (concerning storeProductStacks), delete (orderedProductStack)
    private final DataSource dataSource;
    private JdbcTemplate jdbc;

    public ProductStackManager(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbc = new JdbcTemplate(dataSource);
    }
        // StoreProductStacks have the same id and storeId
        public ProductStack getProductStackById(long id) throws DatabaseException {
            try (Connection con = dataSource.getConnection()) {
                try (PreparedStatement st = con.prepareStatement("select * from storeproductstacks where id = ?")) {
                    st.setLong(1, id);
                    ResultSet rs = st.executeQuery();
                    if (rs.next()) {
                        String name = rs.getString("name");
                        ProductStack.Size size = ProductStack.Size.valueOf(rs.getString("size"));
                        double price = rs.getDouble("price");
                        int stackSize = rs.getInt("stacksize");
                        return new ProductStack(id, id, name, size, price, stackSize);
                    } else {
                        return null;
                    }
                }
            } catch (SQLException e) {
                System.err.println("Cannot select productStack with id " + id + ": " + e);
                throw new DatabaseException("Database failed on product stack selection", e);
            }
        }

        // just for check of ordered product stacks, delete it later
        public List<ProductStack> getAllOrderedProductStacks() throws DatabaseException, SQLException {
            try (Connection con = dataSource.getConnection()) {
                try (PreparedStatement st = con.prepareStatement("select * from orderedproductstacks")) {
                    ResultSet rs = st.executeQuery();
                    List<ProductStack> orderedProductStacks = new ArrayList<>();
                    while (rs.next()) {
                        long orderedPSid = rs.getLong("id");
                        long storeId = rs.getLong("storeid");
                        int stackSize = rs.getInt("stackSize");
                        ProductStack storePS = getProductStackById(storeId);
                        orderedProductStacks.add(new ProductStack(orderedPSid, storeId, storePS.getName(), storePS.getSize(), storePS.getPrice(), stackSize));
                    }
                    if (orderedProductStacks.isEmpty()) {
                        System.out.println("There were found no ordered productStacks in orderedProductStacks database table.");
                    }
                    return orderedProductStacks;
                } catch (SQLException | DatabaseException e) {
                    System.err.println("Cannot select otderedProductStacks");
                    throw new DatabaseException("Database failed on product stack selection", e);
                }
            }
        }

        /*public void insertStoreProductStack(String name, ProductStack.Size size, double price, int stackSize) {

        }*/

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
}
