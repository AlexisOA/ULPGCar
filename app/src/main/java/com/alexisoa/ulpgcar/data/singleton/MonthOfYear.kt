package com.alexisoa.ulpgcar.data.singleton

object MonthOfYear{
    val monthName = hashMapOf<Int, String>(
            0 to "Enero", 1 to "Febrero", 2 to "Marzo", 3 to "Abril", 4 to "Mayo", 5 to "Junio", 6 to "Julio",
            7 to "Agosto", 8 to "Septiembre", 9 to "Octubre", 10 to "Noviembre", 11 to "Diciembre"
    )

    init {
        println("Instancia de nombre del mes")
    }

    fun printName(): HashMap<Int,String> = monthName
}