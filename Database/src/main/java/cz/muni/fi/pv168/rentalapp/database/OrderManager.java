package cz.muni.fi.pv168.rentalapp.database;

import cz.muni.fi.pv168.rentalapp.database.entities.Order;
import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack;
import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack.Size;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderManager {

    private DataSource dataSource;

    public OrderManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Order getOrderById(long id) throws DatabaseOrderException {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from orders where id = ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    // is nid necessary?
                    long nid = rs.getLong("id");
                    String email = rs.getString("email");
                    String fullName = rs.getString("fullname");
                    String phoneNumber = rs.getString("phonenumber");
                    // db contains dates in 'YYYY-MM-DD', these can be converted straightforwardly
                    LocalDate returnDate = rs.getDate("returndate").toLocalDate();
                    List<ProductStack> orderItems = getOrderItems(nid);
                    return new Order(nid, orderItems, email, fullName, phoneNumber, returnDate);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Cannot select order with id " + id + ": " + e);
            throw new DatabaseOrderException("Database failed on order selection", e);
        }
    }

    private List<ProductStack> getOrderItems(long orderId) throws DatabaseOrderException {
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

    public List<Order> getAllOrders() throws DatabaseOrderException {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from orders")) {
                ResultSet rs = st.executeQuery();
                List<Order> orders = new ArrayList<>();
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String email = rs.getString("email");
                    String fullName = rs.getString("fullname");
                    String phoneNumber = rs.getString("phonenumber");
                    LocalDate returnDate = rs.getDate("returndate").toLocalDate();
                    List<ProductStack> orderItems = getOrderItems(id);
                    orders.add(new Order(id, orderItems, email, fullName, phoneNumber, returnDate));
                }
                return orders;
            }
        } catch (SQLException e) {
            throw new DatabaseOrderException("Database error: Database select failed on getting all orders.", e);
        }
    }
}
    //TODO. Prevents copy paste code.
//    private Order createOrderOnData(ResultSet rs) {
//        return new Order(id, orderItems, email, fullName, phoneNumber, returnDate);
//    }

//    public void insertOrder(Order order) {
//
//    }
//
//    public void deleteOrder(Long orderId) {
//
//    }

