package com.swmansion.reanimated.nodes;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.swmansion.reanimated.NodesManager;

import java.util.ArrayList;

public class EventNode extends MapNode implements RCTEventEmitter {

  private ArrayList<CallFuncNode> mContext;

  public EventNode(int nodeID, ReadableMap config, NodesManager nodesManager) {
    super(nodeID, config, nodesManager);
  }

  @Override
  public void receiveEvent(int targetTag, String eventName, @Nullable WritableMap event) {
    if (event == null) {
      throw new JSApplicationIllegalArgumentException("Animated events must have event data.");
    }

    setValue(event);
  }

  @Override
  public void receiveTouches(String eventName, WritableArray touches, WritableArray changedIndices) {
    throw new RuntimeException("receiveTouches is not support by animated events");
  }

  @Override
  protected void propagateContext(ArrayList<CallFuncNode> context) {
    mContext = context;
    super.propagateContext(context);
  }

  @Nullable
  @Override
  protected Object evaluate() {
    if (mContext != null) {
      ContextualEventEmitter eventHandler = new ContextualEventEmitter(mContext);
      mContext = null;
      return eventHandler;
    } else {
      return this;
    }
  }

  @Override
  public Object exportableValue() {
    return ZERO;
  }

  private class ContextualEventEmitter implements RCTEventEmitter {
    private ArrayList<CallFuncNode> mContext;
    ContextualEventEmitter(ArrayList<CallFuncNode> context) {
      mContext = context;
    }

    @Override
    public void receiveEvent(int targetTag, String eventName, @Nullable WritableMap event) {
      ValueManagingNode provider = new ContextProvider.ValueManager(EventNode.this, mContext);
      provider.setValue(event);
    }

    @Override
    public void receiveTouches(String eventName, WritableArray touches, WritableArray changedIndices) {
      EventNode.this.receiveTouches(eventName, touches, changedIndices);
    }
  }
}
