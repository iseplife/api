package com.iseplife.api.websocket.handler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.iseplife.api.websocket.packets.WSPacketIn;

@Service
public class PacketHandler {
  Map<Class<? extends WSPacketIn>, List<Handled>> handlers = new ConcurrentHashMap<>();
  public void registerListener(Object listener) {
    for(Method m : listener.getClass().getDeclaredMethods()) {
      if(m.isAnnotationPresent(PacketListener.class)) {
        PacketListener annotation = m.getAnnotation(PacketListener.class);
        Class<?>[] parameters = m.getParameterTypes();
        if (parameters.length == 2 && WSPacketIn.class.isAssignableFrom(parameters[0]) && parameters[1] == WebSocketSession.class)
        {
            handlers.computeIfAbsent(annotation.clazz(), a -> new CopyOnWriteArrayList<Handled>());
            handlers.get(annotation.clazz()).add(new Handled(m, listener));
        }
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
