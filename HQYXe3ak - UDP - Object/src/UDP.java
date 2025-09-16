import java.io.*;
import java.net.*;
import java.util.Locale;

// Lớp Customer
class Customer implements Serializable {
    private static final long serialVersionUID = 20151107L;

    String id;
    String code;
    String name;
    String dayOfBirth;
    String userName;

    public Customer(String id, String code, String name, String dayOfBirth, String userName) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.dayOfBirth = dayOfBirth;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Customer{id='" + id + "', code='" + code + "', name='" + name + "', dayOfBirth='" + dayOfBirth + "', userName='" + userName + "'}";
    }
}

// Server UDP
class UDPServer implements Runnable {
    @Override
    public void run() {
        try (DatagramSocket serverSocket = new DatagramSocket(2209)) {
            System.out.println("UDP Server running on port 2209...");

            // Nhận yêu cầu ban đầu
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            String request = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Server received request: " + request);

            // Tạo Customer mặc định với tên "tran duc anh"
            Customer c = new Customer("C001", "KH123", "tran duc anh", "10-11-2012", "");

            // Chuẩn bị dữ liệu để gửi lại
            String requestId = "REQ12345"; // 8 bytes
            byte[] customerBytes = serialize(c);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(requestId.getBytes(), 0, 8); // 8 byte đầu
            baos.write(customerBytes);              // object
            byte[] sendData = baos.toByteArray();

            // Gửi về client
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            serverSocket.send(sendPacket);

            // Nhận lại Customer đã sửa đổi
            byte[] buffer2 = new byte[4096];
            DatagramPacket modifiedPacket = new DatagramPacket(buffer2, buffer2.length);
            serverSocket.receive(modifiedPacket);

            // Parse dữ liệu
            byte[] data = modifiedPacket.getData();
            String reqIdBack = new String(data, 0, 8);
            Customer modifiedCustomer = (Customer) deserialize(data, 8, modifiedPacket.getLength() - 8);

            System.out.println("Server received modified object:");
            System.out.println("ReqId=" + reqIdBack + ", Customer=" + modifiedCustomer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Serialize
    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(obj);
        return baos.toByteArray();
    }

    // Deserialize
    private static Object deserialize(byte[] data, int offset, int length) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data, offset, length);
        ObjectInputStream in = new ObjectInputStream(bais);
        return in.readObject();
    }
}

// Client UDP
class UDPClient {
    public void runClient() {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress IPAddress = InetAddress.getLocalHost();

            // Gửi request đầu tiên với MSV B22DCVT034
            String studentCode = "B22DCVT034";
            String qCode = "HQYXe3ak";
            String request = ";" + studentCode + ";" + qCode;
            byte[] sendData = request.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 2209);
            clientSocket.send(sendPacket);

            // Nhận Customer từ server
            byte[] receiveData = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            byte[] data = receivePacket.getData();
            String requestId = new String(data, 0, 8);
            Customer c = (Customer) deserialize(data, 8, receivePacket.getLength() - 8);

            System.out.println("Client received object: " + c);

            // === Chỉnh sửa dữ liệu ===
            // 1. Chuẩn hóa tên: "tran duc anh" -> "ANH, Tran Duc"
            String[] parts = c.name.trim().split("\\s+");
            String lastName = parts[parts.length - 1].toUpperCase(Locale.ROOT);
            String firstNames = "";
            for (int i = 0; i < parts.length - 1; i++) {
                firstNames += capitalize(parts[i]) + " ";
            }
            c.name = lastName + ", " + firstNames.trim();

            // 2. Đổi ngày sinh mm-dd-yyyy -> dd/mm/yyyy
            String[] dobParts = c.dayOfBirth.split("-");
            if (dobParts.length == 3) {
                c.dayOfBirth = dobParts[1] + "/" + dobParts[0] + "/" + dobParts[2];
            }

            // 3. Sinh username: tda + anh = tdanh
            StringBuilder uname = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                uname.append(parts[i].charAt(0));
            }
            uname.append(parts[parts.length - 1]);
            c.userName = uname.toString().toLowerCase(Locale.ROOT);

            // Gửi lại Customer đã chỉnh sửa
            byte[] objBytes = serialize(c);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(requestId.getBytes(), 0, 8);
            baos.write(objBytes);
            byte[] sendBack = baos.toByteArray();

            DatagramPacket modifiedPacket = new DatagramPacket(sendBack, sendBack.length, IPAddress, 2209);
            clientSocket.send(modifiedPacket);

            System.out.println("Client sent modified object: " + c);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1).toLowerCase(Locale.ROOT);
    }

    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(obj);
        return baos.toByteArray();
    }

    private static Object deserialize(byte[] data, int offset, int length) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data, offset, length);
        ObjectInputStream in = new ObjectInputStream(bais);
        return in.readObject();
    }
}

// Main chạy thử
public class UDP {
    public static void main(String[] args) {
        // Chạy server trong 1 thread riêng
        new Thread(new UDPServer()).start();

        // Cho server khởi động trước một chút
        try { Thread.sleep(1000); } catch (InterruptedException e) { }

        // Chạy client
        UDPClient client = new UDPClient();
        client.runClient();
    }
}
