//
//  OAuthViewController.m
//  Authentication Sample
//
//  Created by Ryan Latta on 5/14/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "OAuthViewController.h"
#import "SBJson.h"


@implementation OAuthViewController
@synthesize web;
@synthesize code;

+(BOOL) isAuthenticated {
    NSString *token = [[NSUserDefaults standardUserDefaults] stringForKey:@"token"];
    if([token length] > 0) {
        return YES;
    }
    return NO;
}

+(NSString *) getToken {
    return [[NSUserDefaults standardUserDefaults] stringForKey:@"token"];
}
- (id) initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    return [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
}
//Where you programatically create your views.
- (void) loadView {
    CGRect frame = [[[[UIApplication sharedApplication] delegate] window] frame];
    self.web = [[[UIWebView alloc] initWithFrame:CGRectMake(frame.origin.x, frame.origin.y, frame.size.width, frame.size.height)] autorelease];
    self.view = web;
    self.web.delegate = self;
}

- (void) viewDidLoad {
    [self.web loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"http://local.slidev.org:8080/api/oauth/authorize?redirect_uri=https://localhost&client_id=EGbI4LaLaL"]]];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void) dealloc {
    [web release];
    [code release];
    [super dealloc];
}

- (void) getOauthToken {
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://local.slidev.org:8080/api/oauth/token?client_id=EGbI4LaLaL&client_secret=iGdeAGCugi4VwZNtMJR062nNKjB7gRKUjSB0AcZqpn8Beeee&code=%@&redirect_uri=NONCE", self.code]];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    NSLog(@"Got a response of %@", [request responseString]);
    NSDictionary *token = [[request responseString] JSONValue];
    [[NSUserDefaults standardUserDefaults] setObject:[token objectForKey:@"access_token"] forKey:@"token"];
    [self.navigationController dismissModalViewControllerAnimated:YES];
}

/**
 * UIWebView Delegate
 */

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    return YES;
    
}
- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    NSLog(@"didFailWithError %@", [error localizedFailureReason]);
    [self extractCode:[[[error userInfo] valueForKey:@"NSErrorFailingURLKey"] query]];
    [webView stopLoading];
    [self getOauthToken];
}

-(void) extractCode: (NSString *) fromUrl {
    NSMutableDictionary *parameters = [[NSMutableDictionary alloc] init];
    NSArray *kv = [fromUrl componentsSeparatedByString:@"="];
    if([kv count] > 0) {
        [parameters setObject:[kv objectAtIndex:1] forKey:[kv objectAtIndex:0]];
    }
             
    self.code = [parameters objectForKey:@"code"];
    [parameters release];
    NSLog(@"We have a code: %@", code);
}
@end
