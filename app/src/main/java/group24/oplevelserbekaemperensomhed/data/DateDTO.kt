package group24.oplevelserbekaemperensomhed.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DateDTO (
    val date: String,
    val startTime: String,
    val endTime: String
):Parcelable