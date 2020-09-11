import java.net.*;
import java.io.*;

public class Transfer {

    public static void sendAsFile(Socket socket, String filename) throws IOException {
        int count;
        byte[] buffer = new byte[1024];

        InputStream in = new BufferedInputStream(new FileInputStream(filename));
        OutputStream out = socket.getOutputStream();

        while((count=in.read(buffer)) > -1){
            out.write(buffer, 0, count);
            out.flush();
        }
        in.close(); //outputStream will be handled by calling function
    }

    public static void receiveAsFile(Socket socket, String filename) throws IOException {
        int count;
        byte[] buffer = new byte[1024];

        File file = new File(filename);
        file.createNewFile();

        InputStream in = socket.getInputStream();
        OutputStream out = new BufferedOutputStream(new FileOutputStream(filename));

        while ((count = in.read(buffer)) > -1) {
            out.write(buffer, 0, count);
            out.flush();
        }
        out.close(); //inputStream will be handled by calling function
    }
}
