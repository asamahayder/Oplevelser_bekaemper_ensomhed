package group24.oplevelserbekaemperensomhed.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventDTO (
    val eventCreator: UserDTO,
    val participants: ArrayList<UserDTO>,
    val eventDescription: String,
    val eventTitle: String,
    val eventDate: DateDTO,
    val Category: String,
    val address: String,
    val price: String,
    val pictureURL: String
):Parcelable