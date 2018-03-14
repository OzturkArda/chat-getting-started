package io.axoniq.labs.chat.commandmodel;

import io.axoniq.labs.chat.coreapi.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.*;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class ChatRoom {

    @AggregateIdentifier
    private String roomId;
    private String name;
    private Set<String> participants;;

    public ChatRoom(){}

    @CommandHandler
    public ChatRoom(CreateRoomCommand command){
        apply(new RoomCreatedEvent(command.getRoomId(), command.getName()));
    }

    @CommandHandler
    public void handle(JoinRoomCommand command){
        if(!participants.contains(command.getParticipant())){
            apply(new ParticipantJoinedRoomEvent(command.getParticipant(), command.getRoomId()));
        }
    }

    @CommandHandler
    public void handle(LeaveRoomCommand command){
        if(participants.contains(command.getParticipant())){
            apply(new ParticipantLeftRoomEvent(command.getParticipant(), command.getRoomId()));
        }
    }

    @CommandHandler
    public void handle(PostMessageCommand command) {
        if(participants.contains(command.getParticipant())){
            apply(new MessagePostedEvent(command.getParticipant(), roomId, command.getMessage()));
        }
    }

    @EventSourcingHandler
    public void on(RoomCreatedEvent event){
        this.roomId = event.getRoomId();
        this.name = event.getName();
        participants = new HashSet<>();
    }

    @EventSourcingHandler
    protected void on(ParticipantJoinedRoomEvent event){
        this.participants.add(event.getParticipant());
    }

    @EventSourcingHandler
    protected void on(ParticipantLeftRoomEvent event){
        this.participants.remove(event.getParticipant());
    }

}
