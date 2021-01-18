package group24.oplevelserbekaemperensomhed.data

import group24.oplevelserbekaemperensomhed.logic.firebase.DBBanner

object LocalData{

    // Singleton object that is used to handle local data
    // Although called local data, this object does not save anything locally to the device,
    // it only keeps track of the data being used during the session the app has been initialized

    lateinit var id: String
    lateinit var userPassword: String
    lateinit var userEmail: String

    lateinit var userData: UserDTO
    var searchResultsEvents = ArrayList<EventDTO>()
    var searchResultsBanners = ArrayList<DBBanner>()
}