package org.schabi.newpipe.settings.smartlook.payment

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.smartlook.sdk.smartlook.Smartlook
import kotlinx.android.synthetic.main.fragment_payment_login.*
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.FragmentPaymentPurchaseBinding
import org.schabi.newpipe.settings.smartlook.extensions.viewBinding
import org.schabi.newpipe.settings.smartlook.onboarding.SmartlookOnboardingFirstFragment
import org.schabi.newpipe.settings.smartlook.prefs.SmartlookSettingsRepository
import org.schabi.newpipe.settings.smartlook.settings.SmartlookNavigator

class PaymentPurchaseFragment : Fragment(R.layout.fragment_payment_purchase) {

    private val binding by viewBinding(FragmentPaymentPurchaseBinding::bind)
    private var dialog: AlertDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindViews()
    }

    private fun FragmentPaymentPurchaseBinding.bindViews() {
        confirmButton.setOnClickListener {
            Smartlook.trackCustomEvent("plan_purchase_begin")
            checkLogin()
        }
        standardRadioButton.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                confirmButton.isEnabled = true
                if (premiumRadioButton.isChecked) {
                    premiumRadioButton.isChecked = false
                }
            }
        }
        premiumRadioButton.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                confirmButton.isEnabled = true
                if (standardRadioButton.isChecked) {
                    standardRadioButton.isChecked = false
                }
            }
        }
        confirmButton.isEnabled = false
    }

    private fun checkLogin() {
        when (user()) {
            "user3" -> {
                dialog?.dismiss()
                dialog = AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage("Not enough funds!")
                        .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                            Smartlook.trackCustomEvent("plan_purchase_error", "error", "not_enough_funds")
                            activity?.finish()
                        }
                        .create()

                dialog?.show()
            }
            else -> {
                Smartlook.trackCustomEvent("plan_purchase_success", Bundle().apply {
                    putString("name", if (binding.standardRadioButton.isChecked) "standard" else "premium")
                })
                SmartlookSettingsRepository.userLogin = user()
                (activity as? SmartlookNavigator)?.goTo(PaymentPurchaseFinishedFragment.newInstance())
            }
        }
    }

    private fun user() = arguments?.getString(USER)

    companion object {
        private const val USER = "USER"
        fun newInstance(user: String): PaymentPurchaseFragment {
            return PaymentPurchaseFragment().apply {
                arguments = Bundle().apply {
                    putString(USER, user)
                }
            }
        }
    }
}