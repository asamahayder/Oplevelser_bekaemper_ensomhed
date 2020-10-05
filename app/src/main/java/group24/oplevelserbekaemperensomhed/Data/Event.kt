package group24.oplevelserbekaemperensomhed.Data

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