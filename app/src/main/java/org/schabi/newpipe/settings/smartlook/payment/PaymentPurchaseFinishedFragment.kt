package org.schabi.newpipe.settings.smartlook.payment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.smartlook.sdk.smartlook.Smartlook
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.FragmentPaymentPurchaseFinishedBinding
import org.schabi.newpipe.settings.smartlook.extensions.viewBinding
import org.schabi.newpipe.settings.smartlook.settings.SmartlookNavigator

class PaymentPurchaseFinishedFragment : Fragment(R.layout.fragment_payment_purchase_finished) {

    private val binding by viewBinding(FragmentPaymentPurchaseFinishedBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindViews()
        Smartlook.trackCustomEvent("payment_completed")
    }

    private fun FragmentPaymentPurchaseFinishedBinding.bindViews() {
        confirmButton.setOnClickListener {
            (activity as? SmartlookNavigator)?.goHome()
        }
    }

    companion object {
        fun newInstance(): PaymentPurchaseFinishedFragment {
            return PaymentPurchaseFinishedFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}