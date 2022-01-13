import 'dart:async';

import 'package:flutter/services.dart';

class OtaUpdate {
  static const EventChannel _progressChannel =
      EventChannel('foxconn.fii.app.update');
  Stream<OtaEvent>? _progressStream;

  Stream<OtaEvent> execute(String url) {
    final StreamController<OtaEvent> controller =
        StreamController<OtaEvent>.broadcast();
    if (_progressStream == null) {
      _progressChannel.receiveBroadcastStream(
        <dynamic, dynamic>{
          'url': url,
        },
      ).listen((dynamic event) {
        final OtaEvent otaEvent = _toOtaEvent(event.cast<String>());
        controller.add(otaEvent);
      }).onError((Object error) {
        if (error is PlatformException) {
          controller.add(_toOtaEvent(<String?>[error.code, error.message]));
        }
      });
      _progressStream = controller.stream;
    }
    return _progressStream!;
  }

  OtaEvent _toOtaEvent(List<String?> event) {
    return OtaEvent(OtaStatus.values[int.parse(event[0]!)], event[1]);
  }
}

class OtaEvent {
  OtaEvent(this.status, this.value);
  OtaStatus status;
  String? value;

  @override
  String toString() {
    return 'OtaEvent{status: $status, value: $value}';
  }
}

enum OtaStatus {
  DOWNLOADING,
  DOWNLOAD_ERROR,
  INSTALLING,
  INSTALL_ERROR,
  ALREADY_RUNNING_ERROR,
}
