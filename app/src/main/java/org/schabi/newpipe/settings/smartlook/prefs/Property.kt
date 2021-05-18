package org.schabi.newpipe.settings.smartlook.prefs

import java.io.Serializable

data class Property(
        val key: String,
        val value: String
) : Serializable