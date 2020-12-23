package org.wit.site.models

interface SiteStore {
    fun findAll(): List<SiteModel>
    fun create(site: SiteModel)
    fun update(site: SiteModel)
    fun delete(site: SiteModel)
}