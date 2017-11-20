# Websockets with Stomp

This example implements a very limited chat. It provides with two channels, a default one and another that the user 
can name. In case the user does not name the channel, both will be considered the same.

Once the client is connected to the two channels, messages can be sent to either channel or to both at the same time.
Many clients can connect to the channels but the end users are not identified (as said it's a very simple chat).ç

The example uses WebSockets and/or SockJS. This last is used in case the browser doesn't support Websockets. It also 
uses [STOMP](https://stomp.github.io/stomp-specification-1.2.html) which is a simple interoperable protocol 
designed for asynchronous message passing between clients via mediating servers or **message brokers**. 
It defines a text based wire-format for messages passed between these clients and servers.



### Configuration

Will need to add the following dependencies to the *pom.xml* (apart from the ones you can guess). The first is for the
server and the others are for the client (the browser)
```
       <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>webjars-locator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>sockjs-client</artifactId>
            <version>1.0.2</version>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>stomp-websocket</artifactId>
            <version>2.3.3</version>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>jquery</artifactId>
            <version>3.1.0</version>
        </dependency>

```


For the java configuration: 
We need to configure a message broker and a web server. The browser (client) will send messages to a web server which in turn will send a message to the broker. This last
broker which will then send the message to all the connected clients.


```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket").withSockJS();
    }

}
```
* *registerStompEndpoints* registers the end point where client browsers will need to connect to send messages to
the web server via websockets

* *configureMessageBroker* creates a simple message broker that lives in memory (*enableSimpleBroker*) that will be
'hearing' in a queue that begins with **'/topic'**. It also sets the web server link prefix to **'/app'** 

### The web server
The web server is configured as all the servers we've seen up now but it expects webSocket messages that are marsalled as
always

```java
@Controller
public class MessageController {

    @Autowired
    SimpMessagingTemplate msgTempl;

    @MessageMapping("/message")
    @SendTo("/topic/messaging/")
    public MessageToClient greeting(MessageToServer message) throws Exception {
        return new MessageToClient("## " + message.getMessage() + " ##");
    }
}
```
This controller receives a message from the browser (via webSockets). The client needs to send the message to **"/app/message"** 
and sends another message to the broker at channel **"/topic/messaging"**. Once the broker receives the message it is in
charge of sending it to all clients registered to this channel.

### The browser (client)

The client first needs to attach the socket to the declared entry-point (*'/gs-guide-websocket'*)and then subscribe to 
all the channels it needs to (*'/topic/messaging/'*).
```javascript
function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/messaging/', function (greeting) {
            ... //do whatever
        });
    });
}
```

Then the client can send messages to the web controller stating the receiving method (almost like it was a regular http call)
```javascript
function sendMessage(mapping) {
    stompClient.send(mapping, {}, JSON.stringify({'message': $("#message").val(), 'channel': myChannel}));
}
```
Where mapping can be *'app/message'* to reach the method annotated with *@MessageMapping("/message")*


## Credits
This code is based on https://github.com/spring-guides/gs-messaging-stomp-websocket from the Spring guides
 https://spring.io/guides/gs/messaging-stomp-websocket/
 
