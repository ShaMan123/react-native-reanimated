import React, { useMemo, useRef, useCallback } from 'react';
import { Image, StyleSheet, ScrollView as RNScrollView } from 'react-native';
import Animated from 'react-native-reanimated';
import { State, NativeViewGestureHandler, PanGestureHandler } from 'react-native-gesture-handler';

const { Value, event, dispatch, useCode, createAnimatedComponent, block, cond, not, and, divide, acc, eq, set, View, or, debug, add, call } = Animated;
const ScrollView = createAnimatedComponent(RNScrollView);

export default function SyncedScrollViews() {
  //const [handleA, setHandleA] = React.useState();
  //const [handleB, setHandleB] = React.useState();
  const scrollX = useMemo(() => new Value(0), []);
  const scrollY = useMemo(() => new Value(0), []);
  const effect = useMemo(() => new Value(0), []);
  const state = useMemo(() => debug('scroll state', new Value(0)), []);

  const scrollToA = useMemo(() => {
    const d = dispatch('RCTScrollView', 'scrollTo', scrollX, scrollY, 0);
    d.__attach();
    return d;
  }, [scrollX, scrollY]);
  const scrollToB = useMemo(() => {
    const d = dispatch('RCTScrollView', 'scrollTo', scrollX, scrollY, 0);
    d.__attach();
    return d;
  }, [scrollX, scrollY]);

  const panRef = useRef();
  const scrollARef = useRef();
  const scrollBRef = useRef();

  const beginDragA = useMemo(() =>
    event([{
      nativeEvent: ({ contentOffset: { x, y } }) => block([
        set(state, 1),
        set(scrollX, x),
        set(scrollY, y)
      ])
    }]),
    [state]
  );

  const beginDragB = useMemo(() =>
    event([{
      nativeEvent: ({ contentOffset: { x, y } }) => block([
        set(state, 2),
        set(scrollX, x),
        set(scrollY, y)
      ])
    }]),
    [state]
  );

  const holder = useMemo(() => debug('eval', new Value(0)), []);

  const effectEvent = useMemo(() =>
    event([{
      //nativeEvent: ({ contentOffset: { x } }) => set(holder, divide(add(x, 1), add(x, 1)))
      nativeEvent: () => set(holder, 1)
    }]),
    [holder]
  );

  useCode(() =>
    set(effect, acc(holder)),
    [effect, holder]
  );

  const onScrollA = useMemo(() =>
    event([{
      nativeEvent: ({ contentOffset: { x, y } }) =>
        cond(
          or(eq(state, 1), eq(state, 0)),
          [
            set(scrollX, x),
            set(scrollY, y)
          ]
        )
    }]),
    [state]
  );

  const onScrollB = useMemo(() =>
    event([{
      nativeEvent: ({ contentOffset: { x, y } }) =>
        cond(
          or(eq(state, 2), eq(state, 0)),
          [
            set(scrollX, x),
            set(scrollY, y),
          ]
        )
    }]),
    [state]
  );

  const otherScrollY = useMemo(() => new Value(0), []);
  const otherOnScroll = useMemo(() => event([{ nativeEvent: { contentOffset: ({ x, y }) => set(debug('assert nested proxy', otherScrollY), y) } }]), [otherScrollY]);

  useCode(() =>
    block([
      scrollToA,
      scrollToB,
      call([effect], console.log)
    ]),
    [scrollToA, scrollToB]
  );

  const __scrollX = useMemo(() => new Value(0), []);
  const __scrollY = useMemo(() => new Value(0), []);
  const attachedEvent = useMemo(() => event([{ nativeEvent: { contentOffset: { x: __scrollX, y: __scrollY } } }]), [scrollX, scrollY]);

  useCode(() =>
    call([__scrollX, __scrollY], v => console.log('logging second attached event values', v)),
    [__scrollX, __scrollY]
  );

  const onScrollFunc = useCallback((e) =>
    console.log('logging additional `onScroll` function prop', e.nativeEvent.contentOffset),
    []
  );

  const baseScrollComponent = (
    <ScrollView
      style={styles.scrollView}
      collapsable={false}
      scrollEventThrottle={1}
      simultaneousHandlers={panRef}
      disableScrollViewPanResponder
      decelerationRate='normal'
    >
      <Image source={require('../imageViewer/grid.png')} collapsable={false} />
    </ScrollView>
  );

  const scrollerA = onScrollA//[onScrollA, onScrollFunc, attachedEvent];
  const scrollerB = onScrollB//[onScrollB, onScrollFunc, attachedEvent];

  return (
    <PanGestureHandler
      ref={panRef}
      simultaneousHandlers={[scrollARef, scrollBRef]}
      enabled={false}
    >
      <View style={styles.default} collapsable={false}>
        {React.cloneElement(baseScrollComponent, {
          ref: (ref) => {
            scrollToA.setNativeView(ref);
            scrollARef.current = ref;
          },
          onScroll: scrollerA,
          onScrollBeginDrag: beginDragA,
          onMomentumScrollBegin: beginDragA,
          onMomentumScrollEnd: scrollerA,
          onScrollEndDrag: effectEvent
        })}
        {React.cloneElement(baseScrollComponent, {
          ref: (ref) => {
            scrollToB.setNativeView(ref);
            scrollBRef.current = ref;
          },
          onScroll: scrollerB,
          onScrollBeginDrag: beginDragB,
          onMomentumScrollBegin: beginDragB,
          onMomentumScrollEnd: scrollerB,
          onScrollEndDrag: otherOnScroll
        })}
      </View>
    </PanGestureHandler>
  );
}

const styles = StyleSheet.create({
  scrollView: {
    flex: 1,
    borderColor: 'blue',
    borderWidth: 2,
    margin: 5
  },
  default: {
    flex: 1,
  }
})