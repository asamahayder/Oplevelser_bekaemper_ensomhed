package group24.oplevelserbekaemperensomhed.data

import kotlin.collections.ArrayList

data class UserDTO     //mangler evt.
//interests
//Social media integration (Instagram, facebook_common friends)
    (
    var name: String?,
    var age: Int?,
    var address: String?,
    var occupation: String?,
    var education: String?,
    var about: String?,
    var gender: String?,
    var eventsCreatedByUser: ArrayList<EventDTO>?,
    var profilePictures: ArrayList<String>
)