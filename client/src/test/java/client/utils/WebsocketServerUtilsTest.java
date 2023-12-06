package client.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;


// could definitely be tested more diligently, however I tested the functionality by injecting it in main and that worked fine
class WebsocketServerUtilsTest {

    private WebsocketServerUtils socket;


    @BeforeEach
    public void setup(){
        this.socket = new WebsocketServerUtils();
    }

    //if you run the server it will break, since it does not throw an exception then
    @Test
    void initSocketTest(){
        assertThrows(ExecutionException.class, () -> this.socket.initSocket());
    }


    @Test
    void subscribeToBoard() {
        assertThrows(RuntimeException.class, () -> socket.subscribeToBoard(1));
    }

    @Test
    void getCurrentBoard() {
        assertThrows(RuntimeException.class, () -> socket.getCurrentBoard());
    }

    @Test
    void setServer() {
        String serverAddress = "serverAddress";
        socket.setServer(serverAddress);
        String newServer = socket.getServer();
        String expected = "ws://serverAddress";
        assertEquals(expected, newServer);
    }

}