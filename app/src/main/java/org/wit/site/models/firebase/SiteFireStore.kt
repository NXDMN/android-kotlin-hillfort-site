package org.wit.site.models.firebase

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.jetbrains.anko.AnkoLogger
import org.wit.site.helpers.readImageFromPath
import org.wit.site.models.SiteModel
import org.wit.site.models.SiteStore
import java.io.ByteArrayOutputStream
import java.io.File

class SiteFireStore(val context: Context): SiteStore, AnkoLogger {

  val sites = ArrayList<SiteModel>()
  lateinit var userId: String
  lateinit var db: DatabaseReference
  lateinit var st: StorageReference

  override fun findAll(): List<SiteModel> {
    return sites
  }

  override fun findById(id: Long): SiteModel? {
    val foundSite: SiteModel? = sites.find { s -> s.id == id }
    return foundSite
  }

  override fun create(site: SiteModel) {
    val key = db.child("users").child(userId).child("sites").push().key
    key?.let{
      site.fbId = key
      sites.add(site)
      db.child("users").child(userId).child("sites").child(key).setValue(site)
      updateImage(site, site.image)
      updateImage(site, site.image2)
      updateImage(site, site.image3)
      updateImage(site, site.image4)
    }
  }

  override fun update(site: SiteModel) {
    var foundSite: SiteModel? = sites.find { s -> s.fbId == site.fbId }
    if(foundSite != null){
      foundSite.name = site.name
      foundSite.description = site.description
      foundSite.visited = site.visited
      foundSite.date = site.date
      foundSite.notes = site.notes
      foundSite.image = site.image
      foundSite.image2 = site.image2
      foundSite.image3 = site.image3
      foundSite.image4 = site.image4
      foundSite.location = site.location
      foundSite.rating = site.rating
      foundSite.favourite = site.favourite
    }

    db.child("users").child(userId).child("sites").child(site.fbId).setValue(site)
    if((site.image.length) > 0 && (site.image[0] != 'h')){
      updateImage(site, site.image)
    }
    if((site.image2.length) > 0 && (site.image2[0] != 'h')){
      updateImage(site, site.image2)
    }
    if((site.image3.length) > 0 && (site.image3[0] != 'h')){
      updateImage(site, site.image3)
    }
    if((site.image4.length) > 0 && (site.image4[0] != 'h')){
      updateImage(site, site.image4)
    }
  }

  override fun delete(site: SiteModel) {
    db.child("users").child(userId).child("sites").child(site.fbId).removeValue()
    sites.remove(site)
  }

  override fun clear() {
    sites.clear()
  }

  fun fetchSites(sitesReady: () -> Unit){
    val valueEventListener = object: ValueEventListener{
      override fun onCancelled(dataSnapshot: DatabaseError) {
      }
      override fun onDataChange(dataSnapshot: DataSnapshot) {
        dataSnapshot!!.children.mapNotNullTo(sites) { it.getValue<SiteModel>(SiteModel::class.java) }
        sitesReady()
      }
    }
    userId = FirebaseAuth.getInstance().currentUser!!.uid
    db = FirebaseDatabase.getInstance().reference
    st = FirebaseStorage.getInstance().reference
    sites.clear()
    db.child("users").child(userId).child("sites").addListenerForSingleValueEvent(valueEventListener)
  }

  fun updateImage(site: SiteModel, image: String){
    if(image != ""){
      val fileName = File(image)
      val imageName = fileName.getName()

      var imageRef = st.child(userId + '/' + imageName)
      val baos = ByteArrayOutputStream()
      val bitmap = readImageFromPath(context, image)

      bitmap?.let{
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
          println(it.message)
        }.addOnSuccessListener { taskSnapshot ->
          taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
            when(image){
              site.image -> site.image = it.toString()
              site.image2 -> site.image2 = it.toString()
              site.image3 -> site.image3 = it.toString()
              site.image4 -> site.image4 = it.toString()
            }

            db.child("users").child(userId).child("sites").child(site.fbId).setValue(site)
          }
        }
      }
    }
  }
}
