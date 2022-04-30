package com.inf146470.rpnkalkulator

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import com.inf146470.rpnkalkulator.databinding.ActivityPreferencesBinding
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener

class PreferencesActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        bind = DataBindingUtil.setContentView(this, R.layout.activity_preferences)
        bind.preferencesViewModel = prefViewModel
        bind.lifecycleOwner = this
        super.onCreate(savedInstanceState)
        if (intent.hasExtra("bgColor"))
        {
            val bgColor = intent.getIntExtra("bgColor", 0)
            intent.removeExtra("bgColor")

            prefViewModel.backgroundColor.value = bgColor
            prefViewModel.setBackgroundColor(bgColor)
            bind.main.setBackgroundColor(bgColor)
            bind.backgroundColorPicker.setColor(bgColor)
            bind.main.postInvalidate()
        }
        if (intent.hasExtra("precision"))
        {
            val precision = intent.getIntExtra("precision", 500)
            intent.removeExtra("precision")
            prefViewModel.precision.value       = precision
            bind.precisionEdit.setText(precision.toString())
        }
        bind.backgroundColorPicker.setColorSelectionListener(object : SimpleColorSelectionListener()
        {
            override fun onColorSelected(color: Int)
            {
                prefViewModel.setBackgroundColor(color)
            }
        })
    }
    override fun finish()
    {
        val intent = Intent(
            this,
            MainActivity::class.java)
        intent.putExtra("bgColor",      prefViewModel.backgroundColor.value)
        if (bind.precisionEdit.text.toString().isEmpty())
            intent.putExtra("precision", 0)
        else
            intent.putExtra("precision", bind.precisionEdit.text.toString().toInt())
        setResult(RESULT_OK, intent)
        super.finish()
    }

    //Po tym jak wreszcie udało mi się ogarnąć kolorowanie NumberPicker, okazało się, że po zmianie koloru
    //NumberPicker crashuje gdy osiągnie minumum
    //Goodbye NumberPicker
    /*fun setNumberPickerTextColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            bind.precisionPicker.textColor = prefViewModel.textColor.value!!

        }
        else
        {
            val count = bind.precisionPicker.childCount
            for (i in 0 until count) {
                val child = bind.precisionPicker.getChildAt(i)
                if (child is EditText) {
                    try {
                        val selectorWheelPaintField =
                            bind.precisionPicker.javaClass.getDeclaredField("mSelectorWheelPaint")
                        selectorWheelPaintField.isAccessible = true
                        (selectorWheelPaintField[bind.precisionPicker] as Paint).color =
                            prefViewModel.textColor.value!!
                        child.setTextColor(prefViewModel.textColor.value!!)
                        bind.precisionPicker.invalidate()
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }*/

    private val             prefViewModel   : PreferencesViewModel      by viewModels()
    private lateinit var    bind            : ActivityPreferencesBinding
}