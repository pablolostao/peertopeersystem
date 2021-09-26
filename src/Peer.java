import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardWatchEventKinds.*;

//General class for a peer, it has another class for server part and another one for client part
public class Peer {

    //Index of local files
    ConcurrentHashMap<String,File> fileNameToFile=null;


    public Peer(Integer peer_port,Integer server_port) throws NumberFormatException, IOException
    {
        fileNameToFile = new ConcurrentHashMap<String,File>();
        new Peer._Client(peer_port,server_port,fileNameToFile).start();
        ServerSocket serverSocket=null;
        Socket socket = null;
        try{
            //Create a server socket to listen for requests
            serverSocket = new ServerSocket(peer_port);
            System.out.println("Peer listening for requests on port "+peer_port.toString()+"...\n");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        //Always listening
        while(true)
        {
            try{
                //For each new connection we create a thread Server, that manages request for one peer
                socket = serverSocket.accept();
            }
            catch(IOException e)
            {
                System.out.println("I/O error: " +e);
            }
            new Peer._Server(socket,fileNameToFile).start();
        }

    }

    //Class to act as a server (retrieve files)
    private static class _Server extends Thread{
        ConcurrentHashMap<String,File> fileNameToFile = null;
        Socket socket = null;

        public _Server(Socket socket, ConcurrentHashMap<String,File> fileNameToFile){
            this.fileNameToFile = fileNameToFile;
            this.socket =socket;
        }
        //Thread itself
        public void run() {
            try {
                //Get the file they are looking for
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                String fileName = (String) in.readObject();
                File internalFile = fileNameToFile.get(fileName);
                //If it exists, return it
                if (internalFile!=null){
                    byte[] mybytearray  = new byte [(int)internalFile.length()];
                    FileInputStream fis = new FileInputStream(internalFile);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);
                    FileResponse response = new FileResponse();
                    response.setSize(mybytearray.length);
                    response.setRequestData(mybytearray);
                    out.writeObject(response);
                    out.flush();
                    out.close();
                    in.close();
                    fis.close();
                    bis.close();
                    System.out.println("File transferred");
                }
            }catch (Exception e){
                System.err.println(e);
            }

        }
    }

    //Class to act as a client (user actions)
    private static class _Client extends Thread{
        Integer indexer_port;
        Integer peer_port;
        Path shared_directory=null;
        ConcurrentHashMap<String,File> fileNameToFile = null;

        public _Client(Integer peer_port,Integer indexer_port,ConcurrentHashMap<String,File> fileNameToFile){
            this.fileNameToFile = fileNameToFile;
            this.indexer_port =indexer_port;
            this.peer_port =peer_port;
        }

        //Thread itself
        public void run() {
            try{
                Socket socket = new Socket("localhost",this.indexer_port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                out.writeObject(this.peer_port);
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                String response = (String) in.readObject();
                System.out.println(response);
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                Boolean retry = true;
                IndexerRequest request = null;
                //Get shared folder
                while(retry){
                    try{
                        System.out.println("Insert the path of the shared directory:");
                        String dirPath = reader.readLine().trim();
                        File dir = new File(dirPath);
                        if (dir.isDirectory()){
                            File[] files = dir.listFiles();
                            if(files !=null){
                                request = new IndexerRequest();
                                request.setRequestType(IndexerRequestType.REGISTER_FOLDER);
                                request.setRequestData(files);
                                out.reset();
                                out.writeObject(request);
                                for (File child : files) {
                                    fileNameToFile.put(child.getName(),child);
                                }
                            }
                            shared_directory = dir.toPath();
                            retry = false;
                        }else{
                            System.out.println("The path is not a directory");
                        }
                        response = (String) in.readObject();
                        System.out.println(response);
                    }catch (NumberFormatException e){
                        System.out.println("Path not valid");
                    }
                }
                //Create another thread to watch directory events
                WatchService watcher = FileSystems.getDefault().newWatchService();
                shared_directory.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                new _Watcher(in,out,watcher,fileNameToFile).start();
                int choice=0;
                //The client waits for a user action
                while(true){
                    retry = true;
                    while (retry){
                        try{
                            System.out.println("Choose action");
                            System.out.println("[1] To look for alternatives to download a file");
                            System.out.println("[2] To download a specific file from specific peer");
                            System.out.println("[3] To exit");
                            String line = reader.readLine().trim();
                            choice = Integer.parseInt(line);
                            retry = false;
                        }catch (NumberFormatException e){
                            System.out.println("Choice not valid");
                        }
                    }
                    boolean exit = false;
                    switch (choice) {
                        //We request to the indexer information about one file
                        case 1:
                            System.out.println("\nEnter the name of the file you are looking for:");
                            String name = reader.readLine().trim();
                            request = new IndexerRequest();
                            request.setRequestType(IndexerRequestType.LOOKFOR);
                            request.setRequestData(name);
                            out.writeObject(request);
                            HashSet<String> set = (HashSet<String>) in.readObject();
                            if (set==null || set.isEmpty()){
                                System.out.println("File does not exist in the server");
                            }else{
                                System.out.println("You can find the file "+name+" in the following peers: "+set.toString());
                            }
                            break;
                        //We request to another peer a file
                        case 2:
                            System.out.println("\nEnter the name of the file you are looking for:");
                            String fileName = reader.readLine().trim();
                            System.out.println("\nEnter the peer you want to download from (address:port):");
                            String peer = reader.readLine().trim();
                            String[] address_port=peer.split(":");
                            Socket fileSocket = new Socket(address_port[0],Integer.parseInt(address_port[1]));
                            ObjectOutputStream outFile = new ObjectOutputStream(fileSocket.getOutputStream());
                            ObjectInputStream inFile = new ObjectInputStream(fileSocket.getInputStream());
                            outFile.writeObject(fileName);
                            FileResponse res = (FileResponse) inFile.readObject();
                            byte [] mybytearray  = res.getRequestData();
                            Path pathToFile = shared_directory.resolve(fileName);
                            OutputStream fileOutputStream = new FileOutputStream(pathToFile.toFile());
                            BufferedOutputStream  bufferedOutputStream= new BufferedOutputStream(fileOutputStream);
                            bufferedOutputStream.write(mybytearray, 0 ,mybytearray.length );
                            bufferedOutputStream.flush();
                            fileOutputStream.close();
                            bufferedOutputStream.close();
                            outFile.close();
                            inFile.close();
                            break;
                        //Exit
                        case 3:
                            exit = true;
                            break;
                    }
                    if(exit){
                        break;
                    }
                }

            }catch (Exception e){
                System.err.println(e.getMessage());
            }

        }

        private static class _Watcher extends Thread{
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            WatchService watcher = null;
            ConcurrentHashMap<String,File> fileNameToFile = null;
            public _Watcher(ObjectInputStream in, ObjectOutputStream out,WatchService watcher,ConcurrentHashMap<String,File> fileNameToFile) throws IOException{
                this.in =in;
                this.out =out;
                this.watcher = watcher;
                this.fileNameToFile=fileNameToFile;
            }

            //Method to look for events and act as needed
            void processEvents() throws Exception{
                IndexerRequest request = null;
                String response = null;
                while(true) {
                    WatchKey key;
                    try {
                        key = watcher.take();
                    } catch (InterruptedException x) {
                        System.out.println("Exception");
                        return;
                    }
                    for (WatchEvent<?> event: key.pollEvents()) {
                        WatchEvent.Kind kind = event.kind();
                        if (kind == OVERFLOW || kind == ENTRY_MODIFY) {
                            continue;
                        }
                        if (kind == ENTRY_CREATE) {
                            WatchEvent<Path> ev = (WatchEvent<Path>)event;
                            Path filepath = ev.context();
                            File file = new File(filepath.toString());
                            request = new IndexerRequest();
                            request.setRequestType(IndexerRequestType.REGISTER);
                            request.setRequestData(file);
                            out.writeObject(request);
                            response = (String) in.readObject();
                            System.out.println(response);
                            continue;
                        }
                        if (kind == ENTRY_DELETE) {
                            WatchEvent<Path> ev = (WatchEvent<Path>)event;
                            Path filepath = ev.context();
                            File file = new File(filepath.toString());
                            if(fileNameToFile.containsKey(file.getName())){
                                fileNameToFile.remove(file.getName());
                            }
                            request = new IndexerRequest();
                            request.setRequestType(IndexerRequestType.UNREGISTER);
                            request.setRequestData(file);
                            out.writeObject(request);
                            response = (String) in.readObject();
                            System.out.println(response);
                            continue;
                        }
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            }
            public void run() {
                try{
                    processEvents();
                }catch (Exception e){
                    System.err.println(e);
                }
            }
        }

    }
}
