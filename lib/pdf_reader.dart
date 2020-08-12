import 'dart:async';
import 'dart:ui';

import 'package:flutter/services.dart';

class PdfReader {
  static const MethodChannel _channel =
      const MethodChannel('pdf_reader');

  static PdfReader _instance;

  factory PdfReader() => _instance ??= new PdfReader._();
  PdfReader._() {
    _channel.setMethodCallHandler(_handleMessages);
  }

  final _onDestroy = new StreamController<Null>.broadcast();
  Stream<Null> get onDestroy => _onDestroy.stream;
  Future<Null> _handleMessages(MethodCall call) async {
    switch (call.method) {
      case 'onDestroy':
        _onDestroy.add(null);
        break;
    }
  }

  launch(String path, {Rect rect}) async {
    final args = <String, dynamic>{'path': path};
    if (rect != null) {
      print(rect);
      args['rect'] = {
        'left': rect.left,
        'top': rect.top,
        'width': rect.width,
        'height': rect.height
      };
    }
    var a = await _channel.invokeMethod('launch', args);
    if(a=="error"){
      return false;
    }
    return true;
    print("a${a}");
  }

  /// Close the PDFViewer
  /// Will trigger the [onDestroy] event
  Future close() => _channel.invokeMethod('close');

  /// adds the plugin as ActivityResultListener
  /// Only needed and used on Android
  Future registerAcitivityResultListener() =>
      _channel.invokeMethod('registerAcitivityResultListener');

  /// removes the plugin as ActivityResultListener
  /// Only needed and used on Android
  Future removeAcitivityResultListener() =>
      _channel.invokeMethod('removeAcitivityResultListener');

  /// Close all Streams
  void dispose() {
    _onDestroy.close();
    _instance = null;
  }

  /// resize PDFViewer
  Future<Null> resize(Rect rect) async {
    final args = {};
    args['rect'] = {
      'left': rect.left,
      'top': rect.top,
      'width': rect.width,
      'height': rect.height
    };
    await _channel.invokeMethod('resize', args);
  }



}
