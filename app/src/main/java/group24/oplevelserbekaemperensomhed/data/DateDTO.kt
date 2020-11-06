package group24.oplevelserbekaemperensomhed.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DateDTO (
    val day: Int,
    val month: Int,
    val year: Int
):Parcelable