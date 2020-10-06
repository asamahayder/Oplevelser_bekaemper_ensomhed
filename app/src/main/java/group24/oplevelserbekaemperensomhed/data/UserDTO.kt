package group24.oplevelserbekaemperensomhed.data

import java.util.*

data class UserDTO     //mangler evt.
//picture
//interests
//Social media integration (Instagram, facebook_common friends)
    (
    var name: String,
    var age: Int,
    var address: String,
    var occupation: String,
    var education: String,
    var about: String,
    var gender: String,
    var eventsCreatedByUser: ArrayList<EventDTO>
)