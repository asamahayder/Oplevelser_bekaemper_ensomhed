package group24.oplevelserbekaemperensomhed.data

import group24.oplevelserbekaemperensomhed.logic.firebase.DBBanner

object LocalData{

    lateinit var id: String
    lateinit var userPassword: String

    lateinit var userData: UserDTO
    var userCreatedEvents: ArrayList<EventDTO> = ArrayList()
    var searchResultsEvents = ArrayList<EventDTO>()
    var searchResultsBanners = ArrayList<DBBanner>()
}