package client.scenes;

import client.utils.ServerUtils;
import client.utils.WebsocketServerUtils;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ClientConnectCtrlTest {

    @Test
    public void testConstructor() {
        ServerUtils serverUtils = mock(ServerUtils.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        WebsocketServerUtils websocketServerUtils = mock(WebsocketServerUtils.class);

        ClientConnectCtrl clientConnectCtrl = new ClientConnectCtrl(serverUtils, mainCtrl, websocketServerUtils);

        assertEquals(serverUtils, clientConnectCtrl.getServerUtils());
        assertEquals(mainCtrl, clientConnectCtrl.getMainCtrl());
        assertEquals(websocketServerUtils, clientConnectCtrl.getWebsocketServerUtils());
    }

    @Test
    public void testClose() {
        ServerUtils serverUtils = mock(ServerUtils.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        WebsocketServerUtils websocketServerUtils = mock(WebsocketServerUtils.class);
        ClientConnectCtrl clientConnectCtrl = new ClientConnectCtrl(serverUtils, mainCtrl, websocketServerUtils);

        clientConnectCtrl.close();

        verify(mainCtrl).closeApp();
    }

    @Test
    public void testMinimize() {
        ServerUtils serverUtils = mock(ServerUtils.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        WebsocketServerUtils websocketServerUtils = mock(WebsocketServerUtils.class);
        ClientConnectCtrl clientConnectCtrl = new ClientConnectCtrl(serverUtils, mainCtrl, websocketServerUtils);

        clientConnectCtrl.minimize();

        verify(mainCtrl).minimizeStage();
    }

    @Test
    public void testMaxMin() {
        ServerUtils serverUtils = mock(ServerUtils.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        WebsocketServerUtils websocketServerUtils = mock(WebsocketServerUtils.class);
        ClientConnectCtrl clientConnectCtrl = new ClientConnectCtrl(serverUtils, mainCtrl, websocketServerUtils);

        clientConnectCtrl.maxMin();

        verify(mainCtrl).maxMinStage();
    }

}