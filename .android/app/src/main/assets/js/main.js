"use strict";
const count_ref = Array.of(0);
document.querySelector("#btn").addEventListener("click", () => { count_ref.push((1 + count_ref.pop())); document.querySelector("#text2").innerHTML = count_ref.at(0);; return console.info("FIXME(JS): clicked") })
const globalDispatch = (key, message) => { console.info("FIXME(JS): ", key, message); return document.querySelector("#text1").innerHTML = ("" + "Charge: " + JSON.parse(message).level + "%"); };
Android.registerBroadcast("charge_changed", "android.intent.action.BATTERY_CHANGED")
