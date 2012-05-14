//
//  OAuthViewController.h
//  Authentication Sample
//
//  Created by Ryan Latta on 5/14/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface OAuthViewController : UIViewController<UIWebViewDelegate>

@property (nonatomic, retain) UIWebView *web;
@property (nonatomic, retain) NSString *code;

@end
