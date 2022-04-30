package com.inf146470.rpnkalkulator

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import ch.obermuhlner.math.big.DefaultBigDecimalMath.*
import ch.obermuhlner.math.big.kotlin.bigdecimal.*
import com.inf146470.rpnkalkulator.Action.*
import com.inf146470.rpnkalkulator.databinding.ActivityMainBinding
import java.lang.Math.max
import java.math.BigDecimal
import java.math.MathContext

class MainActivity :    AppCompatActivity()
{
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main)
        bind.lifecycleOwner = this
        bind.mainViewModel = viewModel
        bind.preferencesViewModel = prefViewModel

        bind.zeroButton .setOnClickListener{ addChar('0') }
        bind.oneButton  .setOnClickListener{ addChar('1') }
        bind.twoButton  .setOnClickListener{ addChar('2') }
        bind.threeButton.setOnClickListener{ addChar('3') }
        bind.fourButton .setOnClickListener{ addChar('4') }
        bind.fiveButton .setOnClickListener{ addChar('5') }
        bind.sixButton  .setOnClickListener{ addChar('6') }
        bind.sevenButton.setOnClickListener{ addChar('7') }
        bind.eightButton.setOnClickListener{ addChar('8') }
        bind.nineButton .setOnClickListener{ addChar('9') }
        bind.dotButton  .setOnClickListener()
        {
            if (viewModel.edit.value == "") {
                viewModel.edit.value = "0."
                viewModel.editMode.value = true
            }
            else if (!viewModel.edit.value!!.contains('.'))
                viewModel.edit.value += '.'
        }
        bind.signButton .setOnClickListener()
        {
            if (viewModel.editMode.value == true)
            {
                if (viewModel.edit.value != "" && viewModel.edit.value != "0")
                {
                    if (viewModel.edit.value!![0] == '-')
                        viewModel.edit.value = viewModel.edit.value!!.substring(1)
                    else
                        viewModel.edit.value = "-" + viewModel.edit.value
                }
            }
            else if (viewModel.stack.size > 0)
                {
                    viewModel.stack[viewModel.stack.size - 1] = viewModel.stack.last().negate()
                    displayStack()
                }
        }
        bind.ACButton       .setOnClickListener{AC()}
        bind.enterButton    .setOnClickListener{enter()}
        bind.plusButton     .setOnClickListener{arithmeticOperation(plus)}
        bind.minusButton    .setOnClickListener{arithmeticOperation(minus)}
        bind.multiplyButton .setOnClickListener{arithmeticOperation(multiply)}
        bind.divisionButton .setOnClickListener{arithmeticOperation(divide)}
        bind.powerButton    .setOnClickListener{arithmeticOperation(power)}
        bind.sqrtButton     .setOnClickListener{arithmeticOperation(sqrt)}
        bind.undoButton     .setOnClickListener{addChar_undo()}
        bind.swapButton     .setOnClickListener{swap()}
        bind.dropButton     .setOnClickListener{drop()}

        bind.preferencesButton.setOnClickListener{
            if (viewModel.prefOpen)
                return@setOnClickListener
            viewModel.prefOpen = true
            val intent = Intent(
                this,
                PreferencesActivity::class.java)
            intent.putExtra("bgColor", prefViewModel.backgroundColor.value)
            intent.putExtra("precision", prefViewModel.precision.value)
            preferences.launch(intent)
        }

        bind.stackLayout.setOnTouchListener(object : OnSwipeListener(this)
        {
            override fun onSwipeRight() {
                undo()
            }

            override fun onSwipeLeft() {
                redo()
            }
        })
        val orientation = resources.configuration.orientation
        if (orientation == ORIENTATION_LANDSCAPE)
            setButtonSize(bind.calculator, 12f)
        else
            setButtonSize(bind.calculator, 24f)
    }

    fun setButtonSize(parent : ViewGroup, size : Float)
    {
        for (i in 0 until parent.childCount)
        {
            val child = parent.getChildAt(i)

            if (child is Button) child.setTextSize(size)
            else if (child is ViewGroup)
                setButtonSize(child, size)
        }
    }

    fun addHistory(action : Action, stack1 : BigDecimal = valueOf(0), stack2 : BigDecimal = valueOf(0))
    {
        if (viewModel.historyIndex < viewModel.history.size)
            viewModel.history = viewModel.history.subList(0, viewModel.historyIndex)
        viewModel.history.add(History(action, stack1, stack2))
        viewModel.historyIndex += 1
    }

    fun addChar(c : Char)
    {
        viewModel.edit.value += c
        //Zapewnia poprawny zapis liczby np. brak nadmiernych zer na początku
        viewModel.edit.value = BigDecimal(viewModel.edit.value).toString()
        viewModel.editMode.value = true
    }

    fun addChar_undo()
    {
        if (viewModel.edit.value?.length == 0)
        {
            if (viewModel.stack.size == 0)
                return

            val value = viewModel.stack.last()
            if (value.precision() + max(0, -value.scale()) > 500)
            {
                Toast.makeText(this, "Zbyt duża liczba na szczycie stosu!", Toast.LENGTH_LONG).show()
                return
            }
            viewModel.edit.value        = viewModel.stack.last().toPlainString()
            viewModel.editMode.value    = true
            drop(true)
        }
        else if (viewModel.edit.value?.length != 0)
            viewModel.edit.value = viewModel.edit.value?.substring(0, (viewModel.edit.value!!.length - 1))

        if (viewModel.edit.value?.length == 0)
            viewModel.editMode.value = false
    }

    fun enter(bRedo : Boolean = false) {
        if (viewModel.edit.value?.length == 0)
            return

        viewModel.stack.add(BigDecimal(viewModel.edit.value).round(MathContext(prefViewModel.precision.value!!)))

        viewModel.edit.value = ""
        viewModel.editMode.value = false

        if (!bRedo)
            addHistory(enter, viewModel.stack.last())
        displayStack()
    }

    fun enter_undo()
    {
        //To raczej nie jest intuicyjne zachowanie, jeżeli chcielibyśmy z powrotem mieć w polu edycji
        //liczbę, którą właśnie chcemy usunąć ze szczytu stosu
        //viewModel.edit.value = viewModel.stack.last().toString()
        //viewModel.editMode.value = true
        viewModel.stack.removeLast()
    }

    fun swap(bUndo : Boolean = false, bRedo : Boolean = false)
    {
        if (viewModel.stack.size < 2)
            return

        val copy = viewModel.stack[viewModel.stack.size - 1]
        viewModel.stack[viewModel.stack.size - 1] = viewModel.stack[viewModel.stack.size - 2]
        viewModel.stack[viewModel.stack.size - 2] = copy
        if (!bUndo && !bRedo)
            addHistory(swap)
        displayStack()
    }

    fun drop(bSilent : Boolean = false, bRedo : Boolean = false)
    {
        if (!bRedo && !bSilent && viewModel.editMode.value == true)
        {
            viewModel.edit.value        = ""
            viewModel.editMode.value    = false
        }
        else if (viewModel.stack.size > 0)
        {
            if (!bRedo)
                addHistory(drop, viewModel.stack.last())
            viewModel.stack.removeLast()
        }
        displayStack()
    }

    fun drop_undo()
    {
        viewModel.stack.add(viewModel.history[viewModel.historyIndex].stack1.round(MathContext(prefViewModel.precision.value!!)))
    }

    fun arithmeticOperation(action : Action, bRedo : Boolean = false)
    {
        if (viewModel.stack.size < 2 && !(viewModel.stack.size == 1 && viewModel.editMode.value == true))
            if (!(action == sqrt && (viewModel.stack.size >= 1 || viewModel.editMode.value == true)))
                return

        if (viewModel.editMode.value == true)
            enter()

        if (!bRedo)
        {
            if (action == sqrt)
                addHistory(action, viewModel.stack.last())
            else
                addHistory(action, viewModel.stack.last(), viewModel.stack[viewModel.stack.size - 2])
        }
        val mathContext: MathContext = MathContext(prefViewModel.precision.value!!)
        try
        {
            when (action)
            {
                plus    ->
                {
                    createLocalMathContext(mathContext).use{
                        viewModel.stack[viewModel.stack.size - 2] = viewModel.stack[viewModel.stack.size - 2].add(viewModel.stack.last(), mathContext)
                    }
                }
                minus   ->
                {
                    createLocalMathContext(mathContext).use{
                        viewModel.stack[viewModel.stack.size - 2] = viewModel.stack[viewModel.stack.size - 2].subtract(viewModel.stack.last(), mathContext)
                    }
                }
                multiply->
                {
                    createLocalMathContext(mathContext).use {
                        viewModel.stack[viewModel.stack.size - 2] = viewModel.stack[viewModel.stack.size - 2].multiply(viewModel.stack.last(), mathContext)
                    }
                }
                divide  ->
                {
                    createLocalMathContext(mathContext).use{
                        viewModel.stack[viewModel.stack.size - 2] = viewModel.stack[viewModel.stack.size - 2].divide(viewModel.stack.last(), mathContext)
                    }
                }
                power   ->
                {
                    createLocalMathContext(mathContext).use{
                        viewModel.stack[viewModel.stack.size - 2] =  viewModel.stack[viewModel.stack.size - 2].pow(viewModel.stack.last())
                    }
                }
                sqrt    ->
                {
                    createLocalMathContext(mathContext).use{
                        viewModel.stack[viewModel.stack.size - 1] = viewModel.stack[viewModel.stack.size - 1].pow(BigDecimal(0.5))
                    }
                }
                else    ->
                {
                    viewModel.history.removeLast()
                    return
                }
            }
            if (action != sqrt)
                viewModel.stack.removeLast()
        }
        catch (e : Exception)
        {
            Toast.makeText(this, "Niepowodzenie w działaniu", Toast.LENGTH_SHORT).show()
            viewModel.history.removeLast()
            //if (bEdit == true)
                //viewModel.stack.removeLast()
        }
        displayStack()
    }

    fun arithmeticOperation_undo(action : Action)
    {
        viewModel.stack.removeLast()
        //Ewentualna zmiana precyzji musi być uwzględniona
        val roundContext = MathContext(prefViewModel.precision.value!!)
        if (action == sqrt)
            viewModel.stack.add(viewModel.history[viewModel.historyIndex].stack1.round(roundContext))
        else
        {
            viewModel.stack.add(viewModel.history[viewModel.historyIndex].stack2.round(roundContext))
            viewModel.stack.add(viewModel.history[viewModel.historyIndex].stack1.round(roundContext))
        }
    }

    fun AC()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Wymagane potwierdzenie")
        builder.setMessage("AC nie można cofnąć. Kontynuować?")

        builder.setPositiveButton("Tak") { _, _ ->
            viewModel.clear()
            viewModel.stack.clear()
            viewModel.history.clear()
            viewModel.historyIndex = 0
        }
        builder.setNegativeButton("Nie") { _, _ -> }
        builder.show()
    }

    fun redo()
    {
        if (viewModel.history.size < 1 || viewModel.historyIndex == viewModel.history.size)
            return

        if (viewModel.historyIndex < viewModel.history.size)
        {
            when(viewModel.history[viewModel.historyIndex].action)
            {
                enter    -> drop_undo()
                drop     -> drop(false, true)
                swap     -> swap(false, true)
                else     ->
                    arithmeticOperation(viewModel.history[viewModel.historyIndex].action, true)
            }
            displayStack()
        }
        ++viewModel.historyIndex
    }

    fun undo()
    {
        if (viewModel.history.size < 1 || viewModel.historyIndex == 0)
            return

        viewModel.historyIndex -= 1
        when(viewModel.history[viewModel.historyIndex].action)
        {
            enter    -> enter_undo()
            drop     -> drop_undo()
            swap     -> swap(true)
            else     -> arithmeticOperation_undo(viewModel.history[viewModel.historyIndex].action)
        }
        displayStack()
        //viewModel.history.removeLast()
    }

    fun setStackDisplay(stackText : MutableLiveData<String>, i : Int)
    {
        stackText.value = ""
        if (viewModel.stack.size > i)
        {
            //Raczej nie zmieścimy na ekranie tekstu, który ma powyżej 500 znaków
            //Zbudowanie skrajnie dużego String spowoduje crasha, więc trzeba się zabezpieczyć
            //Jeżeli ma mniej niż 500 znaków, to sprawdzamy, czy mieści się w bind.stack1
            //Wszystkie stacki są takie same pod względem metody wyświetlania, więc wybór jest dowolny
            val value = viewModel.stack[viewModel.stack.size - i - 1]
            if (((value.precision() + max(0, -value.scale())) > 500) ||
                (bind.stack1.paint.measureText(value.toPlainString()) > bind.stack1.measuredWidth))
                stackText.value = value.round(MathContext(20)).toEngineeringString()
            else
                stackText.value = value.toPlainString()
        }
    }

    fun displayStack()
    {
        /*for (i in 0 until viewModel.viewModel.stack.value!!.size)
        {
            viewModel.viewModel.stack.value!![i] = ""

            if (viewModel.stack.size > i)
            {
                //Raczej nie zmieścimy na ekranie tekstu, który ma powyżej 500 znaków
                //Zbudowanie skrajnie dużego String spowoduje crasha, więc trzeba się zabezpieczyćundo
                //Jeżeli ma mniej niż 500 znaków, to sprawdzamy, czy mieści się w bind.stack1
                //Wszystkie stacki są takie same pod względem metody wyświetlania, więc wybór jest dowolny
                val value = viewModel.stack[viewModel.stack.size - i - 1]
                if ((value.scale() > 500) ||
                    (bind.stack1.paint.measureText(value.toPlainString()) > bind.stack1.measuredWidth))
                    viewModel.viewModel.stack.value!![i] = value.toEngineeringString()
                else
                    viewModel.viewModel.stack.value!![i] = value.toPlainString()
            }
        }*/
        setStackDisplay(viewModel.stack1, 0)
        setStackDisplay(viewModel.stack2, 1)
        setStackDisplay(viewModel.stack3, 2)
        setStackDisplay(viewModel.stack4, 3)

        viewModel.stackCounter.value = viewModel.stack.size.toString()
    }

    //Zalecana nowa metoda przekazywania rezultatów pomiędzy aktywnościami nie respektuje
    //Trybu rozruchu "SingleInstance" zawartego w manifeście, dlatego zrobię mini-semafor
    val preferences = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
            result: ActivityResult ->
        run {
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data!!
                prefViewModel.setBackgroundColor(intent.getIntExtra("bgColor", prefViewModel.backgroundColor.value!!))
                prefViewModel.precision.value = intent.getIntExtra("precision", prefViewModel.precision.value!!)

                val roundContext = MathContext(prefViewModel.precision.value!!)
                for (i in 0 until viewModel.stack.size)
                    viewModel.stack[i] = viewModel.stack[i].round(roundContext)
                displayStack()
            }
            viewModel.prefOpen = false
        }
    }
    private val             viewModel       : MainViewModel             by viewModels()
    private val             prefViewModel   : PreferencesViewModel      by viewModels()
    private lateinit var    bind            : ActivityMainBinding
}