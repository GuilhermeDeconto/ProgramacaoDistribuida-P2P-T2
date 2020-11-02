package com.company;

import com.google.gson.Gson;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;


public class App {
	public static final String CHECK_IF_EXIST_USER = "exist";
    public static final String GET_USER_WITH_HASH = "getFileHash";
    public static final String GET_RESOURCES_LIST = "getResources";
    public static final String GET_FILE_FROM_USER = "getFile";
	public static String endpointServer = "http://9039d4a4e3fd.ngrok.io/api/v1/resources";
	public static String myIp = "192.168.0.8";
	public static String name = "Daniel_Oliveira";
	public static String pathFiles = "Arquivos1";
	public static ArrayList<String[]> files; 
	
    public static void main(String[] args) throws IOException {
    	//IP::13.13.13.13
    	//NAME::Daniel_Oliveira
    	//PATHFILES::Arquivos1
    	//SERVER::http://0cc544a67828.ngrok.io/api/v1/resources
    	files = new ArrayList<String[]>();
    	args(args);
    	lerArquivos(pathFiles);
    	
    	
    	post(endpointServer + "/peer", mapPeer(myIp, name, files));
			
		new Thread() {

			@Override
			public void run() {
			    	
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
				public void run() {
					try {
						get(endpointServer + "/peer/heartbeat/" + name);
			        } catch (Exception e) {
			        	System.out.print("HeartBeat failed");
			            e.printStackTrace();
			            }
			      }}, 0, 5000);}}.start();
			  
			      
			  new Thread(openSocketServer).start();
			  
			  Peer mainPeer = new Peer(name, myIp, pathFiles);
			  
			  String text = "";

	            Scanner scan = new Scanner(System.in);
	            
	            do {
	                /*
	                 * le comando do usuario: -- exist <client_name> -- getFileHash <file_hash> --
	                 * getResources <file_1> <file_2> <...> -- getFile <file>
	                 */
	                text = scan.nextLine();
	                commandController(text, mainPeer);
	            } while (!text.equalsIgnoreCase("exit"));
		
    }
    
    public static void commandController(String command, Peer thisPeer)
            throws RemoteException, IOException {
        String[] commands = command.split(" ");

        switch (commands[0]) {

            // solicitar recurso especifico
            // getFile d4cffa3b48ee3848da861aac7ffbd2f9
            case GET_FILE_FROM_USER:
                Peer peerWithFile = getClientWithFileHash(commands[1]);
                thisPeer.requestFile(commands[1], peerWithFile);
                break;

            // saber quem tem recurso especifico
            // getFileHash d4cffa3b48ee3848da861aac7ffbd2f9
            case GET_USER_WITH_HASH:
                System.out.println("com.company.Peer que possui arquivo: " + getClientWithFileHash(commands[1]).name + " Ip: " + getClientWithFileHash(commands[1]).address);
                break;

            // solicitar lista de recursos
            // getResources
            case GET_RESOURCES_LIST:

                ResponseWrapper2 response = getAllFileHash();
                for (User user : response.data) {
                    System.out.println(user.toString());
                }

                break;

            // verificar se usuario existe
            // exist Daniel_Oliveira
            case CHECK_IF_EXIST_USER:
                ResponseWrapper user = peerExist(commands[1]);
                if(user.data != null){
                    System.out.println("Usuario " + user.data.name + " esta registrado.");
                } else {
                    System.out.println("Usuario " + commands[1] + " nao esta registrado.");
                }
                break;
        }
    }

    private static ResponseWrapper peerExist(String name) {
        String response = get(endpointServer + "/peer/name/" + name).get(0);
        Gson gson = new Gson();
        ResponseWrapper responseWrapper = gson.fromJson(response, ResponseWrapper.class);
        return responseWrapper;
    }

    private static ResponseWrapper2 getAllFileHash() {
        String response = get(endpointServer).get(0);
        Gson gson = new Gson();
        ResponseWrapper2 responseWrapper2 = gson.fromJson(response, ResponseWrapper2.class);
        return responseWrapper2;
    }

    private static Peer getClientWithFileHash(String hash) {
    	String response = get(endpointServer + "/peer/file/" + hash).get(0);
        Gson gson = new Gson();
        ResponseWrapper responseWrapper = gson.fromJson(response, ResponseWrapper.class);

        HashMap<String, String> userFile = new HashMap<String, String>();
        for (int i = 0; i < responseWrapper.data.files.size(); i++){
            userFile.put(responseWrapper.data.files.get(i).hash, responseWrapper.data.files.get(i).name);
        }
    	return new Peer(responseWrapper.data.name, responseWrapper.data.ip, userFile);
	}

	public static void args(String[] args) {

    	for (String arg : args) {

			arg = arg.trim();

			if (arg.contains("IP:")) {

				String[] argIp = arg.split("::");
				myIp = argIp[1];
			} else if (arg.contains("PATHFILES:")) {
					String[] argFileName = arg.split("::");
					pathFiles = argFileName[1];
			} else if (arg.contains("NAME:")) {
				String[] argName = arg.split("::");
				name = argName[1];
			} else if (arg.contains("SERVER:")) {
				String[] argServer = arg.split("::");
				endpointServer = argServer[1];
			} 
    	}
			
			System.out.print("server " + endpointServer);
			System.out.print(" ip " + myIp);
			System.out.print(" name " + name);
			System.out.println(" file " + pathFiles);
    }
    
    public static ArrayList<String> get(String urlRequest) {
    	try {

            URL url = new URL(urlRequest);//your url i.e fetch data from .
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : "
                        + conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String nextLine;
            ArrayList<String> output = new ArrayList<String>();
            while ((nextLine = br.readLine()) != null) {
                output.add(nextLine);
            }
            conn.disconnect();
            return output;

        } catch (Exception e) {
            System.out.println("Exception in NetClientGet:- " + e);
        }
    	return null;
    }

    public static void post(String urlRequest, String body) throws IOException {
    	URL url = new URL (urlRequest);
		
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("POST");
		
		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setRequestProperty("Accept", "application/json");
		
		con.setDoOutput(true);
		
		//JSON String need to be constructed for the specific resource. 
		//We may construct complex JSON using any third-party JSON libraries such as jackson or org.json
		String jsonInputString = body;
		
		try(OutputStream os = con.getOutputStream()){
			byte[] input = jsonInputString.getBytes("utf-8");
			os.write(input, 0, input.length);			
		}

		int code = con.getResponseCode();
		System.out.println(code);
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))){
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			System.out.println(response.toString());
		}
		
    }
    
    public static String mapPeer(String ip, String name, ArrayList<String[]> files) {
    	String fileNames = "";
    	for (int i = 0; i < files.size(); i++) {
			fileNames += "{"
					+ "\"hash\": \"" + files.get(i)[1] + "\","
					+ "\"name\": \"" + files.get(i)[0] + "\""
					+ "},";
		}
    	fileNames = fileNames.substring(0, fileNames.length() - 1);
    	return "{\"ip\": \""+ip+"\", \"name\": \""+name+"\", "
				+ "\"files\": ["
				+ fileNames
				+ "]}";
    }
    
    private static Runnable openSocketServer = () -> {
        try {
            ServerSocket server = null;

            server = new ServerSocket(4444);
            System.out.println("Socket Server created ");

            boolean done = false;

            while (!done) {
                Socket client = server.accept(); // get sender
                DataInputStream dIn = new DataInputStream(client.getInputStream());
                byte messageType = dIn.readByte(); // read message

                switch (messageType) {
                    case 1: // file solicitation           RECEBENDO SOLICITACAO
                        String fileToSend = dIn.readUTF();
                        System.out.println("Requested file: " + fileToSend);
                        try {
                            Socket peerServer = new Socket(client.getInetAddress(), 4444);

                            DataOutputStream dOut = new DataOutputStream(peerServer.getOutputStream());

                            FileInputStream fis = new FileInputStream(fileToSend);
                            String[] file = fileToSend.split("\\.");
                            String[] fileName = file[0].split(Pattern.quote(File.separator));
                            String extension = "." + fileName[fileName.length - 1] + "-" + file[1] + ".";
                            byte[] extensionByte = extension.getBytes();
                            for (byte b : extensionByte) {
                                dOut.write(b);
                            }
                            byte[] buffer = new byte[4096];
                            int count;
                            while ((count = fis.read(buffer)) >= 0) {
                                dOut.write(buffer, 0, count);
                            }


                            fis.close();
                            dOut.close();

                        } catch (Exception e) {
                            System.out.println("Erro ao enviar arquivo ao solicitante");
                            e.printStackTrace();
                        }
                        break;
                    default: // receiving file   
                        System.out.println("File received");

                        DataInputStream dis = new DataInputStream(client.getInputStream());
                        byte[] searchBuffer = dis.readAllBytes();
                        byte[] fileContent = new byte[0];
                        byte[] fileExtension = new byte[0];
                        byte[] fileName = new byte[0];
                        boolean hasName = false;
                        boolean hasFileExtension = false;
                        int fileNameLastByte = 0;
                        int count = 0;
                        for (byte infoByte : searchBuffer) {
                            // verify if byte == "-"
                            if (infoByte == 45) {
                                if (!hasName) {
                                    fileName = Arrays.copyOfRange(searchBuffer, 0, count);
                                    fileNameLastByte = count;
                                    hasName = true;
                                }
                            }
                            // verify if byte == "."
                            if (infoByte == 46) {
                                if (!hasFileExtension) {
                                    fileExtension = Arrays.copyOfRange(searchBuffer, fileNameLastByte + 1, count);
                                    fileContent = Arrays.copyOfRange(searchBuffer, count + 1, searchBuffer.length);
                                    hasFileExtension = true;
                                }
                            }
                            count++;
                        }

                        FileOutputStream fos = new FileOutputStream("" + new String(fileName) + "." + new String(fileExtension));

                        int totalRead = 0;
                        for (byte info : fileContent) {
                            fos.write(info);
                            totalRead++;
                        }
                        System.out.println("Read " + totalRead + " bytes.");

                        fos.close();
                        dis.close();
                        break;
                }
                client.close();
                dIn.close();
            }

        } catch (IOException e) {
            System.out.println("Failed to create socket server");
            e.printStackTrace();
        }
    };
    
    public static void lerArquivos(String rootPath) {
        System.out.println("Reading your files to share...");
        try (Stream<Path> paths = Files.walk(Paths.get(rootPath))) {

            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                    	String[] f = {filePath.toString(), getHashFile(filePath.toString())};
                        files.add(f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            System.out.println("Files ready");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String getHashFile(String path) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(Paths.get(path)));

            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void requestFile(String hashFile, Peer host) throws IOException {
        String pathFile = host.pathByHash(hashFile);

        Socket socket = new Socket(host.getAddress(), 4444);
        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

        // file request
        dOut.writeByte(1);
        // file path
        dOut.writeUTF(pathFile);
        dOut.flush(); // Send off the data

        // finish
        dOut.writeByte(-1);
        dOut.flush(); // Send off the data

        dOut.close();

    }
    
    public static String pathByHash(String hash) {
    	for (int i = 0; i < files.size(); i++) {
			if(files.get(i)[1].equals(hash)) {
				return files.get(i)[0];
			}
		}
        return null;
    }
    
    
}