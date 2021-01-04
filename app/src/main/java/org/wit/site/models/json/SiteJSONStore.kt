package org.wit.site.models.json

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.AnkoLogger
import org.wit.site.helpers.exists
import org.wit.site.helpers.read
import org.wit.site.helpers.write
import org.wit.site.models.SiteModel
import org.wit.site.models.SiteStore
import java.util.*

val JSON_FILE = "placemarks.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<SiteModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class SiteJSONStore : SiteStore, AnkoLogger {

    val context: Context
    var sites = mutableListOf<SiteModel>()
    constructor (context: Context) {
        this.context = context
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<SiteModel> {
        return sites
    }

    override fun create(site: SiteModel) {
        site.id = generateRandomId()
        sites.add(site)
        serialize()
    }


    override fun update(site: SiteModel) {
        val sitesList = findAll() as ArrayList<SiteModel>
        var foundSite: SiteModel? = sitesList.find { s -> s.id == site.id }
        if (foundSite != null) {
            foundSite.name = site.name
            foundSite.description = site.description
            foundSite.visited = site.visited
            foundSite.date = site.date
            foundSite.notes = site.notes
            foundSite.image = site.image
            foundSite.location = site.location
        }
        serialize()
    }

    override fun delete(site: SiteModel) {
//        val foundSite: SiteModel? = sites.find { it.id == site.id }
//        sites.remove(foundSite)
        sites.remove(site)
        serialize()
    }

    override fun findById(id:Long) : SiteModel? {
        val foundSite: SiteModel? = sites.find { it.id == id }
        return foundSite
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(sites, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        sites = Gson().fromJson(jsonString, listType)
    }
}
