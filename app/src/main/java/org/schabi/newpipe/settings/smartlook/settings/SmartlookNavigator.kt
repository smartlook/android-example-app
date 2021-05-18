package org.schabi.newpipe.settings.smartlook.settings

import androidx.fragment.app.Fragment

interface SmartlookNavigator {
    fun goTo(fragment: Fragment)
    fun goHome() {}
}
