# Rubric: Testing

The students are not supposed to submit a specific assignment, instead, you are supposed to look into their code base. You can share this rubric with the teams and ask them for pre-filled feedback until Friday.


### Coverage

Testing is an integral part of the coding activities. Unit tests cover all parts of the application (client, server, commons). Excellent teams will also pay attention to the (unit test) code coverage of crucial system components (according to the build result over all components).

Examples: commons/src/test/java/commons/ all of the classes in this dir,
server/src/test/java/server/api/ all of the classes in this dir,
client/src/test/java/client/utils/ all of the classes in this dir

- Excellent: The project reaches a code coverage of more than 80%.
- Good: All conceptual parts of the application have several automated tests (client, server, commons).
- Sufficient: The average MR does not only contain business logic, but also unit tests.
- Insufficient: The application is barely tested or tests are only added as an afterthought.


### Unit Testing

Classes are tested in isolation. Configurable *dependent-on-components* are passed to the *system-under-test* to avoid integration tests (for example, to avoid running a database or opening REST requests in each a test run).

To test classes in isolation, we decided to use Mockito and for the REST-api's we use MockMvc
Examples: server/src/test/java/server/api/BoardControllerTest.java , BoardListControllerTest.java, CardControllerTest.java

- Excellent: Configurable subclasses are created to replace dependent-on-components in most of the tests.
- Good: The project contains some examples, in which configurable subclasses are created to replace dependent-on-components in the tests.
- Sufficient: The project contains unit tests, but only for classes without dependent-on-components.
- Insufficient: The project only contains integration tests, which test multiple components at once.


### Indirection

The project applies the test patterns that have been covered in the lecture on *Dependency Injection*. More specifically, the test suite includes tests for indirect input/output and behavior.

Examples: the use of MockMvc allows us to test the behaviour and indirect input of REST-controllers in for 
example server/src/test/java/server/api/BoardControllerTest.java, BoardListControllerTest.java and CardControllerTest.java

- Excellent: The project contains tests that assert indirect input, indirect output, and behavior for multiple systems-under-test.
- Good: The project contains at least one test for each of the following assert types: 1. indirect input, 2. indirect output, and 3. behavior.
- Sufficient: The project contains at least one exemplary test that goes beyond asserting direct input/output of a system-under-test. For example, by asserting indirect input, indirect output, or behavior.
- Insufficient: The project does not test or only tests direct input/output.


### Endpoint Testing


The REST API is tested through automated JUnit tests.

Most endpoints of the REST API are tested, but please see 
server/src/test/java/server/api and if you want to see the client side, check 
client/src/test/java/client/utils/ServerUtilsTest.java

- Excellent: The project contains automated tests that cover regular and exceptional use of most endpoints.
- Good: The project contains automated JUnit tests that cover the regular use of multiple endpoints.
- Sufficient: The project contains one automated JUnit test that covers one use of one endpoint.
- Insufficient: The project does not use JUnit to test the REST API.

