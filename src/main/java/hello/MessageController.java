package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @Autowired
    SimpMessagingTemplate msgTempl;

    private final String destination = "/topic/messaging";

    @MessageMapping("/message")
    @SendTo("/topic/messaging")
    public MessageToClient greeting(MessageToServer message) throws Exception {
        return new MessageToClient("## " + message.getMessage() + " ##");
    }

    @MessageMapping("messageChannel")
    public void greetingOnlyMe(MessageToServer message) throws Exception{
        MessageToClient messageToClient = new MessageToClient("CC " + message.getMessage() + " CC");
        msgTempl.convertAndSend(destination + "." + message.getChannel(), messageToClient);
    }

    @MessageMapping("/messageBoth/{channel}")
    @SendTo("/topic/messaging.{channel}")
    public MessageToClient greeting(MessageToServer message, @DestinationVariable String channel) throws Exception {
        String channelName = channel.toUpperCase();
        String content = channelName + "  " + message.getMessage() + "  " + channelName;
        MessageToClient messageToClient = new MessageToClient(content);

        //send message with msgTempl
        msgTempl.convertAndSend(destination, messageToClient);

        //send message with @SendTo
        return messageToClient;
    }


}
