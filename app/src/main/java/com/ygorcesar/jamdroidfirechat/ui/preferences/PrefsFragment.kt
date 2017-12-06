package com.ygorcesar.jamdroidfirechat.ui.preferences

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceScreen
import android.support.annotation.StringRes
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.ui.BaseActivity
import com.ygorcesar.jamdroidfirechat.utils.Constants
import com.ygorcesar.jamdroidfirechat.utils.subscribeToGlobal
import com.ygorcesar.jamdroidfirechat.utils.unsubscribeFromGlobal
import com.ygorcesar.jamdroidfirechat.utils.userReference
import kotlinx.android.synthetic.main.dialog_fragment_version.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.browse
import org.jetbrains.anko.toast

class PrefsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen?, preference: Preference): Boolean {
        when (preference.key) {
            Constants.KEY_PREF_NOTIFICATION -> toggleNotificationStatus(preference)
            Constants.KEY_PREF_GITHUB -> browse(Constants.PROJECT_GITHUB_URL)
            Constants.KEY_PREF_ABOUT -> showAboutDialog()
            Constants.KEY_PREF_REVOKE_ACCESS -> buildDialog(R.string.dialog_title_revoke_access, { (activity as BaseActivity).revokeAccess() })
            Constants.KEY_PREF_DELETE_ACCOUNT -> buildDialog(R.string.dialog_title_delete_account, { deleteAccount() })
            Constants.KEY_PREF_EXIT -> buildDialog(R.string.dialog_title_logout, { (activity as BaseActivity).logout() })
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }

    private fun toggleNotificationStatus(preference: Preference) {
        val notificationStatus = preference.sharedPreferences.getBoolean(Constants.KEY_PREF_NOTIFICATION, true)
        preference.editor.putBoolean(Constants.KEY_PREF_NOTIFICATION, notificationStatus).commit()

        val statusId = if (notificationStatus) {
            FirebaseMessaging.getInstance().subscribeToGlobal()
            R.string.msg_notification_status_active
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromGlobal()
            R.string.msg_notification_status_disable
        }
        toast(getString(R.string.msg_notification_status_changed, getString(statusId)))
    }

    private fun showAboutDialog() {
        val dialog = Dialog(activity)
        dialog.apply {
            setContentView(R.layout.dialog_fragment_version)
            tv_version.text = String.format(getString(R.string.version), getString(R.string.pref_version))
            show()
        }
    }

    private fun buildDialog(@StringRes idRes: Int, onPositive: (DialogInterface) -> Unit) {
        alert {
            titleResource = idRes
            positiveButton(R.string.dialog_ok, onPositive)
            negativeButton(R.string.dialog_cancel, {})
            show()
        }
    }

    private fun deleteAccount() {
        val baseAct = activity
        if (baseAct is BaseActivity) {
            toast(R.string.msg_account_deleted)
            baseAct.revokeAccess()
            FirebaseDatabase.getInstance().userReference(baseAct.userEmail)
                    .removeValue()
        }
    }
}