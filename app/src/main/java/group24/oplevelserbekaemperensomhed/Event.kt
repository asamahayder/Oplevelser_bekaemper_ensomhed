package group24.oplevelserbekaemperensomhed

import group24.oplevelserbekaemperensomhed.data.DateDTO
import group24.oplevelserbekaemperensomhed.data.UserDTO

data class Event (
    val eventCreator: UserDTO,
    val participants: ArrayList<UserDTO>,
    val eventDescription: String,
    val eventTitle: String,
    val eventDate: DateDTO,
    val Category: String,
    val address: String,
    val price: String,
    val pictureURL: String
)