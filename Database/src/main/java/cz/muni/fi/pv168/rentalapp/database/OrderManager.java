package cz.muni.fi.pv168.rentalapp.database;

import cz.muni.fi.pv168.rentalapp.database.entities.Order;
import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack;
import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack.Size;
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

    public Order getOrderById(long id) throws DatabaseOrderException {
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
            throw new DatabaseOrderException("Database failed on order selection", e);
        }
    }

    public List<Order> getAllOrders() throws DatabaseOrderException {
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
            throw new DatabaseOrderException("Database error: Database select failed on getting all orders.", e);
        }
    }

    private List<ProductStack> getOrderedProductStacksByOrderId(long orderId) throws DatabaseOrderException {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from orderedproductstacks where orderid = ?")) {
                st.setLong(1, orderId);
                ResultSet rs = st.executeQuery();
                List<ProductStack> orderedItems = new ArrayList<>();
                while (rs.next()) {
                    long id = rs.getLong("id");
                    long storeId = rs.getLong("storeid");
                    String name = rs.getString("name");
                    Size size = Size.valueOf(rs.getString("size"));
                    double price = rs.getDouble("price");
                    int stackSize = rs.getInt("stacksize");
                    orderedItems.add(new ProductStack(id, orderId, storeId, name, size, price, stackSize));
                }
                if (orderedItems.isEmpty()) {
                    throw new IllegalArgumentException("Database error: There were found no ordered products for given order.");
                }
                return orderedItems;
            }
        } catch (SQLException e) {
            System.err.println("Cannot select product stack with orderId " + orderId + ": " + e);
            throw new DatabaseOrderException("Database failed on product stack selection", e);
        }
    }

    // just saving lines of code in getAllOrders and getOrderById
    private Order createOrderOnResultSet(ResultSet rs) throws SQLException, DatabaseOrderException {
        long id = rs.getLong("id");
        String email = rs.getString("email");
        String fullName = rs.getString("fullname");
        String phoneNumber = rs.getString("phonenumber");
        LocalDate returnDate = rs.getDate("returndate").toLocalDate();
        List<ProductStack> orderItems = getOrderedProductStacksByOrderId(id);
        return new Order(id, orderItems, email, fullName, phoneNumber, returnDate);
    }

    public void createOrder(Order order) {
        SimpleJdbcInsert insertOrder = new SimpleJdbcInsert(jdbc).withTableName("orders").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>(2);
        parameters.put("email", order.getEmail());
        parameters.put("fullname", order.getFullName());
        parameters.put("phonenumber", order.getPhoneNumber());
        // LocalDate returnDate is already parsed to ISO_LOCAL pattern in DataManager.createOrder()
        parameters.put("returndate", Date.valueOf(order.getReturnDate()));
        Number id = insertOrder.executeAndReturnKey(parameters);
        order.setId(id.longValue());
    }
}
//
//    public void deleteOrder(Long orderId) {
//
//    }

