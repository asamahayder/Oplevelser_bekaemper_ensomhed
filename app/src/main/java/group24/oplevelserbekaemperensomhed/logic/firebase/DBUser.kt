package group24.oplevelserbekaemperensomhed.logic.firebase

data class DBUser(
    val name: String = "",
    val age: Int = -1,
    val gender: String = "",
    val about: String = "",
    val address: String = "",
    val occupation: String = "",
    val education: String = "",
    val eventsCreatedByUser: List<String> = emptyList(),
    val profilePictures: List<String> = emptyList()
) {
    constructor() :
            this("",-1,"","","","","",
                emptyList(), emptyList())
}