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

        //DELETE localhost:8080/messages/{message_id}
        app.delete("messages/{message_id}", this::deleteOneMessageHandler);

        //PATCH localhost:8080/messages/{message_id}.
        app.patch("messages/{message_id}", this::updateOneMessageHandler);

        //GET localhost:8080/accounts/{account_id}/messages
        app.get("accounts/{account_id}/messages", this::getAllMessagesByUserId);

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
        Account accountCredential = om.readValue(jsonString, Account.class);
        //call and use isAccountExist(Account account) method from the AccountService class
        Account confirmedAccount = accountService.isAccountExist(accountCredential);
        if(confirmedAccount != null){        
            ctx.json(confirmedAccount);
            ctx.status(200);
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
        if(message == null){
            ctx.json("");
        }else{
            ctx.json(message);
        }
        
        
    }

    private void deleteOneMessageHandler(Context ctx) throws JsonParseException, JsonMappingException, IOException{
        /*The deletion of an existing message should remove an existing message from the database. If the message existed, the response body should contain 
        the now-deleted message. The response status should be 200, which is the default. If the message did not exist, the response status should be 200, 
        but the response body should be empty. This is because the DELETE verb is intended to be idempotent, ie, multiple calls to the DELETE endpoint should 
        respond with the same type of response. */
        int id = Integer.parseInt(ctx.pathParam("message_id")) ;
        Message deletedMessage = messageService.deleteMessageById(id);
        if(deletedMessage == null){
            ctx.json("");
            ctx.status(200);
        }
        ctx.json(deletedMessage);
        
    }

    private void updateOneMessageHandler(Context ctx) throws JsonParseException, JsonMappingException, IOException{
        /*As a user, I should be able to submit a PATCH request on the endpoint PATCH localhost:8080/messages/{message_id}. The request body should contain 
        a new message_text values to replace the message identified by message_id. The request body can not be guaranteed to contain any other information.
        The update of a message should be successful if and only if the message id already exists and the new message_text is not blank and is not over 255 characters. 
        If the update is successful, the response body should contain the full updated message (including message_id, posted_by, message_text, and time_posted_epoch), 
        and the response status should be 200, which is the default. The message existing on the database should have the updated message_text.
        If the update of the message is not successful for any reason, the response status should be 400. (Client error)*/
        int id = Integer.parseInt(ctx.pathParam("message_id")) ;
        ObjectMapper om = new ObjectMapper();
        Message message = om.readValue(ctx.body(), Message.class);
        String messageText = message.getMessage_text();
        if(messageText == "" || messageText.length() > 255){
            ctx.status(400);
        }else{
            Message updatedMessage = messageService.updateMessageById(id, messageText);
            if(updatedMessage == null){
            ctx.status(400);
            }else{
            ctx.json(message);
            ctx.status(200);
            }
        }
               
    }

    private void getAllMessagesByUserId(Context ctx) throws JsonParseException, JsonMappingException, IOException{
        /*As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/accounts/{account_id}/messages.
        The response body should contain a JSON representation of a list containing all messages posted by a particular user, 
        which is retrieved from the database. It is expected for the list to simply be empty if there are no messages. 
        The response status should always be 200, which is the default.. */
        int id = Integer.parseInt(ctx.pathParam("account_id"));
        ObjectMapper om = new ObjectMapper();
        List<Message> messages = messageService.getAllMessagesByUserId(id);
        ctx.json(messages);
    }

}