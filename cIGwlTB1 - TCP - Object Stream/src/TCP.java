import java.net.*;
import java.io.*;
import java.util.*;
import TCP.Product;

public class TCP {
    public static void main(String[] args) throws IOException {
        String serverID = "203.162.10.109";
        int port = 2209;
        String studentCode = "B22DCVT034";
        String qCode = "cIGwlTB1";
        String message = studentCode + ";" + qCode;

        try(Socket socket = new Socket(serverID, port)){
            socket.setSoTimeout(5000);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            // a. Send studentCode;qCode string
            oos.writeObject(message);
            oos.flush();

            // b. Receive Product object
            Product product = (Product) ois.readObject();
            System.out.println("Received product: " + product);

            // c. Calculate discount
            int priceInt = (int) product.getPrice();
            int discount = 0;
            while (priceInt > 0) {
                discount += priceInt % 10;
                priceInt /= 10;
            }
            product.setDiscount(discount);
            System.out.println("Updated product with discount: " + product);

            // Send updated product back
            oos.writeObject(product);
            oos.flush();

            // d. Close streams (try-with-resources will close socket)
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
