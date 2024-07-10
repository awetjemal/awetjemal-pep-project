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
        MessageDAO msgDao = new MessageDAO();
        System.out.println("deleteMEssageById from the MessageService class");
        return msgDao.deleteMessageById(id);
        
    }

    public Message updateMessageById(int id, String messageText){
        
        if(messageDAO.getMessageById(id) != null){
            Message updatedMessage = messageDAO.updateMessageById(id, messageText);
            //System.out.println("The updatedmessage object --- from updateMessageById method from MessageService class is  " + updatedMessage);
            return updatedMessage;

        }
        System.out.print("Message-Id " + id + "does not exist");
        return null;
        //return messageDAO.updateMessageById(id);
    }
}
