package com.iseplife.api.websocket.handler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.socket.WebSocketSession;

import com.iseplife.api.websocket.packets.WSPacketIn;

public class PacketHandler {
  Map<Class<? extends WSPacketIn>, List<Handled>> handlers = new ConcurrentHashMap<>();
  public void registerListener(Object listener) {
    for(Method m : listener.getClass().getDeclaredMethods()) {
      if(m.isAnnotationPresent(PacketListener.class)) {
        PacketListener annotation = m.getAnnotation(PacketListener.class);
        
        System.out.println("Loaded packetlistener : "+m+" ("+annotation.clazz()+")");
        
        handlers.computeIfAbsent(annotation.clazz(), a -> new CopyOnWriteArrayList<Handled>());
        handlers.get(annotation.clazz()).add(new Handled(m, listener));
      }
    }
  }
  
  public void fire(WSPacketIn packet, WebSocketSession session) {
    List<Handled> handleds = handlers.get(packet.getClass());
    if(handleds != null)
      for(Handled handled : handleds) {
        try {
          handled.getMethod().invoke(handled.getHandled(), packet, session);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          new IOException("An error occured when handling packet "+packet+" in "+handled.getMethod(), e).printStackTrace();
        }
      }
  }

  private static class Handled
  {
      protected Method method;
      protected Object handled;
      public Handled(Method method, Object handled)
      {
          this.handled = handled;
          this.method = method;
      }
      public Object getHandled()
      {
          return handled;
      }
      public Method getMethod()
      {
          return method;
      }
  }
}
