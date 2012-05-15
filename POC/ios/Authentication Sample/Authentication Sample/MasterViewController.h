//
//  MasterViewController.h
//  Authentication Sample
//
//  Created by Ryan Latta on 5/14/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ASIHTTPRequest.h"

@class DetailViewController;

@interface MasterViewController : UITableViewController<ASIHTTPRequestDelegate>

@property (strong, nonatomic) DetailViewController *detailViewController;

@end
