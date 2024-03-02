package com.skyd.anivu.ui.component.preference

import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.ListPreferenceDialogFragmentCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.skyd.anivu.R
import com.skyd.anivu.ui.component.dialog.InputDialogBuilder

abstract class MaterialPreferenceFragmentCompat : PreferenceFragmentCompat() {
    override fun onDisplayPreferenceDialog(preference: Preference) {
        when (preference) {
            is EditTextPreference -> {
                InputDialogBuilder(requireContext())
                    .setInitInputText(preference.text.orEmpty())
                    .setPositiveButton(preference.positiveButtonText) { _, _, text ->
                        preference.text = text
                    }
                    .setIcon(preference.dialogIcon)
                    .setTitle(preference.dialogTitle)
                    .setNegativeButton(preference.negativeButtonText) { _, _ -> }
                    .show()
            }

            is ListPreference -> {
                ListPreferenceDialogFragmentCompat.newInstance(preference.getKey())
            }

            else -> /*if (preference is MultiSelectListPreference) {
                MultiSelectListPreferenceDialogFragmentCompat.newInstance(preference.getKey())
            } else */ {
                throw IllegalArgumentException(
                    "Cannot display dialog for an unknown Preference type: "
                            + preference.javaClass.simpleName
                            + ". Make sure to implement onPreferenceDisplayDialog() to handle "
                            + "displaying a custom dialog for this Preference."
                )
            }
        }
    }
}