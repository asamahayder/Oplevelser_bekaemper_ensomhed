package group24.oplevelserbekaemperensomhed.logic.firebase

// Data transfer object

class DBEventParticipantPair(val eventName: String, val participant: String) {
    constructor() :
            this("","")
}