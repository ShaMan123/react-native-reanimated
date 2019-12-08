package com.swmansion.reanimated.nodes;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.JSApplicationCausedNativeException;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.swmansion.reanimated.NodesManager;

public class FunctionNode extends Node implements ValueManagingNode, RCTEventEmitter {

  private final int mWhatNodeID;

  public FunctionNode(int nodeID, ReadableMap config, NodesManager nodesManager) {
    super(nodeID, config, nodesManager);
    mWhatNodeID = config.getInt("what");
  }

  @Override
  protected Object evaluate() {
    Node what = mNodesManager.findNodeById(mWhatNodeID, Node.class);
    return what.value();
  }

  @Override
  public void setValue(Object value) {
    Node what = mNodesManager.findNodeById(mWhatNodeID, Node.class);
    try {
      ((ValueManagingNode) what).setValue(value);
    } catch (Throwable throwable) {
      throw new JSApplicationCausedNativeException(
              "Error while trying to set value on reanimated " + what.getClass().getSimpleName(), throwable);
    }
  }

  @Override
  public void receiveEvent(int targetTag, String eventName, @Nullable WritableMap event) {
    Node whatNode = mNodesManager.findNodeById(mWhatNodeID, Node.class);
    ((RCTEventEmitter) whatNode).receiveEvent(targetTag, eventName, event);
  }

  @Override
  public void receiveTouches(String eventName, WritableArray touches, WritableArray changedIndices) {
    Node whatNode = mNodesManager.findNodeById(mWhatNodeID, Node.class);
    ((RCTEventEmitter) whatNode).receiveTouches(eventName, touches, changedIndices);
  }

  @Override
  public Object exportableValue() {
    Node what = mNodesManager.findNodeById(mWhatNodeID, Node.class);
    return what.exportableValue();
  }
}
