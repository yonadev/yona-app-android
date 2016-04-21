//
//  Preferences.m
//  SafariLauncher
//
//  Created by Budhaditya Das on 6/10/13.
//  Copyright (c) 2013 Bytearc. All rights reserved.
//

#import "Preferences.h"

static NSString * const PREF_LAUNCH_URL = @"preference_launchUrl";
static NSString * const DEFAULT_URL = @"http://www.google.com";

@implementation Preferences

@synthesize launchUrl = launchUrl_;

/*
static Preferences *singleton = nil;
 + (Preferences*) sharedInstance {
    if (singleton == nil) {
        singleton = [[Preferences alloc] init];
    }
    return singleton;
}
*/
+ (Preferences *)sharedInstance
{
    //  Static local predicate must be initialized to 0
    static Preferences *sharedInstance = nil;
    static dispatch_once_t onceToken = 0;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[Preferences alloc] init];
    });
    return sharedInstance;
}

+ (void) initPreferences {
    NSUserDefaults* userDefaults = [NSUserDefaults standardUserDefaults];
    id launchUrl = [userDefaults objectForKey:PREF_LAUNCH_URL];
    
    if (launchUrl == nil) {
        NSLog(@"App setting not found. Initializing app settings to default values.");
        
        NSString* bundlePath = [[NSBundle mainBundle] bundlePath];
        NSString* settingsPath = [bundlePath stringByAppendingPathComponent:
                                  @"Settings.bundle"];
        NSString* rootPlist = [settingsPath stringByAppendingPathComponent:
                               @"Root.plist"];
        
        NSDictionary* settings = [NSDictionary dictionaryWithContentsOfFile:
                                  rootPlist];
        NSArray* preferences = [settings objectForKey:@"PreferenceSpecifiers"];
        
        NSMutableDictionary* defaultPrefs =
        [NSMutableDictionary dictionaryWithCapacity:[preferences count]];
        for (NSDictionary* item in preferences) {
            id key = [item objectForKey:@"Key"];
            if (key != nil) {
                [defaultPrefs setObject:[item objectForKey:@"DefaultValue"]
                                 forKey:key];
            }
        }
        
        [[NSUserDefaults standardUserDefaults] registerDefaults:defaultPrefs];
        [[NSUserDefaults standardUserDefaults] synchronize];
    } else {
        NSLog(@"App settings already initialized");
    }
}


- (id)init {
    [Preferences initPreferences];
    
    // Fetching paramters from [NSUserDefaults standardUserDefaults].
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
	
    launchUrl_ = [defaults stringForKey:PREF_LAUNCH_URL];
    if (!launchUrl_) {
        launchUrl_ = [[Preferences getDefaultUrl] absoluteString];
        [defaults setObject:launchUrl_ forKey:PREF_LAUNCH_URL];
    }
    
    [defaults synchronize];
    return self;
}

+ (NSURL *) getDefaultUrl{
    return [NSURL URLWithString:DEFAULT_URL];
}
@end

