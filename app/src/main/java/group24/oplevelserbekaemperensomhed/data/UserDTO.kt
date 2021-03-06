package group24.oplevelserbekaemperensomhed.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlin.collections.ArrayList

@Parcelize
data class UserDTO
    (
    var name: String?,
    var age: List<Int>,
    var address: String?,
    var occupation: String?,
    var education: String?,
    var about: String?,
    var gender: String?,
    var eventsCreatedByUser: ArrayList<EventDTO>?,
    var profilePictures: ArrayList<String>
):Parcelable