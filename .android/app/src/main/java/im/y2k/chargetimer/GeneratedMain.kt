package im.y2k.chargetimer

val main = { activity:android.app.Activity, webView:android.webkit.WebView ->  val webSettings = webView.getSettings(); webSettings.setJavaScriptEnabled(true); webView.addJavascriptInterface(object  { 
@android.webkit.JavascriptInterface
fun play_alarm (sound:Int) { activity.runOnUiThread({  val notification = android.media.RingtoneManager.getDefaultUri(sound); val r = android.media.RingtoneManager.getRingtone(activity, notification); r.play() }) }
@android.webkit.JavascriptInterface
fun registerBroadcast (topic:String, action:String) { activity.runOnUiThread({  do_register_receiver(activity, action, { json:String ->  webView.evaluateJavascript(("" + topic + "('" + json + "')"), null) }) }) } }, "Android"); webView.loadUrl("file:///android_asset/index.html") }
val do_register_receiver = { context:android.content.Context, action:String, callback:(String)->Unit ->  context.registerReceiver(object : android.content.BroadcastReceiver() { 
 override fun onReceive (context:android.content.Context, intent:android.content.Intent) { val extras = requireNotNull(intent.extras); val allValues = extras.keySet().map({ key ->  Pair(key, extras.get(key)) }).associate({ x ->  x }); callback(com.google.gson.Gson().toJson(allValues)) } }, android.content.IntentFilter(action)) }
