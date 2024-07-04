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

    public Message getMessageById(int id){
        return messageDAO.getMessageById(id);
    }
}
