package group24.oplevelserbekaemperensomhed.logic.firebase

data class DBEvent (
    val address: String = "",
    val category: String = "",
    val eventCreator: String = "",
    val eventDate: List<Int> = emptyList(),
    val eventDescription: String = "",
    val eventLikes: Int = -1,
    val eventTitle: String = "",
    val participants: List<String> = emptyList(),
    val pictures: List<String> = emptyList(),
    val price: String = ""
) {
    constructor() :
            this("","","",emptyList(),"",-1,
                "",emptyList(),emptyList(),"")

}