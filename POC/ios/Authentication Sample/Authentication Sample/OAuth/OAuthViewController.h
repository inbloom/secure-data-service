//
//  OAuthViewController.h
//  Authentication Sample
//
//  Created by Ryan Latta on 5/14/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ASIHTTPRequest.h"

@interface OAuthViewController : UIViewController<UIWebViewDelegate,ASIHTTPRequestDelegate>

@property (nonatomic, retain) UIWebView *web;
@property (nonatomic, retain) NSString *code;

+(BOOL) isAuthenticated;
+(NSString *) getToken;

@end
