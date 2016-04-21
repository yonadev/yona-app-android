//
//  SafariLauncher.m
//  SafariLauncher
//
//  Created by Budhaditya Das on 6/5/13.
//  Copyright (c) 2013 Bytearc. All rights reserved.
//

#import "SafariLauncher.h"
#import "Preferences.h"

@implementation SafariLauncher

+ (void)launch:(NSString *)url {
    Preferences *preferences = [Preferences sharedInstance];
    
    NSURL *launchUrl = [NSURL URLWithString: url];
    
    if(launchUrl == nil || (![[launchUrl scheme] isEqualToString:@"http"] && ![[launchUrl scheme] isEqualToString:@"https"])) {
        NSLog(@"Invalid URL [%@] specified. Trying settings URL", url);

        launchUrl = [NSURL URLWithString: preferences.launchUrl];
        
        if(launchUrl == nil || (![[launchUrl scheme] isEqualToString:@"http"] && ![[launchUrl scheme] isEqualToString:@"https"])) {
            NSLog(@"Invalid settings URL [%@]. Launching default URL", preferences.launchUrl);
            launchUrl = [Preferences getDefaultUrl]; 
        }
    }
    NSLog(@"Launching URL - %@", launchUrl);
    [[UIApplication sharedApplication] openURL:launchUrl];
    
}

@end
