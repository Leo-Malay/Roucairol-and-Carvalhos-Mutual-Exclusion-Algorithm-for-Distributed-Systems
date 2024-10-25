import java.io.*;

enum MessageType {
    REQUEST, REPLY, BOTH,
};

public class Message implements Serializable {
    public int id = -1;
    public MessageType msgType;
    public int clock;
    public int key = -1;

    public Message(MessageType type, int id, int clock, int key) {
        this.msgType = type;
        this.id = id;
        this.clock = clock;
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
