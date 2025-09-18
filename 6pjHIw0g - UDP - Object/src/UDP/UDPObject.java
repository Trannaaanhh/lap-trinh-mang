package UDP;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class UDPObject {
    public static void main(String[] args) {
        String serverHost = "203.162.10.109";
        int serverPort = 2209;
        String studentCode = "B22DCVT034";
        String qCode = "6pjHIw0g";

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);

            // 1. Gửi request
            String request = ";" + studentCode + ";" + qCode;
            byte[] sendData = request.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length,
                    InetAddress.getByName(serverHost), serverPort
            );
            socket.send(sendPacket);
            System.out.println("Sent request: " + request);

            // 2. Nhận phản hồi
            byte[] receiveBuffer = new byte[65535];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            byte[] responseData = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());

            // 8 byte đầu là requestId
            String requestId = new String(responseData, 0, 8);
            System.out.println("Received requestId = " + requestId);

            // phần còn lại là object Product
            byte[] objectBytes = Arrays.copyOfRange(responseData, 8, responseData.length);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(objectBytes));
            Product product = (Product) ois.readObject();
            System.out.println("Received Product: " + product);

            // 3. Sửa dữ liệu sai
            String fixedName = fixProductName(product.getName());
            int fixedQuantity = fixQuantity(product.getQuantity());

            product.setName(fixedName);
            product.setQuantity(fixedQuantity);

            System.out.println("Fixed Product: " + product);

            // 4. Gửi lại object đã sửa
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(product);
            oos.flush();

            byte[] productBytes = baos.toByteArray();
            byte[] finalSendData = new byte[8 + productBytes.length];

            // copy requestId vào 8 byte đầu
            System.arraycopy(requestId.getBytes(), 0, finalSendData, 0, 8);
            // copy object phía sau
            System.arraycopy(productBytes, 0, finalSendData, 8, productBytes.length);

            DatagramPacket sendBack = new DatagramPacket(
                    finalSendData, finalSendData.length,
                    InetAddress.getByName(serverHost), serverPort
            );
            socket.send(sendBack);

            System.out.println("Sent fixed product back to server.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ====== Hàm sửa tên sản phẩm ======
    private static String fixProductName(String wrongName) {
        String[] parts = wrongName.split(" ");
        if (parts.length > 1) {
            // chỉ hoán đổi từ đầu với từ cuối
            String tmp = parts[0];
            parts[0] = parts[parts.length - 1];
            parts[parts.length - 1] = tmp;
        }
        return String.join(" ", parts);
    }

    // ====== Hàm sửa số lượng sản phẩm ======
    private static int fixQuantity(int wrongQuantity) {
        String str = String.valueOf(wrongQuantity);
        String reversed = new StringBuilder(str).reverse().toString();
        return Integer.parseInt(reversed);
    }
}

// ==================== CLASS PRODUCT ====================
class Product implements Serializable {
    private static final long serialVersionUID = 20161107L;

    private String id;
    private String code;
    private String name;
    private int quantity;

    public Product(String id, String code, String name, int quantity) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }

    public void setId(String id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "Product{id='" + id + "', code='" + code + "', name='" + name + "', quantity=" + quantity + "}";
    }
}
