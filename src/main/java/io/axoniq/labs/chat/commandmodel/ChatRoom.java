package io.axoniq.labs.chat.commandmodel;

import io.axoniq.labs.chat.coreapi.CreateRoomCommand;
import io.axoniq.labs.chat.coreapi.JoinRoomCommand;
import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.RoomCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;

import java.util.*;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

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

}
