package group24.oplevelserbekaemperensomhed.data

import group24.oplevelserbekaemperensomhed.data.EventDTO

data class SearchHomeItem(
    val category: String, val searchItemHorizontal: List<EventDTO>
)