package com.driver;


import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepo {
//    key=mobile,value=User
    HashMap<String,User> userDb=new HashMap<>();

    int groupChats=0;
    int personalChats=0;


//    key=Group-Name,value=userMobile
    HashMap<String,String> groupAdminDb=new HashMap<>();



//    key=Group-Name,value=Group
    HashMap<String,Group> groupNameToGroupDb=new HashMap<>();


    HashMap<Integer,Message> messageIdToMessageDb=new HashMap<>();

//    key=userMobile , value=GroupName
    HashMap<String,String> userGroupNameDb=new HashMap<>();


    HashMap<String,List<Integer>> groupNameToMessageIdsDb=new HashMap<>();

    HashMap<String,List<Integer>> userMobileToMessageIdsDb=new HashMap<>();


    public String createUser(User user) throws Exception{
        if(userDb.containsKey(user.getMobile())){
            throw new Exception("User already exists");
        }
        userDb.put(user.getMobile(),user);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        Group group=new Group();
        if(users.size()==2) personalChats++;
        else groupChats++;

       if(users.size()==2) group.setName(users.get(1).getName());
       else group.setName("Group "+ groupChats);

       group.setNumberOfParticipants(users.size());


       groupAdminDb.put(group.getName(),users.get(0).getMobile());

       groupNameToGroupDb.put(group.getName(),group);
       for(User user:users){
           userGroupNameDb.put(user.getMobile(), group.getName());
       }
       return group;

    }

    public int createMessage(Message message) {
        message.setId(messageIdToMessageDb.size()+1);
        messageIdToMessageDb.put(message.getId(),message);
        return message.getId();
    }

    public int sendMessage(Message message, User user, Group group) throws Exception {
        if(!groupNameToGroupDb.containsKey(group.getName())){
            throw new Exception("Group does not exist");
        }

        if(!userGroupNameDb.get(user.getMobile()).equals(group.getName())){
            throw new Exception("You are not allowed to send message");
        }
        if(!groupNameToMessageIdsDb.containsKey(group.getName())){
            groupNameToMessageIdsDb.put(group.getName(), new ArrayList<>());
        }
        groupNameToMessageIdsDb.get(group.getName()).add(message.getId());

        if(!userMobileToMessageIdsDb.containsKey(user.getMobile())){
            userMobileToMessageIdsDb.put(user.getMobile(),new ArrayList<>());
        }
        userMobileToMessageIdsDb.get(user.getMobile()).add(message.getId());

        return groupNameToMessageIdsDb.get(group.getName()).size();

    }

    public String changeAdmin(User currentAdmin, User adminToBe, Group group) throws Exception {
        if(!groupNameToGroupDb.containsKey(group.getName())){
            throw new Exception("Group does not exist");
        }
        if(!groupAdminDb.get(group.getName()).equals(currentAdmin.getMobile())){
            throw new Exception("Approver does not have rights");
        }
        if(!userGroupNameDb.get(adminToBe.getMobile()).equals(group.getName())){
            throw new Exception("User is not a participant");
        }
        groupAdminDb.put(group.getName(),adminToBe.getMobile());
        return "SUCCESS";

    }

    public int removeUser(User user) throws Exception {
        if(!userGroupNameDb.containsKey(user.getMobile())){
            throw new Exception("User not found");
        }
        for(String adminMobile:groupAdminDb.values()){
            if(adminMobile.equals(user.getMobile())){
                throw new Exception("Cannot remove admin");
            }
        }
        String groupName=userGroupNameDb.get(user.getMobile());
        userGroupNameDb.remove(user.getMobile());
       List<Integer> messageIds= userMobileToMessageIdsDb.get(user.getMobile());
       userMobileToMessageIdsDb.remove(user.getMobile());

       for(int id:messageIds){
           groupNameToMessageIdsDb.get(groupName).remove(id);
           messageIdToMessageDb.remove(id);
       }

      Group group= groupNameToGroupDb.get(groupName);
       group.setNumberOfParticipants(group.getNumberOfParticipants()-1);
       groupNameToGroupDb.put(groupName,group);

       return group.getNumberOfParticipants()+groupNameToMessageIdsDb.get(groupName).size()+messageIdToMessageDb.size();


    }

    public String findMessage(Date start, Date end, int k) throws Exception{
        List<Integer> id=new ArrayList<>();
        for(Message m:messageIdToMessageDb.values()){
            if(m.getTimestamp().toInstant().isAfter(start.toInstant()) &&
                    m.getTimestamp().toInstant().isBefore(end.toInstant())){

                id.add(m.getId());

            }
        }
        if(id.size()<k){
            throw new Exception("K is greater than the number of messages");
        }
        Collections.sort(id,Collections.reverseOrder());
        return messageIdToMessageDb.get(id.get(k-1)).getContent();
    }
}
