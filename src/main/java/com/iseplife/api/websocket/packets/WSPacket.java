package com.iseplife.api.websocket.packets;

public abstract class WSPacket {
  static protected int MAX_HASH = 255;  // uint_8 max value

  // In case of edits make to sure to also update the algorithm on API side
  @Override
  public int hashCode(){
    String key = this.getClass().getName();
    int preHash = 0;
    for(int i = 0; i < key.length(); i++) {
      preHash += Math.pow(31, i) * key.charAt(i);
    }
    return preHash % MAX_HASH;
  }
}
