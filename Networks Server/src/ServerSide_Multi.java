import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerSide_Multi {
    public static void main(String[] args) throws IOException {
        int port = 4211;

        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("Server is listening on port "+port);
            while (true) {
                Socket sock = ss.accept();
                System.out.println("A thing connected...");

                ServerThread st = new ServerThread(sock);
                System.out.println(st.toString() + " is starting...");
                st.start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: " +e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ServerThread extends Thread {
        private Socket sock;

        public ServerThread(Socket socket) {
            this.sock = socket;
        }

        public void run() {
            InputStream input = null;
            BufferedReader iReader = null;
            OutputStream output = null;
            PrintWriter writer = null;
            Runtime runtime = null;
            try {
                input = sock.getInputStream();
                iReader = new BufferedReader(new InputStreamReader(input));
                output = sock.getOutputStream();
                writer = new PrintWriter(output, true);
                runtime = Runtime.getRuntime();

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
                                while ((s = outputReader.readLine()) != null) {
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
                output.close();
                input.close();

            } catch (IOException e) {

                System.out.println("Server Exception " + e.getMessage());
                e.printStackTrace();
                try {
                    sock.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
