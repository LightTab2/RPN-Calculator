package com.inf146470.rpnkalkulator

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.math.BigDecimal

class MainViewModel: ViewModel(), Observable
{
    @Bindable
    val stackCounter    = MutableLiveData("0")
    @Bindable
    val edit            = MutableLiveData("")
    //Te rozwiązanie, mimo że wygodniejsze, nie działa. Chodzi o to, że nadzorowana jest tablica (wskaźnik), a nie jej dane
    //@Bindable
    //val stack         = MutableLiveData(arrayOf("", "", "", ""))
    @Bindable
    val stack1          = MutableLiveData("")
    @Bindable
    val stack2          = MutableLiveData("")
    @Bindable
    val stack3          = MutableLiveData("")
    @Bindable
    val stack4          = MutableLiveData("")
    @Bindable
    val editMode        = MutableLiveData(false)

    var prefOpen        = false
    val stack           : MutableList<BigDecimal>   = ArrayList()
    var history         : MutableList<History>      = ArrayList()
    var historyIndex    = 0

    fun clear()
    {
        stackCounter.value  = "0"
        edit.value          = ""
        //for (i in 0 until stack.value!!.size) stack.value!![i] = ""
        stack1.value        = ""
        stack2.value        = ""
        stack3.value        = ""
        stack4.value        = ""
        editMode.value      = false
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?)
    {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?)
    {
    }
}