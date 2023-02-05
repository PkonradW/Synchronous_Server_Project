import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyServer {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(4200);
        int port = 4200;
        String output = null;
        int input;


        //	Socket s  = ss.accept();

        while (true) {
            Runtime runtime = Runtime.getRuntime();
            float memory = runtime.totalMemory() - runtime.freeMemory();
            Socket sock = null;

            try {
                // socket object to receive incoming client requests
                sock = ss.accept();
                System.out.println("A new client is connected : " + sock);
                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(sock.getInputStream());
                DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                //printwriter
                System.out.println("Assigning new thread for this client");
                output = "Connection Established\n";
                //dos.write(output.getBytes(StandardCharsets.UTF_8));

                ///////
                PrintWriter pWriter = new PrintWriter(sock.getOutputStream(), true);

                Process runProcess = Runtime.getRuntime().exec("ps -aux");
                BufferedReader runReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                String run = "hello";
                //while ((run = runReader.readLine()) != null) {
                pWriter.println(run);
                //}
                //////

                input = Integer.parseInt(reader.readLine());
                if (input == 200) {
                    output = "Test connection detected, closing connection...\n";
                    System.out.println(output);
                    dos.write(output.getBytes(StandardCharsets.UTF_8));
                } else {
                    switch (input) {
                        case 1:
                            Date date = new Date();
                            DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
                            DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
                            //dos.writeUTF(fordate.format(date));
                            //dos.writeUTF(fortime.format(date));
                            output = "case 1";
                            dos.write(output.getBytes(StandardCharsets.UTF_8));
                            System.out.println("case 1 written");
                            break;

                        case 2:
                            output = "case 2";
                            dos.write(output.getBytes(StandardCharsets.UTF_8));
                            System.out.println("Case 2 written");
                            break;

                        case 3:
                            dos.writeUTF("case 3");
                            System.out.println("Case 3 written");
                            break;

                        case 4:
                            break;

                        default:
                            dos.writeUTF("Invalid input");
                            dos.writeUTF("boogity");
                            break;
                    }
                }


                // create a new thread object
                //Thread t = new ClientHandler(sock, dis, dos);

                // Invoking the start() method
                //t.start();
                sock.close();
            } catch (Exception e) {
                sock.close();
                e.printStackTrace();
            }
        }
    }

    public static void printOutput(Socket sock) throws IOException {
        PrintWriter pWriter = new PrintWriter(System.out);
        BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String read;
        while ((read = reader.readLine()) != null) {
            pWriter.println(read);
        }
    }
}

// ClientHandler class
class ClientHandler extends Thread {
    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;


    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        System.out.println("Constructor reached");
    }

    @Override
    public void run() {
        int received;
        String toReturn;
        while (true) {
            try {

                // Ask user what he wants
                dos.writeUTF("Connection Established");
                dos.writeUTF("boogity");


                // receive the answer from client
                received = Integer.parseInt(dis.readUTF());

                // I don't think this is going to work
                if (received == 0) {
                    System.out.println("Client " + this.s + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }

                // creating Date object
                Date date = new Date();


                // write on output stream based on the
                // answer from the client
                switch (received) {

                    case 1:
                        toReturn = fordate.format(date);
                        dos.writeUTF(toReturn);
                        dos.writeUTF("case 1");
                        System.out.println("case 1 written");
                        break;

                    case 2:
                        toReturn = fortime.format(date);
                        dos.writeUTF(toReturn);
                        dos.writeUTF("case 2");
                        System.out.println("Case 2 written");
                        break;

                    case 3:
                        Process p = Runtime.getRuntime().exec("runtime");
                        BufferedReader stdInput = new BufferedReader(new
                                InputStreamReader(p.getInputStream()));
                        while ((toReturn = stdInput.readLine()) != null) {
                            dos.writeUTF(toReturn);
                            dos.writeUTF("boogity");
                        }

                    default:
                        dos.writeUTF("Invalid input");
                        dos.writeUTF("boogity");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // closing resources
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}