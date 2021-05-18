package org.schabi.newpipe.views.smartlook

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.ViewSmartlookSettingsPreferenceSwitchViewBinding

class SmartlookSettingsPreferenceSwitchView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSmartlookSettingsPreferenceSwitchViewBinding.inflate(LayoutInflater.from(context), this, true)

    var isChecked: Boolean = false
        get() = binding.switchView.isChecked
        set(value) {
            field = value
            binding.switchView.isChecked = field
        }

    var onCheckChangedListener: CompoundButton.OnCheckedChangeListener? = null
        set(value) {
            field = value
            binding.switchView.setOnCheckedChangeListener(field)
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
        } finally {
            typedArray.recycle()
        }
    }
}
