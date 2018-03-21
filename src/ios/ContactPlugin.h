//
//  TestPlugin.h
//  testCordova
//
//  Created by mr、j on 2018/3/15.
//

#import <Cordova/CDVPlugin.h>

@interface ContactPlugin : CDVPlugin

-(void) getContactOrAppList:(CDVInvokedUrlCommand *)command;

@end
