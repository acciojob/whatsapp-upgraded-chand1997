package com.driver;


import org.springframework.stereotype.Service;

import java.util.Date;

import java.util.List;

@Service
public class WhatsappService {

   WhatsappRepo whatsappRepo=new WhatsappRepo();
    public String createUser(String name, String mobile) throws Exception {
       User user=new User();
       user.setMobile(mobile);
       user.setName(name);
      return  whatsappRepo.createUser(user);
    }

    public Group createGroup(List<User> users) {
        return whatsappRepo.createGroup(users);

    }

    public int createMessage(String content) {
        Message message=new Message();
        message.setContent(content);
        message.setTimestamp(new Date());
        return whatsappRepo.createMessage(message);
    }

    public int sendMessage(Message message, User user, Group group) throws Exception {
       return  whatsappRepo.sendMessage(message,user,group);
    }

    public String changeAdmin(User currentAdmin, User adminToBe, Group group) throws Exception {
        return  whatsappRepo.changeAdmin(currentAdmin,adminToBe,group);
    }

    public int removeUser(User user) throws Exception {
        return whatsappRepo.removeUser(user);
    }

    public String findMessage(Date start, Date end, int k) throws Exception{
        return whatsappRepo.findMessage(start,end,k);
    }
}
