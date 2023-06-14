import 'dart:io';
import 'package:call_log/call_info.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:developer' as dev;
import 'package:intl/intl.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Call Log Demo',
      theme: ThemeData(
        primarySwatch: Colors.purple,
      ),
      home: const MyHomePage(title: 'The Boring Call Log Demo'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const _platform = MethodChannel('dev.crazelu/call_log_manager');

  static const _limit = 10;
  int _page = 1;
  bool _canFetchNextPage = false;

  List<CallInfo> _calls = [];

  Future<void> _checkPermission() async {
    if (!Platform.isAndroid) {
      dev.log("Only Android is supported");
      return;
    }
    await _platform.invokeMethod("requestPermission");
  }

  Future<void> _getCallLog() async {
    try {
      if (!Platform.isAndroid) {
        dev.log("Only Android is supported");
        return;
      }
      await _checkPermission();

      final callLog = await _platform.invokeMethod(
        "getCallLog",
        {
          "page": _page,
          "itemsPerPage": _limit,
        },
      );

      if (callLog is List) {
        _calls += callLog.map((e) => CallInfo.fromJson(e)).toList();
        _canFetchNextPage = callLog.length == _limit;
        if (_canFetchNextPage) _page++;
        setState(() {});
      }
    } catch (e) {
      dev.log(e.toString());
    }
  }

  Future<void> _getNextPage() async {
    if (!_canFetchNextPage) return;

    await _getCallLog();
  }

  @override
  void initState() {
    super.initState();
    Future.microtask(_getCallLog);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: ListView.builder(
        itemCount: _calls.length,
        itemBuilder: (context, index) {
          final info = _calls[index];
          if (_canFetchNextPage && index == _calls.length - 1) {
            return Column(
              children: [
                ListTile(
                  title: Text(info.callerId),
                  subtitle: Text(info.type),
                  trailing: Column(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      Text(
                        DateFormat.yMMMd().format(
                          DateTime.fromMillisecondsSinceEpoch(
                            int.parse(info.date),
                          ),
                        ),
                      ),
                      Text("${info.duration} secs"),
                    ],
                  ),
                ),
                const SizedBox(height: 8),
                TextButton(
                  onPressed: _getNextPage,
                  child: const Text("Get Next Page"),
                ),
              ],
            );
          }
          return ListTile(
            title: Text(info.callerId),
            subtitle: Text(info.type),
            trailing: Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text(
                  DateFormat.yMMMd().format(
                    DateTime.fromMillisecondsSinceEpoch(
                      int.parse(info.date),
                    ),
                  ),
                ),
                Text("${info.duration} secs"),
              ],
            ),
          );
        },
      ),
    );
  }
}
