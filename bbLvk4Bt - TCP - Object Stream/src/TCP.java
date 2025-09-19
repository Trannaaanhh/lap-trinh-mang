import java.io.*;
import java.net.*;
// Import TCP.Address from separate file
import TCP.Address;

public class TCP {
    public static void main(String[] args) {
        String serverID = "203.162.10.109";
        int port = 2209;
        String studentCode = "B22DCVT034"; // <-- thay bằng mã sinh viên của bạn
        String qCode = "bbLvk4Bt";
        String message = studentCode + ";" + qCode;

        try (Socket socket = new Socket(serverID, port)) {
            socket.setSoTimeout(5000);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            // a. Gửi "studentCode;qCode"
            oos.writeObject(message);
            oos.flush();

            // b. Nhận TCP.Address object
            Address address = (Address) ois.readObject();
            System.out.println("Received address: " + address);

            // c. Chuẩn hóa thông tin
            address.setAddressLine(normalizeAddressLine(address.getAddressLine()));
            address.setPostalCode(normalizePostalCode(address.getPostalCode()));

            System.out.println("Normalized address: " + address);

            // gửi lại TCP.Address object đã chuẩn hóa
            oos.writeObject(address);
            oos.flush();

            System.out.println("Done. Connection closed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm chuẩn hóa addressLine
    private static String normalizeAddressLine(String input) {
        if (input == null) return null;
        // Giữ lại chữ cái, số, khoảng trắng và chèn khoảng trắng giữa các số và chữ nếu liền nhau
        // VD: "NguyenThiMinh406TranVanBao" -> "Nguyen Thi Minh 406 Tran Van Bao"
        // Bước 1: Thêm khoảng trắng giữa chữ và số
        String step1 = input.replaceAll("([a-zA-Z])([0-9])", "$1 $2").replaceAll("([0-9])([a-zA-Z])", "$1 $2");
        // Bước 2: Loại bỏ ký tự đặc biệt, chỉ giữ lại chữ, số và khoảng trắng
        String cleaned = step1.replaceAll("[^a-zA-Z0-9\\s]", " ");
        // Bước 3: Loại bỏ khoảng trắng thừa
        cleaned = cleaned.trim().replaceAll("\\s+", " ");
        // Bước 4: Viết hoa chữ cái đầu mỗi từ
        StringBuilder sb = new StringBuilder();
        for (String word : cleaned.split(" ")) {
            if (word.isEmpty()) continue;
            sb.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                sb.append(word.substring(1).toLowerCase());
            }
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    // Hàm chuẩn hóa postalCode
    private static String normalizePostalCode(String input) {
        if (input == null) return null;
        return input.replaceAll("[^0-9-]", "");
    }
}
