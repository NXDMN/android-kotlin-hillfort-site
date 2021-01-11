package org.wit.site.views.login

import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.toast
import org.wit.site.models.firebase.SiteFireStore
import org.wit.site.views.BasePresenter
import org.wit.site.views.BaseView
import org.wit.site.views.VIEW

class LoginPresenter(view: BaseView) : BasePresenter(view) {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firestore: SiteFireStore? = null

    init {
      if(app.sites is SiteFireStore) {
          firestore = app.sites as SiteFireStore
      }
    }

    fun doLogin(email: String, password: String){
        view?.showProgress()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(view!!) { task ->
            if(task.isSuccessful){
                if(firestore != null){
                    firestore!!.fetchSites {
                        view?.hideProgress()
                        view?.navigateTo(VIEW.LIST)
                    }
                }else{
                    view?.hideProgress()
                    view?.navigateTo(VIEW.LIST)
                }
            } else {
                view?.hideProgress()
                view?.toast("Login Failed: ${task.exception?.message}")
            }
        }
    }

    fun doSignUp(email: String, password: String) {
        view?.showProgress()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(view!!){ task ->
            if(task.isSuccessful){
                firestore!!.fetchSites {
                    view?.hideProgress()
                    view?.navigateTo(VIEW.LIST)
                }
                view?.hideProgress()
                view?.navigateTo(VIEW.LIST)
            } else {
                view?.hideProgress()
                view?.toast("Sign Up Failed: ${task.exception?.message}")
            }
        }
    }
}