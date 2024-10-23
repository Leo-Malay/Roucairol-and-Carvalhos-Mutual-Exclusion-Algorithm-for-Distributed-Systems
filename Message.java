import java.io.*;
import java.util.*;

enum MessageType {
    REQUEST, REPLY
};

public class Message implements Serializable {
    public int id = -1;
    public MessageType messageType;
    public int clock;
    String key;
   
    public Message(MessageType type, int id, Vector<Integer> timestamp) {
        this.messageType = type;
        this.id = id;
        this.clock = timestamp;
        this.key = key;
    }

    public byte[] toMessageBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(this);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static Message fromByteArray(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (Message) objectInputStream.readObject();
        }
    }
}
