package com.iseplife.api.websocket.packets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.iseplife.api.websocket.packets.client.WSPCKeepAlive;
import com.iseplife.api.websocket.packets.server.WSPSBadToken;
import com.iseplife.api.websocket.packets.server.WSPSConnected;
import com.iseplife.api.websocket.packets.server.WSPSEventCreated;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostCommentsUpdate;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostCreated;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostEdited;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostLikesUpdate;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostOwnLikeUpdate;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostPollChoiceUpdate;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostRemoved;
import com.iseplife.api.websocket.packets.server.WSPSGroupJoined;
import com.iseplife.api.websocket.packets.server.WSPSGroupLeft;
import com.iseplife.api.websocket.packets.server.WSPSNotificationRecieved;

import io.netty.buffer.ByteBuf;

@Service
public class WSProtocol {
  
  private static WSProtocol instance = new WSProtocol().init();
  
  private List<Class<? extends WSPacketIn>> packetIn = new CopyOnWriteArrayList<>();
  private Map<Class<? extends WSPacketOut>, Integer> packetOut = new ConcurrentHashMap<>();

  private int registerPacketServer(Class<? extends WSPacketOut> packetClass) {
      this.packetOut.put(packetClass, this.packetOut.size());
      return this.packetOut.size() - 1;
  }

  private int registerPacketClient(Class<? extends WSPacketIn> packetClass) {
    this.packetIn.add(packetClass);
    return this.packetIn.size() - 1;
  }

  public WSProtocol init() {
    //Server 
    registerPacketServer(WSPSConnected.class);
    registerPacketServer(WSPSFeedPostCreated.class);
    registerPacketServer(WSPSNotificationRecieved.class);
    registerPacketServer(WSPSEventCreated.class);
    registerPacketServer(WSPSGroupJoined.class);
    registerPacketServer(WSPSGroupLeft.class);
    registerPacketServer(WSPSFeedPostRemoved.class);
    registerPacketServer(WSPSFeedPostEdited.class);
    registerPacketServer(WSPSFeedPostLikesUpdate.class);
    registerPacketServer(WSPSBadToken.class);
    registerPacketServer(WSPSFeedPostCommentsUpdate.class);
    registerPacketServer(WSPSFeedPostPollChoiceUpdate.class);
    registerPacketServer(WSPSFeedPostOwnLikeUpdate.class);
    
    //Client
    registerPacketClient(WSPCKeepAlive.class);
    return this;
  }
  
  @SuppressWarnings("deprecation")
  public WSPacketIn readPacket(ByteBuf buf) throws InstantiationException, IllegalAccessException, IOException{
    int id = buf.readUnsignedByte();
    Class<? extends WSPacketIn> packetClass = packetIn.get(id);
    
    WSPacketIn object = packetClass.newInstance();
    object.read(buf);
    
    return object;
  }
  
  public void writePacket(ByteBuf buf, WSPacketOut packet) throws IOException {
    buf.writeByte(packetOut.get(packet.getClass()));
    packet.write(buf);
  }
  
  public static WSProtocol getInstance() {
    return instance;
  }
}
