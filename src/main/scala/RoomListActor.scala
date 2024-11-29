package fr.cytech.icc

import org.apache.pekko.actor.typed.{ ActorRef, Behavior }
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.Actor.emptyBehavior

enum RoomListMessage {
  case CreateRoom(name: String)
  case GetRoom(name: String, replyTo: ActorRef[Option[ActorRef[Message]]])
  case ListRooms(replyTo: ActorRef[Map[String, ActorRef[Message]]])
}

object RoomListActor {

  import RoomListMessage.*

  def apply(rooms: Map[String, ActorRef[Message]] = Map.empty): Behavior[RoomListMessage] = {
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case CreateRoom(name)       =>  
          //Petit pensement pour pas tout faire exploser si le nom est déjà pris
          if(rooms.get(name).isEmpty){
          val actor = context.spawn(RoomActor(name), name)
          val nouvelleRoom = (name,actor)
          apply(rooms + nouvelleRoom)
          }else{
            apply(rooms)
          }
          

        case GetRoom(name, replyTo) => 
          replyTo ! rooms.get(name)
          Behaviors.same

        case ListRooms(replyTo)=>
          replyTo ! rooms
          Behaviors.same
      }
    }
  }
}
