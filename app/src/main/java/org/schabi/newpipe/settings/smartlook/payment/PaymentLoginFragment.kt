package org.schabi.newpipe.settings.smartlook.payment

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.smartlook.sdk.smartlook.Smartlook
import kotlinx.android.synthetic.main.fragment_payment_login.*
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.FragmentPaymentLoginBinding
import org.schabi.newpipe.settings.smartlook.extensions.viewBinding
import org.schabi.newpipe.settings.smartlook.settings.SmartlookNavigator

class PaymentLoginFragment : Fragment(R.layout.fragment_payment_login) {

    private val binding by viewBinding(FragmentPaymentLoginBinding::bind)

    private var dialog: AlertDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindViews()
    }

    private fun FragmentPaymentLoginBinding.bindViews() {
        Smartlook.registerBlacklistedViews(listOf(userNameInputLayout, passwordInputLayout))

        loginButton.setOnClickListener {
            Smartlook.trackCustomEvent("login_request_begin")
            checkLogin()
        }
        loginButton.isEnabled = false
        passwordEditText.doAfterTextChanged {
            check()
        }
        userNameEditText.doAfterTextChanged {
            check()
        }
    }

    private fun checkLogin() {
        when (userNameEditText.text.toString()) {
            "user2" -> {
                dialog?.dismiss()
                dialog = AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage("Account is not activated")
                        .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                            Smartlook.trackCustomEvent("login_error", "error", "account_not_activated")
                            activity?.finish()
                        }
                        .create()

                dialog?.show()
            }
            else -> {
                Smartlook.trackCustomEvent("login_success")
                goToPayment()
            }
        }
    }

    private fun goToPayment() {
        (activity as? SmartlookNavigator)?.goTo(PaymentPurchaseFragment.newInstance(userNameEditText.text.toString()))
    }

    private fun check() {
        loginButton.isEnabled = !(binding.passwordEditText.text.isNullOrEmpty() || binding.userNameEditText.text.isNullOrEmpty())
    }

    companion object {
        fun newInstance(): PaymentLoginFragment {
            return PaymentLoginFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}