package org.wit.site.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SiteModel(var id: Long = 0,
                     var name: String = "",
                     var description: String = "",
                     var visited: Boolean = false,
                     var date: String = "",
                     var notes: String = "",
                     var image: String = "",
                     var lat : Double = 0.0,
                     var lng: Double = 0.0,
                     var zoom: Float = 0f) : Parcelable

@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable