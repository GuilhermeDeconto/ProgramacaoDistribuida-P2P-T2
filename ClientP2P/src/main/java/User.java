import java.util.ArrayList;

public class User {
    String ip;
    String name;
    ArrayList<UserFile> files;

    @Override
    public String toString() {
        return "User{" +
                "ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                ", files=" + files +
                '}';
    }
}
