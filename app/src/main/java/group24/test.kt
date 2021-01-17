package group24

import java.util.*
import kotlin.collections.ArrayList


val variable = ""
val variableOfType: String = ""
val variableOfObject = Date()

lateinit var editableVariableOfType: String


fun aFunctionThatReturnsString(): String {
    val list = ArrayList<String>()
    list.add("a"); list.add("b"); list.add("c")
    for (a in list) {
        print(a)
    }
    val conditionResult = if (1+1 == 2) {
        3
    } else {
        2
    }

    val classObject = ClassWhichImplementsAnInterface("")
    return ""
}

interface AnInterface {
    fun aMethodToImplement(withAParameterOfTypeInt: Int)
}

class ClassWhichImplementsAnInterface(val constructorVariable: String) : AnInterface {

    override fun aMethodToImplement(withAParameterOfTypeInt: Int) {
        val dataObject = DataObject(1,"")
    }

}

data class DataObject(
    val dataObjectVariables: Int,
    val oftenUsedForDTOs: String
)


