// Copyright 2012-2013 inBloom, Inc. and its affiliates.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
// http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#import "OAuthViewController.h"
#import "SBJson.h"
#import "AppDelegate.h"


@implementation OAuthViewController 
{
    AppDelegate *delegate;
    NSBundle *mainBundle;
}
@synthesize web;
@synthesize code;

+(BOOL) isAuthenticated {
    NSString *token = [self getToken];
    if(token != nil && [token length] > 0) {
        return YES;
    }
    return NO;
}

+(NSString *) getToken {
    return (NSString *)[[[UIApplication sharedApplication] delegate] performSelector:@selector(token)];
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
    
    mainBundle = [NSBundle mainBundle];
    delegate = [[UIApplication sharedApplication] delegate];
    NSString *url = [NSString stringWithFormat:@"%@oauth/authorize?redirect_uri=%@&client_id=%@", [mainBundle objectForInfoDictionaryKey:@"apiAuthBase"], [mainBundle objectForInfoDictionaryKey:@"redirectUrl"], [mainBundle objectForInfoDictionaryKey:@"clientId"]];
    NSLog(@"Sending url for authorization");
    [self.web loadRequest:[NSURLRequest requestWithURL: [NSURL URLWithString:url]]];
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

/**
 * Method that parses the OAuth token out of the response.
 */
- (void) getOauthToken {
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"%@oauth/token?client_id=%@&client_secret=%@&code=%@&redirect_uri=%@", [mainBundle objectForInfoDictionaryKey:@"apiAuthBase"], [mainBundle objectForInfoDictionaryKey:@"clientId"], [mainBundle objectForInfoDictionaryKey:@"clientSecret"],  self.code, [mainBundle objectForInfoDictionaryKey:@"redirectUrl"]]];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    NSDictionary *token = [[request responseString] JSONValue];
    NSLog(@"Authentication token received, ready to make API calls");
    
    delegate.token = [token objectForKey:@"access_token"];
    [self.navigationController dismissModalViewControllerAnimated:YES];
}

/**
 * UIWebView Delegate
 */
- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    return YES;
    
}

/**
 * Once the authorization code page loads this method will use the code to get the oauth token
 */
- (void) webViewDidFinishLoad:(UIWebView *)webView {
    NSString *requestURL = [[webView.request URL] relativeString];
    NSString *response = [webView stringByEvaluatingJavaScriptFromString:@"document.documentElement.textContent"];
    if([requestURL hasSuffix:@"saml/sso/post"])
    {
        NSLog(@"Authentication code has been received");
        self.code = [[response JSONValue] objectForKey:@"authorization_code"];
        [self getOauthToken];
        
    }
}
   
@end
