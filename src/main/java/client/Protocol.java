package client;

public enum Protocol {
    CLIENT_COMPONENT("127.0.0.1", 23238);

    public String Ip;
    public int port;

    Protocol(String Ip, int port) {
        this.Ip = Ip;
        this.port = port;
    }
}
