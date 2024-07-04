package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
        AccountService accountService = new AccountService();
        MessageService messageService = new MessageService();
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);

        //POST localhost:8080/register
        app.post("register" , this::registerHandler);

        //POST localhost:8080/login
        app.post("login" , this::loginHandler);
        
        //POST localhost:8080/messages
        app.post("messages", this::postMessagesHandler);
        
        //GET localhost:8080/messages
        app.get("messages", this::getMessagesHandler);

        //GET localhost:8080/messages/{message_id}.
        app.get("messages/{message_id}", this::getOneMessageHandler);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    private void registerHandler(Context ctx) throws JsonParseException, JsonMappingException, IOException{
            String jsonString = ctx.body();
            ObjectMapper om = new ObjectMapper();
            Account account = om.readValue(jsonString, Account.class);
            /*- The registration will be successful if and only if the username is not blank, the password is at least 4 characters long, and an Account with that username
             does not already exist. If all these conditions are met, the response body should contain a JSON of the Account, including its account_id. The response status 
             should be 200 OK, which is the default. The new account should be persisted to the database.
            - If the registration is not successful, the response status should be 400. (Client error)*/
            if(account.getUsername() != "" && account.getPassword().length() >= 4){
                //ensure username is unique in the database???
                Account addedAccount = accountService.addAccount(account);
                ObjectMapper mapper = new ObjectMapper();
                if(addedAccount != null){
                    ctx.json(mapper.writeValueAsString(addedAccount));
                }else{
                    ctx.status(400);
                }
            }else{
                //response status should be 400//
                ctx.status(400);
            }
    }

    private void loginHandler(Context ctx) throws JsonParseException, JsonMappingException, IOException{
        /*
         * As a user, I should be able to verify my login on the endpoint POST localhost:8080/login. The request body will contain a JSON representation of an Account, 
         * not containing an account_id. In the future, this action may generate a Session token to allow the user to securely use the site. We will not worry about this for now.
        - The login will be successful if and only if the username and password provided in the request body JSON match a real account existing on the database. If successful, 
            the response body should contain a JSON of the account in the response body, including its account_id. The response status should be 200 OK, which is the default.
        - If the login is not successful, the response status should be 401. (Unauthorized)
         */
        String jsonString = ctx.body();
        ObjectMapper om = new ObjectMapper();
        Account accountCredintial = om.readValue(jsonString, Account.class);
        //call and use isAccountExist(Account account) method from the AccountService class
        Account confirmedAccount = accountService.isAccountExist(accountCredintial);
        if(confirmedAccount != null){
            ctx.json(confirmedAccount);
        }else{
            ctx.status(401);
        }
    }

    private void postMessagesHandler(Context ctx) throws JsonParseException, JsonMappingException, IOException{
        /*As a user, I should be able to submit a new post on the endpoint POST localhost:8080/messages. The request body will contain a JSON representation of a message, 
        which should be persisted to the database, but will not contain a message_id.
        - The creation of the message will be successful if and only if the message_text is not blank, is not over 255 characters, and posted_by refers to a real, existing user. 
        If successful, the response body should contain a JSON of the message, including its message_id. The response status should be 200, which is the default. The new message 
        should be persisted to the database.
        - If the creation of the message is not successful, the response status should be 400. (Client error) */
        String jsonString = ctx.body();
        ObjectMapper om = new ObjectMapper();
        Message newMessage = om.readValue(jsonString, Message.class);
        if(newMessage.getMessage_text() != ""){
                Message addedMessage = messageService.addNewMessage(newMessage);
                ObjectMapper mapper = new ObjectMapper();
                if(addedMessage != null){
                    ctx.json(mapper.writeValueAsString(addedMessage));
                }else{
                    ctx.status(400);
                }
        }else{
            ctx.status(400);
        }
    }

    private void getMessagesHandler(Context ctx) throws JsonParseException, JsonMappingException, IOException{
        /*As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages.
        - The response body should contain a JSON representation of a list containing all messages retrieved from the database. 
        It is expected for the list to simply be empty if there are no messages. The response status should always be 200, which is the default. */
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    private void getOneMessageHandler(Context ctx) throws JsonParseException, JsonMappingException, IOException{
        /*The response body should contain a JSON representation of the message identified by the message_id. It is expected for the response body to simply 
        be empty if there is no such message. The response status should always be 200, which is the default. */
        int id = Integer.parseInt(ctx.pathParam("message_id")) ;
        Message message = messageService.getMessageById(id);
        ctx.json(message);
        
    }

}