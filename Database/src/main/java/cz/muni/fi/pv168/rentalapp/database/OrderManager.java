package cz.muni.fi.pv168.rentalapp.database;

import cz.muni.fi.pv168.rentalapp.database.entities.Order;
import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

    public List<Order> getAllOrders() throws DatabaseException {
        return jdbc.query("SELECT * FROM orders", orderMapper);
    }

    public Order getOrderById(long id) throws DatabaseException {
        return jdbc.queryForObject("SELECT * FROM orders WHERE id = ?", orderMapper, id);
    }

    public Order insertOrder(List<ProductStack> productStacks, String email, String fullName, String phoneNumber, LocalDate returnDate) throws DatabaseException {
        SimpleJdbcInsert insertOrder = new SimpleJdbcInsert(jdbc).withTableName("orders").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>(4);
        parameters.put("email", email);
        parameters.put("fullname", fullName);
        parameters.put("phonenumber", phoneNumber);
        // LocalDate returnDate is already parsed to ISO_LOCAL pattern in DataManager.createOrder()
        parameters.put("returndate", Date.valueOf(returnDate));
        long orderId = insertOrder.executeAndReturnKey(parameters).longValue();

        insertOrderedProductStacks(productStacks, orderId);
        System.out.println("(Customer " + fullName + "): Order  containing " + productStacks.size() + " items was added to database.");

        return new Order(orderId, productStacks, email, fullName, phoneNumber, returnDate);
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

    private List<ProductStack> getOrderedProductStacksByOrderId(long orderId) throws DatabaseException {
        return jdbc.query("SELECT * FROM orderedproductstacks WHERE orderid = ?", productStackManager.orderedProductStackMapper, orderId);
    }

    private RowMapper<Order> orderMapper = new RowMapper<Order>() {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong("id");
            String email = rs.getString("email");
            String fullName = rs.getString("fullname");
            String phoneNumber = rs.getString("phonenumber");
            LocalDate returnDate = rs.getDate("returndate").toLocalDate();
            List<ProductStack> orderProductStacks = null;
            try {
                orderProductStacks = getOrderedProductStacksByOrderId(id);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return new Order(id, orderProductStacks, email, fullName, phoneNumber, returnDate);
        }
    };
}

