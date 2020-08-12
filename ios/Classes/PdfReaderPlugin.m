#import "PdfReaderPlugin.h"
#if __has_include(<pdf_reader/pdf_reader-Swift.h>)
#import <pdf_reader/pdf_reader-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "pdf_reader-Swift.h"
#endif

@implementation PdfReaderPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPdfReaderPlugin registerWithRegistrar:registrar];
}
@end
