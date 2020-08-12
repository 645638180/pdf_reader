import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:pdf_reader/pdf_reader.dart';

class PDFScaffold extends StatefulWidget {
  final PreferredSizeWidget appBar;
  final double marginLeft;
  final double marginTop;
  final String path;
  final bool primary;

  const PDFScaffold({
    Key key,
    this.appBar,
    this.marginLeft,
    this.marginTop,
    @required this.path,
    this.primary = true,
  }) : super(key: key);

  @override
  _PDFScaffoldState createState() => new _PDFScaffoldState();
}

class _PDFScaffoldState extends State<PDFScaffold> {
  final pdfViwerRef = new PdfReader();
  Rect _rect;
  Timer _resizeTimer;
  bool LoadPDF = false;

  double fontSize_Title = 28;

  @override
  void initState() {
    super.initState();
    pdfViwerRef.close();
  }

  @override
  void dispose() {
    super.dispose();
    pdfViwerRef.close();
    pdfViwerRef.dispose();
  }

  @override
  void didChangeDependencies() async {
    super.didChangeDependencies();
    if (_rect == null) {
      _rect = _buildRect(context);
      print("_rect${_rect}");
      LoadPDF = await pdfViwerRef.launch(
        widget.path,
        rect: _rect,
      );
      print("LoadPDF-${LoadPDF}");
    } else {
      final rect = _buildRect(context);
      if (_rect != rect) {
        _rect = rect;
        _resizeTimer?.cancel();
        _resizeTimer = new Timer(new Duration(milliseconds: 300), () {
          pdfViwerRef.resize(_rect);
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
        body: Center(child: !LoadPDF ? Text("未获取工位信息或未找到pdf文件", style: TextStyle(fontSize: 20)) : Text("")));
  }

  Rect _buildRect(BuildContext context) {
    final fullscreen = widget.appBar == null;
    final mediaQuery = MediaQuery.of(context);
    final topPadding = widget.primary ? mediaQuery.padding.top : 0.0;
    final top = fullscreen ? 0.0 : widget.appBar.preferredSize.height + topPadding;
    var height = mediaQuery.size.height - top;
    if (height < 0.0) {
      height = 0.0;
    }

    return new Rect.fromLTWH(widget.marginLeft, top + widget.marginTop, mediaQuery.size.width - widget.marginLeft,
        height - widget.marginTop);
  }
}
