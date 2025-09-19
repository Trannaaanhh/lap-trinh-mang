import java.net.*;
import java.io.*;
import java.util.*;
public class TCP {
    public static void main(String[] args) throws IOException {
        String serverID = "203.162.10.109";
        int port = 2209;
        String studentCode = "B22DCVT034";
        String qCode = "clGwlTB1";
        String message = studentCode + ";" + qCode;

        try(Socket socket = new Socket(serverID, port)){
            socket.setSoTimeout(5000);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            //
        }
    }
}
