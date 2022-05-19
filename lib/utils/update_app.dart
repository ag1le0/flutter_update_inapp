import 'dart:async';

import 'package:flutter/services.dart';

class UpdateApp {
  static const EventChannel _progressChannel =
      EventChannel('com.htn.update_app');
  Stream<Event>? _progressStream;

  Stream<Event> execute(String url, String fileName) {
    final StreamController<Event> controller =
        StreamController<Event>.broadcast();
    if (_progressStream == null) {
      _progressChannel.receiveBroadcastStream(
        <dynamic, dynamic>{
          'url': url,
          'file_name': fileName,
        },
      ).listen((dynamic e) {
        final Event event = _toEvent(e);
        controller.add(event);
      }).onError((Object error) {
        if (error is PlatformException) {
          controller.add(_toEvent(<String?>[error.code, error.message]));
        }
      });
      _progressStream = controller.stream;
    }
    return _progressStream!;
  }

  Event _toEvent(dynamic event) {
    return Event(Status.values[int.parse(event[0])], event[1]);
  }
}

class Event {
  Event(this.status, this.value);
  Status status;
  String? value;

  @override
  String toString() {
    return 'Event{status: $status, value: $value}';
  }
}

enum Status {
  stated,
  downloading,
  downloaded,
  downloadError,
  installing,
  alreadyRunningError,
}
