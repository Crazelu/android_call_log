class CallInfo {
  final String caller;
  final String phone;
  final String date;
  final String type;
  final String duration;

  CallInfo({
    required this.caller,
    required this.phone,
    required this.date,
    required this.type,
    required this.duration,
  });

  String get callerId => caller.isEmpty ? phone : caller;

  factory CallInfo.fromJson(Map json) {
    return CallInfo(
      caller: json["name"] ?? "No Name",
      phone: json["phoneNumber"] ?? "No Phone",
      date: json["callTime"] ?? "No Time",
      type: json["callType"] ?? "No Type",
      duration: json["callDuration"] ?? "No Duration",
    );
  }
}
