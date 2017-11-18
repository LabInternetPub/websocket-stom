package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

    @Autowired
    SimpMessagingTemplate msgTempl;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings/")
    public Greeting greeting(HelloMessage message) throws Exception {
        return new Greeting("## " + message.getMessage() + " ##");
    }

    @MessageMapping("helloMe")
    public void greetingOnlyMe(HelloMessage message) throws Exception{
        Greeting greeting = new Greeting("## " + message.getMessage() + " ##");
        msgTempl.convertAndSend("/topic/greetings/" + message.getChannel(), greeting);
    }

}
