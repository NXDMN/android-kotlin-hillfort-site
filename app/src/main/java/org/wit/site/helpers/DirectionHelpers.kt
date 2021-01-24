package org.wit.site.helpers

import android.app.Activity
import com.google.android.gms.maps.model.LatLng
import org.wit.site.R

fun getDirectionsUrl(activity: Activity, origin: LatLng?, dest: LatLng?): String{
  val str_origin = "origin=" + origin?.latitude + "," + origin?.longitude
  val str_dest = "destination=" + dest?.latitude + "," + dest?.longitude
  val key = "key=${activity.getString(R.string.google_maps_key)}"

  val parameters = "${str_origin}&${str_dest}&${key}"

  return "https://maps.googleapis.com/maps/api/directions/json?${parameters}"
}

fun decodePoly(encoded: String): List<LatLng> {
  val poly = ArrayList<LatLng>()
  var index = 0
  val len = encoded.length
  var lat = 0
  var lng = 0

  while (index < len) {
    var b: Int
    var shift = 0
    var result = 0
    do {
      b = encoded[index++].toInt() - 63
      result = result or (b and 0x1f shl shift)
      shift += 5
    } while (b >= 0x20)
    val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
    lat += dlat

    shift = 0
    result = 0
    do {
      b = encoded[index++].toInt() - 63
      result = result or (b and 0x1f shl shift)
      shift += 5
    } while (b >= 0x20)
    val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
    lng += dlng

    val p = LatLng(lat.toDouble() / 1E5,
        lng.toDouble() / 1E5)
    poly.add(p)
  }

  return poly
}