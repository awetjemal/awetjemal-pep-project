package Service;

import Model.Message;
import DAO.MessageDAO;

import java.util.ArrayList;
import java.util.List;

public class MessageService {
    private MessageDAO messageDAO = new MessageDAO();

    public Message addNewMessage(Message message){
        return messageDAO.insertNewMessage(message);
    }

    public List<Message> getAllMessages(){
        List<Message> messages = messageDAO.getAllMessages();
        return messages;
    }

    public List<Message> getAllMessagesByUserId(int user_id){
        List<Message> messages = messageDAO.getAllMessagesByUserId(user_id);
        return messages;
    }

    public Message getMessageById(int id){
        return messageDAO.getMessageById(id);
    }

    public Message deleteMessageById(int id){
        if(messageDAO.getMessageById(id) != null){
            return messageDAO.deleteMessageById(id);
        }
        
        return null;
               
    }

    public Message updateMessageById(int id, String messageText){
        
        if(messageDAO.getMessageById(id) != null){
            messageDAO.updateMessageById(id, messageText);
            return messageDAO.getMessageById(id);
        }
        System.out.print("Message-Id " + id + "does not exist");
        return null;
        //return messageDAO.updateMessageById(id);
    }
}
