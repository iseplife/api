package com.iseplife.api.websocket.packets;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;

public class NetHelper {
  public static void writeString(ByteBuf buf, String str) throws IOException {
    byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
    buf.writeShort(bytes.length);
    buf.writeBytes(bytes);
  }
  
  public static String readString(ByteBuf stream) throws IOException {
    byte[] bytes = new byte[stream.readUnsignedShort()];
    stream.readBytes(bytes);
    return new String(bytes, StandardCharsets.UTF_8);
  }
}
