package cz.muni.fi.pv168.rentalapp.database;

import cz.muni.fi.pv168.rentalapp.database.entities.Order;
import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManager {

    private DataSource dataSource;
    private ProductStackManager productStackManager;
    private JdbcTemplate jdbc;

    public OrderManager(DataSource dataSource) {
        this.dataSource = dataSource;
        this.productStackManager = new ProductStackManager(dataSource);
        this.jdbc = new JdbcTemplate(dataSource);
    }

    public Order getOrderById(long id) throws DatabaseException {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from orders where id = ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    return createOrderOnResultSet(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Cannot select order with id " + id + ": " + e);
            throw new DatabaseException("Database failed on order selection", e);
        }
    }

    public List<Order> getAllOrders() throws DatabaseException {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from orders")) {
                ResultSet rs = st.executeQuery();
                List<Order> orders = new ArrayList<>();
                while (rs.next()) {
                    Order o = createOrderOnResultSet(rs);
                    orders.add(o);
                }
                return orders;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error: Database select failed on getting all orders.", e);
        }
    }

    private List<ProductStack> getOrderedProductStacksByOrderId(long orderId) throws DatabaseException {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from orderedproductstacks where orderid = ?")) {
                st.setLong(1, orderId);
                ResultSet rs = st.executeQuery();
                List<ProductStack> orderedItems = new ArrayList<>();
                while (rs.next()) {
                    long orderedPSid = rs.getLong("id");
                    long storeId = rs.getLong("storeid");
                    int stackSize = rs.getInt("stackSize");
                    ProductStack storePS = productStackManager.getProductStackById(storeId);
                    orderedItems.add(new ProductStack(orderedPSid, storeId, storePS.getName(), storePS.getSize(), storePS.getPrice(), stackSize));
                }
                if (orderedItems.isEmpty()) {
                    throw new IllegalArgumentException("Database error: There were found no ordered products for given order.");
                }
                return orderedItems;
            }
        } catch (SQLException e) {
            System.err.println("Cannot select product stack with orderId " + orderId + ": " + e);
            throw new DatabaseException("Database failed on product stack selection", e);
        }
    }

    // just saving lines of code in getAllOrders and getOrderById
    private Order createOrderOnResultSet(ResultSet rs) throws SQLException, DatabaseException {
        long id = rs.getLong("id");
        String email = rs.getString("email");
        String fullName = rs.getString("fullname");
        String phoneNumber = rs.getString("phonenumber");
        LocalDate returnDate = rs.getDate("returndate").toLocalDate();
        List<ProductStack> orderItems = getOrderedProductStacksByOrderId(id);
        return new Order(id, orderItems, email, fullName, phoneNumber, returnDate);
    }

    public long insertOrder(List<ProductStack> productStacks, String email, String fullName, String phoneNumber, LocalDate returnDate) throws DatabaseException {
        SimpleJdbcInsert insertOrder = new SimpleJdbcInsert(jdbc).withTableName("orders").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>(4);
        parameters.put("email", email);
        parameters.put("fullname", fullName);
        parameters.put("phonenumber", phoneNumber);
        // LocalDate returnDate is already parsed to ISO_LOCAL pattern in DataManager.createOrder()
        parameters.put("returndate", Date.valueOf(returnDate));
        long orderId = insertOrder.executeAndReturnKey(parameters).longValue();

        insertOrderedProductStacks(productStacks, orderId);

        return orderId;
    }

    private void insertOrderedProductStacks(List<ProductStack> productStacks, long orderId) throws DatabaseException {
        SimpleJdbcInsert insertOrderedPS = new SimpleJdbcInsert(jdbc).withTableName("orderedproductstacks").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>(4);

        for (ProductStack ps : productStacks) {
            parameters.put("orderid", orderId);
            parameters.put("storeid", ps.getStoreId());
            parameters.put("stackSize", ps.getStackSize());

            int rowsAffected = insertOrderedPS.execute(parameters);
            if (rowsAffected != 1) {
                throw new DatabaseException("One affected row expected, got " + rowsAffected + " rows affected during adding products into OrderedProductStackTable.");
            }
        }
    }

    public void deleteOrder(Long orderId) {
        jdbc.update("DELETE FROM orders WHERE id=?", orderId);
    }

}


