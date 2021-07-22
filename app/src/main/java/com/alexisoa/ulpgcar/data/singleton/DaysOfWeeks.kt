package com.alexisoa.ulpgcar.data.singleton

object DaysOfWeeks{
    val dayWeek = hashMapOf<Int, String>(
            1 to "Domingo", 2 to "Lunes", 3 to "Martes", 4 to "Miércoles", 5 to "Jueves", 6 to "Viernes", 7 to "Sábado"
    )

    init {
        println("Instancia de día de la semana")
    }

    fun printName(): HashMap<Int,String> = dayWeek
}