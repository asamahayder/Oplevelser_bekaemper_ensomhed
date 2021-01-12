package group24.oplevelserbekaemperensomhed.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class EventDTO(
    var eventCreator: UserDTO,
    val participants: ArrayList<UserDTO>?,
    val eventDescription: String,
    val eventTitle: String,
    val eventDate: DateDTO,
    val eventLikes: Int,
    val category: String,
    val address: String,
    val price: String,
    val pictures: ArrayList<String>
):Parcelable