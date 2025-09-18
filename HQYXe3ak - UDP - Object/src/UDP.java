import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class UDP extends JFrame {
    private JTextField txtMSV;
    private JTextField txtQCode;
    private JTextArea txtOutput;
    private JButton btnConnect;

    public UDP() {
        setTitle("TCP Client - Sum Numbers");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel nhập thông tin
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("Mã SV:"));
        txtMSV = new JTextField("B22DCVT034");
        inputPanel.add(txtMSV);

        inputPanel.add(new JLabel("QCode:"));
        txtQCode = new JTextField("1MdSxrzp");
        inputPanel.add(txtQCode);

        // Nút kết nối
        btnConnect = new JButton("Kết nối & Xử lý");
        btnConnect.addActionListener(e -> connectAndProcess());

        // Output hiển thị kết quả
        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtOutput);

        // Layout
        add(inputPanel, BorderLayout.NORTH);
        add(btnConnect, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void connectAndProcess() {
        String serverAddress = "203.162.10.109";
        int port = 2206;
        String msv = txtMSV.getText().trim();
        String qCode = txtQCode.getText().trim();

        try (Socket socket = new Socket(serverAddress, port)) {
            socket.setSoTimeout(5000);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // a. Gửi mã sinh viên và mã câu hỏi
            String request = msv + ";" + qCode + "\n";
            out.write(request.getBytes());
            out.flush();
            txtOutput.append("Sent: " + request.trim() + "\n");

            // b. Nhận dữ liệu từ server
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            if (bytesRead == -1) {
                txtOutput.append("Không nhận được dữ liệu từ server!\n");
                return;
            }
            String response = new String(buffer, 0, bytesRead).trim();
            txtOutput.append("From server: " + response + "\n");

            // c. Tính tổng
            String[] parts = response.split("\\|");
            int s = 0;
            for (String p : parts) {
                s += Integer.parseInt(p.trim());
            }

            // d. Gửi tổng về server
            String result = s + "\n";
            out.write(result.getBytes());
            out.flush();
            txtOutput.append("Sent: " + s + "\n");

            in.close();
            out.close();
            socket.close();
            txtOutput.append("End program\n");

        } catch (Exception ex) {
            txtOutput.append("Error: " + ex.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UDP client = new UDP();
            client.setVisible(true);
        });
    }
}
