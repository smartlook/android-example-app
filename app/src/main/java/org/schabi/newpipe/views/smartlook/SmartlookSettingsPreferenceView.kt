package org.schabi.newpipe.views.smartlook

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.ViewSmartlookSettingsPreferenceViewBinding

class SmartlookSettingsPreferenceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSmartlookSettingsPreferenceViewBinding.inflate(LayoutInflater.from(context), this, true)

    var value: String? = "Empty"
        get() = binding.valueTextView.text.toString()
        set(value) {
            field = value
            binding.valueTextView.text = value
        }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.rootView.setOnClickListener(l)
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
            binding.valueTextView.text = typedArray.getString(R.styleable.smartlook_PreferenceView_sm_value)
            binding.valueTextView.isVisible = typedArray.getBoolean(R.styleable.smartlook_PreferenceView_sm_hasValue, true)
        } finally {
            typedArray.recycle()
        }
    }
}
