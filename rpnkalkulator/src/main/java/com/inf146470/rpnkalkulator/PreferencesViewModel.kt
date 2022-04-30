package com.inf146470.rpnkalkulator

import android.graphics.Color
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PreferencesViewModel: ViewModel(), Observable
{
    fun floatToDegree(f : Float) : Float
    {
        return ((1.0f + f) % 1.0f) * 360.0f
    }
    @Bindable
    val backgroundColor         = MutableLiveData(Color.HSVToColor(floatArrayOf(floatToDegree(0.0f),            0.0f, 1.0f)))
    @Bindable
    val textColor               = MutableLiveData(Color.HSVToColor(floatArrayOf(floatToDegree(0.0f),            0.0f, 0.0f)))
    @Bindable
    val buttonColor             = MutableLiveData(Color.HSVToColor(floatArrayOf(floatToDegree(0.25f),           0.4f, 0.15f + 1.0f/2.0f)))
    @Bindable
    val stackButtonColor        = MutableLiveData(Color.HSVToColor(floatArrayOf(floatToDegree(0.25f),           1.0f, 0.35f + 1.0f/2.0f)))
    @Bindable
    val mathButtonColor         = MutableLiveData(Color.HSVToColor(floatArrayOf(floatToDegree(0.32f),           0.8f, 0.3f  + 1.0f/2.0f)))
    @Bindable
    val stackRemoveButtonColor  = MutableLiveData(Color.HSVToColor(floatArrayOf(floatToDegree(-0.166f),         0.8f, 0.35f + 1.0f/2.0f)))
    @Bindable
    val charRemoveButton        = MutableLiveData(Color.HSVToColor(floatArrayOf(floatToDegree(0.166f),          0.9f, 0.25f + 1.0f/2.0f)))
    @Bindable
    val precision               = MutableLiveData(500)

    fun setBackgroundColor(color: Int)
    {
        backgroundColor.value = color
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        if (hsv[2] > 0.5f)
        {
            textColor.value                 = Color.HSVToColor(floatArrayOf(0.0f,                                   0.0f, 0.0f))
            buttonColor.value               = Color.HSVToColor(floatArrayOf(floatToDegree(hsv[0]/360.0f + 0.25f),   0.4f, 0.15f + hsv[2]/2.0f))
            stackButtonColor.value          = Color.HSVToColor(floatArrayOf(floatToDegree(hsv[0]/360.0f + 0.25f),   1.0f, 0.35f + hsv[2]/2.0f))
            mathButtonColor.value           = Color.HSVToColor(floatArrayOf(floatToDegree(hsv[0]/360.0f + 0.32f),   0.8f, 0.3f  + hsv[2]/2.0f))
            stackRemoveButtonColor.value    = Color.HSVToColor(floatArrayOf(floatToDegree(hsv[0]/360.0f - 0.166f),  0.9f, 0.35f + hsv[2]/2.0f))
            charRemoveButton.value          = Color.HSVToColor(floatArrayOf(floatToDegree(hsv[0]/360.0f + 0.166f),  0.8f, 0.25f + hsv[2]/2.0f))
        }
        else
        {
            textColor.value                 = Color.HSVToColor(floatArrayOf(0.0f,                                   0.0f, 1.0f))
            buttonColor.value               = Color.HSVToColor(floatArrayOf(floatToDegree(hsv[0]/360.0f + 0.25f),   0.4f, 0.65f + hsv[2]/4.0f))
            stackButtonColor.value          = Color.HSVToColor(floatArrayOf(floatToDegree(hsv[0]/360.0f + 0.25f),   1.0f, 0.6f  + hsv[2]/4.0f))
            mathButtonColor.value           = Color.HSVToColor(floatArrayOf(floatToDegree(hsv[0]/360.0f + 0.32f),   0.8f, 0.55f + hsv[2]/4.0f))
            stackRemoveButtonColor.value    = Color.HSVToColor(floatArrayOf(floatToDegree(hsv[0]/360.0f - 0.166f),  0.9f, 0.6f  + hsv[2]/4.0f))
            charRemoveButton.value          = Color.HSVToColor(floatArrayOf(floatToDegree(hsv[0]/360.0f + 0.166f),  0.8f, 0.5f  + hsv[2]/4.0f))
        }

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?)
    {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?)
    {
    }
}