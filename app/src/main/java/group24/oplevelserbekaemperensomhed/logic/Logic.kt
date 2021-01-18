package group24.oplevelserbekaemperensomhed.logic


import group24.oplevelserbekaemperensomhed.data.EventDTO
import group24.oplevelserbekaemperensomhed.data.SearchHomeItem
import java.util.*

class Logic {

    // Extra function that is used to calculate the age of a user based on the current date

    fun getAge(year: Int, month: Int, day: Int): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age
    }

    // Sorts the events by category
    fun sortEventsByCategory(eventList: ArrayList<EventDTO>): ArrayList<SearchHomeItem> {
        val searchHomeListVertical = ArrayList<SearchHomeItem>()

        // Iterates through the eventList
        for (i in eventList.indices) {
            val searchHomeListHorizontal: MutableList<EventDTO> = java.util.ArrayList()

            // defines which category the i'th category has
            val category: String = eventList[i].category

            // Boolean that is used to define if this is a new category or not
            var isANewCategory = false

            // Iterates through neighbours
            for (j in eventList.indices) {

                // defines which category the neighbour has to cross check with the i'th category
                val nextCategory: String = eventList[j].category

                // creates a event object from the list
                val (eventCreator, participants, eventDescription, eventTitle, eventDate, eventLikes, category1, address, price, pictures) = eventList[j]
                val item = EventDTO(
                    eventCreator,
                    participants,
                    eventDescription,
                    eventTitle,
                    eventDate, eventLikes, category1, address, price, pictures
                )

                // checks if the categories match
                if (category == nextCategory) {

                    // checks if the event already is contained in the vertical list or not
                    var shouldItemGoIntoHorizontalList = true
                    for (k in searchHomeListVertical.indices) {
                        if (searchHomeListVertical[k].category == category) {
                            // If the event matches a category in the vertical list, break and go past the next if statement
                            shouldItemGoIntoHorizontalList = false
                            break
                        }
                    }
                    // Adds the event to the horizontal list if the current category matches the current vertical category
                    if (shouldItemGoIntoHorizontalList || searchHomeListVertical.size == 0) {
                        isANewCategory = true
                        searchHomeListHorizontal.add(item)
                    }
                }
            }
            // if it's a new category, create a new category and add all elements with that category to the list
            if (isANewCategory) {
                val items = SearchHomeItem(category, searchHomeListHorizontal)
                searchHomeListVertical.add(items)
            }
        }
        return searchHomeListVertical
    }
}