import java.io.*;
import java.net.Socket;

public class Peer {
    Socket socket;
    public Peer(Integer client_port,Integer server_port) throws NumberFormatException, IOException
    {
        new Peer._Client(server_port).start();
        /*ServerSocket serverSocket=null;
        Socket socket = null;
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Client started. Listening for requests on port "+port.toString()+"...\n");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        while(true)
        {
            try{
                socket = serverSocket.accept();
            }
            catch(IOException e)
            {
                System.out.println("I/O error: " +e);
            }
            new Client.Client(socket).start();
        }*/

    }
    private static class _Client extends Thread{
        Integer port;
        public _Client(Integer port){
            this.port =port;
        }
        public void run() {
            try{
                Socket socket = new Socket("localhost",this.port);

                // Initializing output stream using the socket's output stream
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                // Initializing input stream using the socket's input stream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                // Read the initial welcome message from the server
                String response = (String) in.readObject();
                System.out.println(response);
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                int choice=0;
                Boolean retry = true;
                while (retry){
                    try{
                        System.out.println("Choose action");
                        System.out.println("[1] To register files");
                        System.out.println("[2] To unregister files");
                        System.out.println("[3] To look for files");
                        String line = reader.readLine().trim();
                        choice = Integer.parseInt(line);
                        retry = false;
                    }catch (NumberFormatException e){
                        System.out.println("Choice not valid");
                    }
                }
                switch (choice){
                    case 1:
                        System.out.println("\nEnter path of the files to sync with the server:");
                        String path = reader.readLine();
                        File file = new File(path);
                        IndexerRequest request = new IndexerRequest();
                        request.setRequestType(IndexerRequestType.REGISTER);
                        request.setRequestData(file);
                        out.writeObject(request);
                        response = (String) in.readObject();
                        System.out.println(response);
                }
            }catch (Exception e){
                System.err.println(e.getMessage());
            }

        }

    }
/*    private class Server extends Thread{
        private Socket socket= null;

        public Server(Socket socket){
            this.socket=socket;
            System.out.println("New connection with peer at " + socket.getInetAddress());
        }
        public void run() {
            try {
                // Initializing output stream using the socket's output stream
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                // Initializing input stream using the socket's input stream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                String clientIp = socket.getInetAddress().getHostAddress();
                out.writeObject("Your IP is: " + clientIp);
            } catch (Exception exc) {
                System.err.println(exc.getMessage());
            }
        }
    }*/
}
