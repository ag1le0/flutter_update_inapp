import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:update_app/utils/update_app.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: OtaUpdateScreen(),
    );
  }
}

class OtaUpdateScreen extends StatefulWidget {
  @override
  _OtaUpdateScreenState createState() => _OtaUpdateScreenState();
}

class _OtaUpdateScreenState extends State<OtaUpdateScreen> {
  Event? currentEvent;

  @override
  void initState() {
    super.initState();

    try {
      UpdateApp()
          .execute(
              'https://10.224.81.70:6443/fiistore/ws-data/images/OPPM/OPPM.apk',
              'OPPM.apk')
          .listen((Event event) {
        setState(() {
          currentEvent = event;
        });
        switch (event.status) {
          case Status.stated:
            break;
          case Status.downloading:
            {
              break;
            }
          case Status.downloaded:
            break;
          case Status.downloadError:
            throw Exception(event.value);
          case Status.installing:
            break;
          case Status.alreadyRunningError:
            throw Exception(event.value);
        }
      });
    } catch (e) {
      Get.dialog(
          AlertDialog(
            title: const Text('Update fail!'),
            content: Text(
                'Sorry, something wrong, can\'t update application now\nLet contact support\nDetail error: ' +
                    e.toString()),
            actions: [
              TextButton(
                onPressed: () {
                  Get.back(result: false);
                },
                child: const Text('Update later'),
              ),
            ],
          ),
          barrierDismissible: false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Container(
      child: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            if (currentEvent != null) ...[
              Text(currentEvent!.status.name),
              if (currentEvent!.status == Status.downloading)
                Text('${currentEvent?.value}%'),
              if (currentEvent!.status == Status.downloadError)
                Text('${currentEvent?.value}')
            ],
          ],
        ),
      ),
    ));
  }
}
