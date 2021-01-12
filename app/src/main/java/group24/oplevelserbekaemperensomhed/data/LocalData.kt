package group24.oplevelserbekaemperensomhed.data

import group24.oplevelserbekaemperensomhed.logic.firebase.DBBanner

object LocalData{

    var userData: UserDTO? = null
    var searchResultsEvents = ArrayList<EventDTO>()
    var searchResultsBanners = ArrayList<DBBanner>()
}