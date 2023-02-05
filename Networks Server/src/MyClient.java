import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

// Client class
public class MyClient {
    public static void main(String[] args) throws IOException {
        int outputInt = 0;
        byte outputByte;
        String outputUTF = null;
        Scanner scan = new Scanner(System.in);
        int port = 4200;
        //InetAddress ip = InetAddress.getByName("139.62.210.153");
        InetAddress ip = InetAddress.getByName("127.0.0.1");
        try {
            /*
            System.out.println("Enter IP Address to connect to");
            String ipAddress = (scan.nextLine());
             */

            // establish the connection with port specified by 'port' var
            Socket sock = new Socket(ip, port);
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(dis));
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            PrintWriter pWriter = new PrintWriter(sock.getOutputStream(), true);


            printOutput(sock);
            pWriter.println("200"); // send arg indicating test connection
            printOutput(sock);

            //printOutput(dis);
            dis.close();
            dos.close();

            // the following loop performs the exchange of
            // information between client and client handler
            boolean False = true;
            while (False) {
                /*
                int length = dis.available();
                byte[] buffer = new byte[length];
                StringBuilder stringB = new StringBuilder(length);
                dis.readFully(buffer);
                for (byte b: buffer) {
                    char c = (char) b;
                    stringB.append(c);
                }
                System.out.println(stringB);

                 */

                /*
                if (output == 200) {
                    scan.close();
                    dis.close();
                    dos.close();
                }
                 */
                ArrayList<Integer> arguments = menu();
                if (arguments.get(0) == 0) {
                    False = false;
                    break;
                }

                ArrayList<Thread> threadList = new ArrayList<>();
                // loop that initializes a bunch of threads with the same argument
                for (int i = 0; i < arguments.get(1); i++) {
                    Thread t = new ThreadMaker(port, ip, dis, dos, arguments.get(0));
                    threadList.add(t);
                    System.out.println("threads made " + i);
                }
                // start em up
                /*for (Thread t: threadList) {
                    t.start();
                    System.out.println("threads started");
                }

                 */

                threadList.forEach(Thread::start);
                for (Thread thread : threadList) {
                    thread.join();
                }

                // waits for all the threads to finish before restarting loop
                // this also doesn't work
                int flag = 0;
                while (flag < threadList.size()) {
                    for (Thread t : threadList) {
                        if (t.isAlive()) {
                            flag++;
                        }
                    }
                }
                /*
                dos.writeUTF(toSend);
                
                // If client sends exit,close this connection
                // and then break from the while loop
                if(toSend.equals("Exit"))
                {
                    System.out.println("Closing this connection : " + sock);
                    sock.close();
                    System.out.println("Connection closed");
                    break;
                }
                
                // printing date or time as requested by client
                String received = dis.readUTF();
                System.out.println(received);

                 */
            }

            // closing resources

        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Prints a menu and returns input from the user.
     * First element: User's choice from menu
     * Second element: Number of threads to start
     *
     * @return A two-element Arraylist of integers.
     * @throws IOException
     */
    public static ArrayList<Integer> menu() throws IOException {
        Scanner scan = new Scanner(System.in);
        ArrayList<Integer> arguments = new ArrayList<>(2);
        int choice = 0;

        System.out.println("Please Select an option:");
        System.out.println("   1. Print Date and Time");
        System.out.println("   2. Display Uptime");
        System.out.println("   3. Display current memory usage on the server");
        System.out.println("   4. Netstat");
        System.out.println("   5. List currently connected users");
        System.out.println("   6. List Programs running on the server");
        System.out.println("   0. exit");
        while (arguments.isEmpty()) {
            try {
                choice = Integer.parseInt(scan.nextLine());
                arguments.add(choice);
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer as input");
            }
        }
        if (choice != 0) {
            choice = 0;

            System.out.println("How many threads do you want to spam the server with?");
            while (choice == 0) {
                try {
                    choice = Integer.parseInt(scan.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Please enter an integer as input");
                }
                if (choice == 0) System.out.println("Enter a number greater than 0");


            }
            arguments.add(choice);
        }
        return arguments;

    }


    /**
     * Extends thread class with important variables for this project, as well as an overwritten run() method
     */
    static class ThreadMaker extends Thread {
        final int port;
        final InetAddress ip;
        final DataOutputStream dos;
        final DataInputStream dis;
        final int arg1;
        String output;

        public ThreadMaker(int port, InetAddress ip, DataInputStream dis, DataOutputStream dos,
                           int arg1) {
            this.port = port;
            this.ip = ip;
            this.dis = dis;
            this.dos = dos;
            this.arg1 = arg1;
        }

        @Override
        public void run() {
            try {
                Socket sock = new Socket(ip, port);
                DataInputStream dis = new DataInputStream(sock.getInputStream());
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
                // clear out the input buffer just in case
                // read everything from server side
                printOutput(sock);

                dos.writeInt(arg1);
                // read everything in the input buffer
                printOutput(sock);
                System.out.println("thread closing");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /*
    This doesn't work and needs to be replaced with something that uses the PrintWriter on the Server Side
     */

}
