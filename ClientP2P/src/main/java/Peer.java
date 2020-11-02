import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Peer implements Serializable {

    public String name; // client name
    public String address; // client address
    public HashMap<String, String> files; // array for all hash and path files

    public Peer(String n, String a, String pathFiles) {
        name = n;
        address = a;
        files = new HashMap<String, String>();
        saveAllFilesPath(pathFiles);
    }

    public Peer(String n, String ip, HashMap<String, String> userFile) {
        name = n;
        address = ip;
        files = userFile;
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

    public void saveAllFilesPath(String rootPath) {
        System.out.println("Reading your files to share...");
        try (Stream<Path> paths = Files.walk(Paths.get(rootPath))) {

            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        files.put(getHashFile(filePath.toString()), filePath.toString());
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

    public String getHashFile(String path) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(Paths.get(path)));

            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean haveFileByHash(String hash) {
        for (String fileHash : files.keySet()) {
            if (fileHash.equals(hash)) {
                return true;
            }
        }
        return false;
    }

    public String pathByHash(String hash) {
        for (String fileHash : files.keySet()) {
            if (fileHash.equals(hash)) {
                return files.get(fileHash);
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Map<String, String> getFiles() {
        return files;
    }

}