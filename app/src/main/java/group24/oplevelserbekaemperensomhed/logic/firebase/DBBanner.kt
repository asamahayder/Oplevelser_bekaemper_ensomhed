package group24.oplevelserbekaemperensomhed.logic.firebase

data class DBBanner (
    val picture: String,
    val url: String
) {
    constructor() :
            this("","")
}