//
//  TestPlugin.m
//  testCordova
//
//  Created by mr、j on 2018/3/15.
//

#import "ContactPlugin.h"
#import <Contacts/Contacts.h>
#import <AddressBook/AddressBookDefines.h>
#import <AddressBook/ABRecord.h>

@interface ContactPlugin ()
{
    NSString *phoneNumber;
    CDVInvokedUrlCommand *urlCommand;
}

@property (nonatomic, strong) NSMutableArray *myArr;

@end

@implementation ContactPlugin

-(void)getContactOrAppList:(CDVInvokedUrlCommand *)command
{
    urlCommand = command;
    [self requestAuthorizationForAddressBook];
}
-(void)requestAuthorizationForAddressBook {
    if ([[UIDevice currentDevice].systemVersion floatValue] >= 9.0) {
        CNAuthorizationStatus authorizationStatus = [CNContactStore authorizationStatusForEntityType:CNEntityTypeContacts];
        if (authorizationStatus == CNAuthorizationStatusNotDetermined) {
            CNContactStore *contactStore = [[CNContactStore alloc] init];
            [contactStore requestAccessForEntityType:CNEntityTypeContacts completionHandler:^(BOOL granted, NSError * _Nullable error) {
                if (granted) {
                    [self getMyAddressBook];
                } else {
                    NSLog(@"授权失败, error = %@", error);
                }
            }];
        } else if (status == CNAuthorizationStatusAuthorized) {
            [self getMyAddressBook];
        }
    } else {
        NSLog(@"请升级系统");
    }
}

-(void)getMyAddressBook{
    self.myArr = [NSMutableArray array];
    NSArray *keysToFetch = @[CNContactGivenNameKey, CNContactFamilyNameKey, CNContactPhoneNumbersKey];
    CNContactFetchRequest *fetchRequest = [[CNContactFetchRequest alloc] initWithKeysToFetch:keysToFetch];
    CNContactStore *contactStore = [[CNContactStore alloc] init];
    [contactStore enumerateContactsWithFetchRequest:fetchRequest error:nil usingBlock:^(CNContact * _Nonnull contact, BOOL * _Nonnull stop) {
        NSMutableArray *phoneArr = [NSMutableArray array];
        NSMutableDictionary *contactDic = [NSMutableDictionary dictionary];
        NSString *nameStr = [NSString stringWithFormat:@"%@%@",contact.familyName,contact.givenName];
        NSArray *phoneNumbers = contact.phoneNumbers;
        for (CNLabeledValue *labelValue in phoneNumbers) {
            CNPhoneNumber *phoneValue = labelValue.value;
            phoneNumber = [self changeString:phoneValue.stringValue];
            [phoneArr addObject:phoneNumber];
        }
        [contactDic setObject:nameStr forKey:@"contactName"];
        [contactDic setObject:phoneArr forKey:@"phoneNos"];
        [_myArr addObject:contactDic];
    }];

    NSString *Str = [self arrayToJson:_myArr];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:Str];
    [self.commandDelegate sendPluginResult:result callbackId:urlCommand.callbackId];
}

- (NSString*)arrayToJson:(NSArray *)arr
{
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:arr options:NSJSONWritingPrettyPrinted error:&error];
    NSString *jsonString;
    if (!jsonData) {
        NSLog(@"%@",error);
    }else{
        jsonString = [[NSString alloc]initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    NSMutableString *mutStr = [NSMutableString stringWithString:jsonString];
    NSRange range = {0,jsonString.length};
    
    //去掉字符串中的空格
    
    [mutStr replaceOccurrencesOfString:@" " withString:@"" options:NSLiteralSearch range:range];
    
    NSRange range2 = {0,mutStr.length};
    
    //去掉字符串中的换行符
    
    [mutStr replaceOccurrencesOfString:@"\n" withString:@"" options:NSLiteralSearch range:range2];
    
    return mutStr;
}

-(NSString *)changeString:(NSString *)str
{
    NSCharacterSet *doNotWant = [NSCharacterSet characterSetWithCharactersInString:@"（#%-*+=_）\\|~(＜＞$%^&*)_+ "];
    NSString * hmutStr = [[str componentsSeparatedByCharactersInSet: doNotWant]componentsJoinedByString: @""];
    return hmutStr;
}

@end

