# Rubric: Technology

The students are not supposed to submit a specific assignment, instead, you are supposed to look into their code base.

### Dependency Injection

Application uses dependency injection to connect dependent components. No use of static fields in classes.

Examples: 
server/src/main/java/server/api/BoardController.java , BoardListController.java , CardController.java , TaskController.java and WebsocketController.java

client/src/main/java/client/scenes/AddCardCtrl.java, BoardOverviewCtrl.java , CardOverviewCtrl.java, EditListNameCtrl.java, ListContainerCtrl.java


- *Excellent:* The application (client and server) uses dependency injection everywhere to connect dependent components. The projects also binds some external types so they can be injected.
- *Good:* The application (client and server) uses dependency injection in multiple places to connect dependent components. The server makes use of Spring annotations to access path variables, parameters, and request bodies.
- *Sufficient:* There is one example in the client code and one in the server code that uses dependency injection to connect dependent components. Static fields and methods are only sparely used to access other components.
- *Insufficient:* Static fields or methods are used. Implementation of the singleton pattern.


### Spring Boot

Application makes good use of the presented Spring built-in concepts to configure the server and maintain the lifecycle of the various server components.

Examples:
see the controllers in server/src/main/java/server/api/
and the jparepositories in server/src/main/java/server/database
and the component BoardUpdateListener.java in server/src/main/java/server/api

- *Excellent:* Additional @Services are defined, which encapsulate business logic or shared state.
- *Good:* The application contains example of @Controller, @RestController, and a JPA repository.
- *Sufficient:* The application uses Spring for the server.
- *Insufficient:* The application uses regular socket communication.


### JavaFX

Application uses JavaFX for the client and makes good use of available features (use of buttons/images/lists/formatting/…). The connected JavaFX controllers are used with dependency injection.

Examples: client/src/main/java/client/scenes/AddCardCtrl.java, BoardOverviewCtrl.java , CardOverviewCtrl.java, EditListNameCtrl.java, ListContainerCtrl.java

- *Excellent:* The JavaFX controllers are used with dependency injection.
- *Good:* The UI contains more than just buttons, text fields, or labels. The application contains images and a non-default layout.
- *Sufficient:* Application uses JavaFX for the client.
- *Insufficient:* 


### Communication

Application uses communication via REST requests and Websockets. The code is leveraging the canonical Spring techniques for endpoints and websocket that have been introduced in the lectures. The client uses libraries to simplify access.

See any of the REST-controllers in server/src/main/java/server/api/ like BoardController.java
And see server/src/main/java/server/api/WebsocketController.java and server/src/main/java/server/api/WebsocketConfiguration.java

- *Excellent:* The server defines all REST and webservice endpoints through Spring and uses a client library like Jersey (REST) or Stomp (Webservice) to simplify the server requests.
- *Good:* All communication between client and server is implemented with REST or websockets.
- *Sufficient:* The application contains functionality that uses 1) a REST request AND 2) long-polling AND 3) websocket communication (in different places).
- *Insufficient:* The application does not contain functionality that uses a REST request OR 2) long-polling, OR 3) websocket communication.


### Data Transfer

Application defines meaningful data structures and uses Jackson to perform the de-/serialization of submitted data.

Examples of meaningful data structures: commons/src/main/java/commons/ Board.java, BoardList.java, Card.java, Task.java
Examples of implicit use of Jackson: server/src/main/java/server/api/BoardController.java , BoardListController.java, CardController.java

- *Excellent:* Jackson is used implicitly by Spring or the client library. No explicit Jackson calls are required in the application.
- *Good:* Application defines data structures and both client and server use Jackson to perform the de-/serialization of submitted data. If required, custom Jackson modules are provided that can de-/serialize external types.
- *Insufficient:* Client or server manually create or parse String messages.


