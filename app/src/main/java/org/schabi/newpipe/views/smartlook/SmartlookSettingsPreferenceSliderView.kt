package org.schabi.newpipe.views.smartlook

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.ViewSmartlookSettingsPreferenceSliderViewBinding

class SmartlookSettingsPreferenceSliderView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSmartlookSettingsPreferenceSliderViewBinding.inflate(LayoutInflater.from(context), this, true)

    var progress: Int = 2
        set(value) {
            val normallizedValue = when {
                value < 2 -> 2
                value > 10 -> 10
                else -> value
            }
            field = normallizedValue
            binding.sliderView.progress = normallizedValue - 2
        }

    var value: String? = null
        get() = binding.valueTextView.text.toString()
        set(value) {
            field = value
            binding.valueTextView.text = value
        }

    var onProgressChangeListener: SeekBar.OnSeekBarChangeListener? = null
        set(value) {
            field = value
            binding.sliderView.setOnSeekBarChangeListener(field)
        }

    init {
        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.smartlook_PreferenceView,
                0,
                0
        )
        try {
            binding.titleTextView.text = typedArray.getString(R.styleable.smartlook_PreferenceView_sm_title)
            binding.valueTextView.text = typedArray.getString(R.styleable.smartlook_PreferenceView_sm_title)
        } finally {
            typedArray.recycle()
        }
    }
}
