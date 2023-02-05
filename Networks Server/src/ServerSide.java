import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerSide {
    public static void main(String[] args) throws IOException {
        int port = 4211;
        ServerSocket ss = new ServerSocket(port);
        /*
         Steps to be repeated for each new client:
         1. Create a server socket and bind it to a specific port number
         2. Listen for a connection from the client and accept it. This results in a client socket is created for the connection.
         3. Read data from the client via an InputStream obtained from the client socket.
         4. Send data to the client via the client socket’s OutputStream.
         5. Close the connection with the client.
         */
        while (true) {
            // Server created and bound to a specific port
            Socket sock = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            Runtime runtime = Runtime.getRuntime();


            try {
                // listen to connection and accept it
                sock = ss.accept();

                //create new thread to handle client connection

                System.out.println("A new client is connected : " + sock);
                // create InputStream and OutputStream to send and read data
                inputStream = sock.getInputStream();
                // InputStreamReader and BufferedReader used to read strings from input
                BufferedReader iReader = new BufferedReader(new InputStreamReader(inputStream));
                outputStream = sock.getOutputStream();
                // PrintWriter used to write strings easily
                PrintWriter writer = new PrintWriter(outputStream, true);

                /*
                int test = 2;
                writer.println(test + " sent from Server");
                */
                // Int corresponding to menu from client
                String argument = iReader.readLine();
                if (argument.matches("0")) {
                    System.out.println("Initial connection established, closing...");
                } else if (argument.matches("[1-6]")) {
                    String s = "";
                    switch (argument) {
                        case "1": // Date and Time, taken from javatpoint
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            writer.println(dtf.format(now));
                            writer.println("flag");
                            break;
                        case "2": // Display Uptime
                            try {
                                Process p = Runtime.getRuntime().exec("uptime");
                                s = "";
                                BufferedReader outputReader = new BufferedReader(new
                                        InputStreamReader(p.getInputStream()));
                                s = outputReader.readLine();
                                writer.println(s);
                                writer.println("flag");

                            } catch (IOException e) {
                                e.printStackTrace();
                                writer.println("Something went wrong");
                            }
                            break;
                        case "3": // Display current memory usage
                            float memory = runtime.totalMemory() - runtime.freeMemory();
                            s = ("Megabytes of memory in use: " + (memory / 1000000));
                            writer.println(s);
                            writer.println("flag");
                            break;
                        case "4": // Netstat
                            try {
                                Process p = Runtime.getRuntime().exec("netstat");
                                String bigString = "";
                                BufferedReader outputReader = new BufferedReader(new
                                        InputStreamReader(p.getInputStream()));
                                while (outputReader.readLine() != null) {
                                    s = outputReader.readLine();
                                    bigString = (bigString + s + "\n");
                                }
                                writer.print(bigString);
                                writer.println("flag");

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "5": // Current Users
                            try {
                                Process p = Runtime.getRuntime().exec("w");
                                s = "";
                                String bigString = "";
                                BufferedReader outputReader = new BufferedReader(new
                                        InputStreamReader(p.getInputStream()));
                                while (outputReader.readLine() != null) {
                                    s = outputReader.readLine();
                                    bigString = (bigString + s + "\n");
                                }
                                writer.print(bigString);
                                writer.println("flag");

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "6": // Running Processes
                            try {
                                Process p = Runtime.getRuntime().exec("ps -ef");
                                s = null;
                                String bigString = "";
                                BufferedReader outputReader = new BufferedReader(new
                                        InputStreamReader(p.getInputStream()));
                                while (outputReader.readLine() != null) {
                                    s = outputReader.readLine();
                                    bigString = (bigString + s + "\n");
                                }
                                writer.print(bigString);
                                writer.println("flag");

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            System.out.println("uh oh, default reached... do we even need this?");
                    }
                }

                // Close resources
                sock.close();
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                sock.close();
                outputStream.close();
                inputStream.close();
                ss.close();
                e.printStackTrace();
            } // end try/catch

        } // end while(true)
    }
}

