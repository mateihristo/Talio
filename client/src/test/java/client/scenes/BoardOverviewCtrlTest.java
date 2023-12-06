package client.scenes;

import client.utils.ServerUtils;
import client.utils.WebsocketServerUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BoardOverviewCtrlTest {

    @Test
    public void testConstructor() {
        ServerUtils serverUtils = mock(ServerUtils.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        WebsocketServerUtils websocketServerUtils = mock(WebsocketServerUtils.class);

        BoardOverviewCtrl boardOverviewCtrl = new BoardOverviewCtrl(mainCtrl, serverUtils, websocketServerUtils);

        assertEquals(serverUtils, boardOverviewCtrl.getServerUtils());
        assertEquals(mainCtrl, boardOverviewCtrl.getMainCtrl());
        assertEquals(websocketServerUtils, boardOverviewCtrl.getWebsocketServerUtils());
    }

    @Test
    void close() {
        ServerUtils serverUtils = mock(ServerUtils.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        WebsocketServerUtils websocketServerUtils = mock(WebsocketServerUtils.class);

        BoardOverviewCtrl boardOverviewCtrl = new BoardOverviewCtrl(mainCtrl, serverUtils, websocketServerUtils);

        boardOverviewCtrl.close();

        verify(mainCtrl).closeApp();
    }

    @Test
    void minimize() {
        ServerUtils serverUtils = mock(ServerUtils.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        WebsocketServerUtils websocketServerUtils = mock(WebsocketServerUtils.class);

        BoardOverviewCtrl boardOverviewCtrl = new BoardOverviewCtrl(mainCtrl, serverUtils, websocketServerUtils);

        boardOverviewCtrl.minimize();

        verify(mainCtrl).minimizeStage();
    }

    @Test
    void maxMin() {
        ServerUtils serverUtils = mock(ServerUtils.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        WebsocketServerUtils websocketServerUtils = mock(WebsocketServerUtils.class);

        BoardOverviewCtrl boardOverviewCtrl = new BoardOverviewCtrl(mainCtrl, serverUtils, websocketServerUtils);

        boardOverviewCtrl.maxMin();

        verify(mainCtrl).maxMinStage();
    }

    @Test
    void disconnect() {
        ServerUtils serverUtils = mock(ServerUtils.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        WebsocketServerUtils websocketServerUtils = mock(WebsocketServerUtils.class);

        BoardOverviewCtrl boardOverviewCtrl = new BoardOverviewCtrl(mainCtrl, serverUtils, websocketServerUtils);

        boardOverviewCtrl.disconnect();

        verify(mainCtrl).showWelcomeOverview();
    }
}