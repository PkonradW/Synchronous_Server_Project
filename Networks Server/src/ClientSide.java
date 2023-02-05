import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientSide {
    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        int port = 4211;
        //InetAddress ip = InetAddress.getByName("127.0.0.1");
        //InetAddress ip = InetAddress.getByName("139.62.210.153");

        // get server IP address and port (how is this going to work on the server side?)
        //                                 - it isn't
        ArrayList<String> connectionArgs = getStuff();
        InetAddress ip = InetAddress.getByName(connectionArgs.get(0));
        port = Integer.parseInt(connectionArgs.get(1));

        try (Socket sock = new Socket(ip, port)) {
            InputStream inputStream = sock.getInputStream();
            BufferedReader iReader = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream = sock.getOutputStream();
            // PrintWriter used to write strings easily
            PrintWriter writer = new PrintWriter(outputStream, true);

            // send argument indicating test connection and close the connection
            writer.println(0);
            inputStream.close();
            outputStream.close();
            sock.close();

            boolean loop = true;
            while (loop) {
                float totalTime = 0;
                float averageTime;
                ArrayList<Integer> arguments = menu();
                int argument = arguments.get(0);
                if (argument == 0) {
                    loop = false;
                } else {
                    int amount = arguments.get(1);
                    ArrayList<ThreadMaker> threadList = new ArrayList<>();
                    for (int i = 0; i < amount; i++) {
                        ThreadMaker t = new ThreadMaker(port, ip, argument);
                        threadList.add(t);
                    }

                    // Start threads and wait for them to end before continuing
                    for (ThreadMaker t : threadList) t.start();
                    for (ThreadMaker t : threadList) t.join();
                    // Sum all the elapsedTime variables from each thread
                    for (ThreadMaker t : threadList) {
                        System.out.println("Turnaround time for " + t.toString() + ": " +
                                t.elapsedTime + "ms");
                        totalTime += t.elapsedTime;
                    }
                    averageTime = totalTime / amount;
                    System.out.printf("Average time: %.2fms\n", averageTime);
                    System.out.println("Total Time: " + totalTime + "ms");
                    // end timer
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }// end try/catch
    }

    static class ThreadMaker extends Thread {
        final int port;
        final InetAddress ip;
        OutputStream outputStream;
        InputStream inputStream;
        final int argument;
        String input;
        long startTime;
        long finishTime;
        long elapsedTime;

        public ThreadMaker(int port, InetAddress ip, int argument) throws IOException {
            this.port = port;
            this.ip = ip;
            this.argument = argument;
        }

        @Override
        public void run() {
            /*
            Will do the following:
            1. Create new socket, InputStream, OutputStream, PrintWriter and BufferedReader
            2. write the argument
            3. read the input
             */
            try {
                startTime = System.currentTimeMillis();
                Socket sock = new Socket(ip, port);
                inputStream = sock.getInputStream();
                BufferedReader iReader = new BufferedReader(new InputStreamReader(inputStream));
                outputStream = sock.getOutputStream();
                // PrintWriter used to write strings easily
                PrintWriter writer = new PrintWriter(outputStream, true);

                writer.println(argument);
                do {
                    input = iReader.readLine();
                    if (input != null && !input.matches("null")) {
                        if (!input.matches("flag")) System.out.println(input);
                    }
                } while (!input.matches("flag"));
                finishTime = System.currentTimeMillis();
                elapsedTime = finishTime - startTime;
                //System.out.println("Elapsed Time: " + elapsedTime + "ms");
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        System.out.println("Please select an option:");
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
                if (choice <= 0) System.out.println("Enter a number greater than 0");
            }
            arguments.add(choice);
        }
        return arguments;
    } // end menu()

    /**
     * Function will get IP address and port number from client, then return both inside
     * of an ArrayList of Strings
     *
     * @return 2 element ArrayList, Index 0 is the ip address as a string, Index 1 is the port
     */
    public static ArrayList<String> getStuff() {
        ArrayList<String> stuffList = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        System.out.println("enter IP address of desired connection: ");
        stuffList.add(scan.nextLine());
        System.out.println("Enter desired port: ");
        stuffList.add(scan.nextLine());
        return stuffList;
    }
}
