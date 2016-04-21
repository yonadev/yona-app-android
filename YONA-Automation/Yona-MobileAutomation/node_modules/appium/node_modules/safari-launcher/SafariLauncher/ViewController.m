//
//  ViewController.m
//  SafariLauncher
//
//  Created by Budhaditya Das on 6/5/13.
//  Copyright (c) 2013 Bytearc. All rights reserved.
//

#import "ViewController.h"
#import "Preferences.h"
#import "SafariLauncher.h"
#import "ViewController.h"
#import "QuartzCore/QuartzCore.h"

@interface ViewController ()

@end

@implementation ViewController
@synthesize delayTimer;

NSUInteger secondsLeft;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    Preferences *preferences = [Preferences sharedInstance];
    // Do any additional setup after loading the view.
    int fontSize = self.view.bounds.size.height/25;
    
    NSLog(@"  height - %02f : width - %02f", self.view.bounds.size.height, self.view.bounds.size.width);
    titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(self.view.bounds.size.width/4.0f,
                                                      10,
                                                      self.view.bounds.size.width,
                                                      50)]; 
    titleLabel.text = @"Safari Launcher";
    titleLabel.textColor = [UIColor blueColor];
    titleLabel.font = [UIFont fontWithName:@"Verdana" size:fontSize];
    
    launchButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [launchButton addTarget:self action:@selector(launchSafari) forControlEvents:UIControlEventTouchUpInside];
    [launchButton setTitle:@"Launch Safari" forState:UIControlStateNormal];
    [launchButton setIsAccessibilityElement:YES];
    [launchButton setAccessibilityLabel:@"launch safari"];
    
    CGRect bounds = self.view.bounds;
    launchButton.frame = CGRectMake(80.0, 210.0, 200.0, 60.0);
    launchButton.center = CGPointMake(CGRectGetMidX(bounds), CGRectGetMidY(bounds));
    
    infoLabel = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                      self.view.bounds.size.height-50.0f,
                                                      self.view.bounds.size.width,
                                                      50)];
    infoLabel.text = [NSString stringWithFormat:@"   url: %@", preferences.launchUrl];
    infoLabel.layer.borderColor = [UIColor blackColor].CGColor;
    infoLabel.layer.borderWidth = 3.0;
    
    [self.view addSubview:titleLabel];
    [self.view addSubview:launchButton];
    [self.view addSubview:infoLabel];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    titleLabel = nil;
    infoLabel = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

-(void) launchSafari{
    Preferences *preferences = [Preferences sharedInstance];
    NSArray * args = [[NSProcessInfo processInfo] arguments];
    NSString *urlArg = preferences.launchUrl;
    if([args count] > 1) {
        urlArg = [args objectAtIndex: 1];
    }
    infoLabel.text = [NSString stringWithFormat:@"  Launching: %@", urlArg];
    [SafariLauncher launch:urlArg];
}

@end
