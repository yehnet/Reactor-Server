package bgu.spl.net.impl.bidi;


import bgu.spl.net.impl.bidi.Messages.*;

import java.util.Map;

public class BidiMessageProtocolImpl implements BidiMessagingProtocol<Message> {

    //========================================================Fields===========================================================//

    private boolean shouldTerminate;
    private ConnectionsImpl<Message> connectionsImpl;
    private int connectionId;
    public BGSDataBase dataBase;

    //========================================================Constructor===========================================================//

    public BidiMessageProtocolImpl(BGSDataBase dataBase){
        this.dataBase=dataBase;
    }

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connectionsImpl= (ConnectionsImpl<Message>) connections;
        this.connectionId=connectionId;
        this.shouldTerminate=false;
    }

    //========================================================Methods===========================================================//


    //status means if the message is ACK or ERROR or NOTIFICATION
    @Override
    public void process(Message message) {
        if (message instanceof  RegisterMessage){
            int status = message.execute(connectionId , dataBase);
            if( status == 0){
                ErrorMessage errorMessage=new ErrorMessage((short) ((RegisterMessage) message).getOpCode());
                connectionsImpl.send(connectionId, errorMessage);
            }else if(status == 1){
                AckMessage ackMessage=new AckMessage(message , (short) ((RegisterMessage) message).getOpCode());
                connectionsImpl.send(connectionId , ackMessage);
            }

        }else if(message instanceof LoginMessage){
            int status = message.execute(connectionId ,dataBase);
            if(status == 0){
                ErrorMessage errorMessage = new ErrorMessage((short) ((LoginMessage) message).getOpCode());
                connectionsImpl.send(connectionId , errorMessage);
            }else if(status == 1){
                AckMessage ackMessage = new AckMessage(message , (short) ((LoginMessage) message).getOpCode());
                connectionsImpl.send(connectionId , ackMessage);

                //check if the users has posts to get when he re-logged in
                BGSuser d = dataBase.loginUsers.get(connectionId);
                synchronized (d.getPosts()) {
                    for (NotificationMessage notificationMessage : d.getPosts()) {
                        connectionsImpl.send(connectionId, notificationMessage);
                    }
                    for (NotificationMessage e : d.getPosts()) {
                        d.getPosts().remove(e);
                    }
                }
            }

        }else if(message instanceof LogoutMessage){
            int status = message.execute(connectionId ,dataBase);
            if(status == 0){
                ErrorMessage errorMessage = new ErrorMessage((short) ((LogoutMessage) message).getOpCode());
                connectionsImpl.send(connectionId , errorMessage);
            }else if(status == 1){
                AckMessage ackMessage = new AckMessage(message , (short) ((LogoutMessage) message).getOpCode());
                connectionsImpl.send(connectionId , ackMessage);
                shouldTerminate=true;
            }

        }else if(message instanceof FollowMessage){
            int status = message.execute(connectionId ,dataBase);
            if(status == 0){
                ErrorMessage errorMessage = new ErrorMessage((short) ((FollowMessage) message).getOpCode());
                connectionsImpl.send(connectionId , errorMessage);
            }else if(status == 1){
                AckMessage ackMessage = new AckMessage(message , (short) ((FollowMessage) message).getOpCode());
                connectionsImpl.send(connectionId , ackMessage);
            }

        }else if(message instanceof PostMessage){
            int status = message.execute(connectionId ,dataBase);
            if(status == 0){
                ErrorMessage errorMessage = new ErrorMessage((short) ((PostMessage) message).getOpCode());
                connectionsImpl.send(connectionId , errorMessage);
            }else if(status == 1){
                AckMessage ackMessage = new AckMessage(message , (short) ((PostMessage) message).getOpCode());
                connectionsImpl.send(connectionId , ackMessage);

                NotificationMessage notificationMessage = new NotificationMessage(message);
                BGSuser user = dataBase.loginUsers.get(connectionId);
                dataBase.postMessagesPerUser.get(user).add(((PostMessage) message).getAllMessage());

                boolean flag= false;
                boolean flag1= false;


                //post the message to all that was tagged in the message
                for(String s : ((PostMessage) message).getTheUsers()) {
                    s = s.substring(1);
                    for (Map.Entry<Integer, BGSuser> tmp : dataBase.loginUsers.entrySet()) {
                        if (tmp.getValue().getName().equals(s) && !s.equals(((PostMessage) message).getUsername())  && !user.followers.contains(s)) {
                            flag1 = true;
                            connectionsImpl.send(tmp.getKey(), notificationMessage);
                        }
                    }
                    if (!flag1) {
                        //that means that the followers is already registered
                        synchronized (user.followers) {
                            for (BGSuser g : dataBase.users) {
                                if (g.getName().equals(s) && !user.followers.contains(s))
                                    g.getPosts().add(notificationMessage);
                            }
                        }
                    }
                }

                //post the message to all users that follow the user
                synchronized (user.followers) {
                    for (String follower : user.followers) {
                        for (Map.Entry<Integer, BGSuser> tmp : dataBase.loginUsers.entrySet()) {
                            if (tmp.getValue().getName().equals(follower)) {
                                flag = true;
                                connectionsImpl.send(tmp.getKey(), notificationMessage);
                            }
                        }
                        if (!flag) {
                            //that means that the followers is already registered
                            for (BGSuser g : dataBase.users) {
                                if (g.getName().equals(follower))
                                    g.getPosts().add(notificationMessage);
                            }
                        }
                    }
                }

            }

        }else if(message instanceof PMMessage){
            int status = message.execute(connectionId ,dataBase);
            if(status == 0){
                ErrorMessage errorMessage = new ErrorMessage((short) ((PMMessage) message).getOpCode());
                connectionsImpl.send(connectionId , errorMessage);
            }else if(status == 1) {
                AckMessage ackMessage = new AckMessage(message, (short) ((PMMessage) message).getOpCode());
                connectionsImpl.send(connectionId, ackMessage);

                BGSuser user = dataBase.loginUsers.get(connectionId);
                dataBase.postMessagesPerUser.get(user).add(((PMMessage) message).getAllMessage());
                NotificationMessage notificationMessage = new NotificationMessage(message);

                //post the message to the user he want to pm to
                for (BGSuser g : dataBase.users) {
                    if (g.getName().equals(((PMMessage) message).getUserName())) {
                        if (dataBase.loginUsers.containsKey(g.getConnectionID())) {
                            connectionsImpl.send(g.getConnectionID(), notificationMessage);
                        }
                        else{
                            synchronized (g.getPosts()) {
                                g.getPosts().add(notificationMessage);
                            }
                        }
                    }
                }

            }

        }else if(message instanceof UserListMessage){
            int status = message.execute(connectionId ,dataBase);
            if(status == 0){
                ErrorMessage errorMessage = new ErrorMessage((short) ((UserListMessage) message).getOpCode());
                connectionsImpl.send(connectionId , errorMessage);
            }else if(status == 1){
                AckMessage ackMessage = new AckMessage(message , (short) ((UserListMessage) message).getOpCode());
                connectionsImpl.send(connectionId , ackMessage);
            }

        }else if(message instanceof StatMessage){
            int status = message.execute(connectionId ,dataBase);
            if(status == 0){
                ErrorMessage errorMessage = new ErrorMessage((short) ((StatMessage) message).getOpCode());
                connectionsImpl.send(connectionId , errorMessage);
            }else if(status == 1){
                AckMessage ackMessage = new AckMessage(message , (short) ((StatMessage) message).getOpCode());
                connectionsImpl.send(connectionId , ackMessage);
            }

        }

    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
