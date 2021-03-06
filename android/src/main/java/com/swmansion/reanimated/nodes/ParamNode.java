package com.swmansion.reanimated.nodes;

import com.facebook.react.bridge.JSApplicationCausedNativeException;
import com.facebook.react.bridge.ReadableMap;
import com.swmansion.reanimated.NodesManager;

import java.util.EmptyStackException;
import java.util.Stack;

public class ParamNode extends ValueNode {

  private final Stack<Integer> mArgsStack;
  private String mPrevCallID;

  public ParamNode(int nodeID, ReadableMap config, NodesManager nodesManager) {
    super(nodeID, config, nodesManager);
    mArgsStack = new Stack<>();
  }

  @Override
  public void setValue(Object value) {
    try {
      Node node = mNodesManager.findNodeById(mArgsStack.peek(), Node.class);
      String callID = mUpdateContext.callID;
      mUpdateContext.callID = mPrevCallID;
      ((ValueManagingNode) node).setValue(value);
      mUpdateContext.callID = callID;
    } catch (EmptyStackException e) {
      throwNoContext(e);
    }
  }

  public void beginContext(Integer ref, String prevCallID) {
    mPrevCallID = prevCallID;
    mArgsStack.push(ref);
  }

  public void endContext() {
    mArgsStack.pop();
  }

  @Override
  protected Object evaluate() {
    try {
      String callID = mUpdateContext.callID;
      mUpdateContext.callID = mPrevCallID;
      Node node = mNodesManager.findNodeById(mArgsStack.peek(), Node.class);
      Object val = node.value();
      mUpdateContext.callID = callID;
      return val;
    } catch (EmptyStackException e) {
      throwNoContext(e);
      return null;
    }
  }

  private void throwNoContext(Throwable throwable) throws JSApplicationCausedNativeException {
    throwable.printStackTrace();
    throw new JSApplicationCausedNativeException(getClass().getSimpleName() + " is trying to evaluate with no context.\n" +
            "This happens when using value setting nodes (e.g `callback`) inside `proc`. This is not yet supported.",
            throwable);
  }

  public void start() {
    Node node = mNodesManager.findNodeById(mArgsStack.peek(), Node.class);
    if (node instanceof ParamNode) {
      ((ParamNode) node).start();
    } else {
      ((ClockNode) node).start();
    }
  }

  public void stop() {
    Node node = mNodesManager.findNodeById(mArgsStack.peek(), Node.class);
    if (node instanceof ParamNode) {
      ((ParamNode) node).stop();
    } else {
      ((ClockNode) node).stop();
    }
  }

  public boolean isRunning() {
    Node node = mNodesManager.findNodeById(mArgsStack.peek(), Node.class);
    if (node instanceof ParamNode) {
      return  ((ParamNode) node).isRunning();
    }
    return ((ClockNode) node).isRunning;
  }
}


