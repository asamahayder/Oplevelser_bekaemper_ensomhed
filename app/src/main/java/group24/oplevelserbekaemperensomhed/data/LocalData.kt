package group24.oplevelserbekaemperensomhed.data

import group24.oplevelserbekaemperensomhed.logic.firebase.DBBanner

object LocalData{

    var userID: String? = null

    var userData: UserDTO? = null
    var userCreatedEvents: ArrayList<EventDTO> = ArrayList()
    var searchResultsEvents = ArrayList<EventDTO>()
    var searchResultsBanners = ArrayList<DBBanner>()
}