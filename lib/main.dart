import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:update_app/utils/linear_persent_indicator.dart';
import 'package:update_app/utils/ota_update.dart';

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
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or simply save your changes to "hot reload" in a Flutter IDE).
        // Notice that the counter didn't reset back to zero; the application
        // is not restarted.
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
  bool isLoading = true;
  bool isAuth = false;
  OtaEvent? currentEvent;

  @override
  void initState() {
    super.initState();

    try {
      OtaUpdate()
          .execute(
        'link app',
      )
          .listen(
        (OtaEvent event) {
          if (event.status == OtaStatus.INSTALLING) {
          } else if (event.status == OtaStatus.DOWNLOADING) {
            setState(() => currentEvent = event);
          } else {
            Get.dialog(AlertDialog(
              title: Text('Update fail!'),
              content: Text(
                  'Sorry, something wrong, cant update application now\n\nDetail error: ' +
                      event.value.toString()),
              actions: [
                TextButton(
                  onPressed: () {
                    Get.back();
                    Get.back(result: true);
                  },
                  child: const Text('Update later'),
                ),
              ],
            ));
          }
        },
      );
    } catch (e) {
      Get.dialog(
          AlertDialog(
            title: Text('Update fail!'),
            content: Text(
                'Sorry, something wrong, can\'t update application now\nLet contact FII support\nDetail error: ' +
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
          padding: EdgeInsets.all(30),
          width: double.infinity,
          height: double.infinity,
          decoration: BoxDecoration(
              gradient: LinearGradient(
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                  colors: [
                Colors.blueGrey,
                Colors.blue,
              ])),
          child: Column(
            mainAxisSize: MainAxisSize.max,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                margin: EdgeInsets.only(bottom: 30),
                height: 200,
                width: 200,
                decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(100),
                    color: Colors.black.withOpacity(.2),
                    border: Border.all(
                        color: Colors.white.withOpacity(.1),
                        width: 2,
                        style: BorderStyle.solid)),
                child: Center(
                  child: Icon(
                    Icons.sync,
                    size: 130,
                    color: Color.fromRGBO(84, 169, 238, .7),
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: LinearPercentIndicator(
                  //leaner progress bar
                  animation: true,
                  animationDuration: 100,
                  animateFromLastPercent: true,
                  lineHeight: 15.0,
                  percent: getProgress(),
                  // linearStrokeCap: LinearStrokeCap.roundAll,
                  barRadius: Radius.circular(10),
                  progressColor: Colors.blue[400],
                  backgroundColor: Colors.grey[300],
                ),
              ),
              SizedBox(
                height: 10,
              ),
              const Text(
                'Updating...',
                style: TextStyle(color: Colors.blue, fontSize: 18),
              ),
            ],
          )),
    );
  }

  double getProgress() {
    if (currentEvent == null) {
      return 0;
    } else if (currentEvent!.status == OtaStatus.DOWNLOADING) {
      return double.parse(currentEvent!.value!) / 100;
    } else if (currentEvent!.status == OtaStatus.INSTALLING) {
      return 1;
    } else {
      return 0;
    }
  }
}
