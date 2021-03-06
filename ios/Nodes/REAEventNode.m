#import "REAEventNode.h"
#import "REANodesManager.h"
#import "REAValueNode.h"

@implementation REAEventNode

- (instancetype)initWithID:(REANodeID)nodeID config:(NSDictionary<NSString *,id> *)config
{
    self = [super initWithID:nodeID config:config];
  return self;
}

- (void)processEvent:(id<RCTEvent>)event
{
  // argMapping is an array of eventPaths, each even path ends with a target node ID
    // Supported events args are in the following order: viewTag, eventName, eventData.
    NSArray *eventData = event.arguments[2];
    [self setValue:eventData];
}

-(id)evaluate
{
    return @(0);
}

@end
