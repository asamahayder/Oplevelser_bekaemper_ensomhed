package group24.oplevelserbekaemperensomhed.logic.firebase

// Data transfer object

data class DBUser(
    val name: String = "",
    val age: List<Int> = emptyList(),
    val gender: String = "",
    val about: String = "",
    val address: String = "",
    val occupation: String = "",
    val education: String = "",
    val eventsCreatedByUser: List<String> = emptyList(),
    val profilePictures: List<String> = emptyList()
) {
    constructor() :
            this("",emptyList(),"","","","","",
                emptyList(), emptyList())
}