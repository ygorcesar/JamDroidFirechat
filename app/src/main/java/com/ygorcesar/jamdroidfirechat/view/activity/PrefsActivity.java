package com.ygorcesar.jamdroidfirechat.view.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.onesignal.OneSignal;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;


public class PrefsActivity extends BaseActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeScreen();

        getFragmentManager().beginTransaction()
                .add(android.R.id.content, new PrefsFragment())
                .commit();
    }

    private void initializeScreen() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, final Preference preference) {
            switch (preference.getKey()) {
                case Constants.KEY_PREF_NOTIFICATION:
                    boolean notificationStatus = preference.getSharedPreferences()
                            .getBoolean(Constants.KEY_PREF_NOTIFICATION, true);

                    OneSignal.setSubscription(notificationStatus);
                    Toast.makeText(this.getActivity(),
                            getString(R.string.msg_notification_status_changed, notificationStatus ?
                                    getString(R.string.msg_notification_status_active) :
                                    getString(R.string.msg_notification_status_disable)),
                            Toast.LENGTH_SHORT).show();
                    break;
                case Constants.KEY_PREF_GITHUB:
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(Constants.PROJECT_GITHUB_URL));
                    startActivity(i);
                    break;
                case Constants.KEY_PREF_ABOUT:
                    Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_fragment_version);
                    dialog.show();
                    break;
                case Constants.KEY_PREF_REVOKE_ACCESS:
                    buildDialog(getString(R.string.dialog_title_revoke_access)
                            , new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    ((BaseActivity) getActivity()).revokeAccess();
                                }
                            });
                    break;
                case Constants.KEY_PREF_DELETE_ACCOUNT:
                    buildDialog(getString(R.string.dialog_title_delete_account),
                            new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    deleteFirebaseAccount(preference.getSharedPreferences()
                                            .getString(Constants.KEY_ENCODED_EMAIL, ""));
                                }
                            });
                    break;
                case Constants.KEY_PREF_EXIT:
                    buildDialog(getString(R.string.dialog_title_logout)
                            , new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    ((BaseActivity) getActivity()).logout();
                                }
                            });
                    break;
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        private void buildDialog(String title, DialogInterface.OnClickListener okListener) {
            new AlertDialog.Builder(getActivity(), R.style.AppTheme_AlertDialog)
                    .setTitle(title)
                    .setPositiveButton(getString(R.string.dialog_ok), okListener)
                    .setNegativeButton(getString(R.string.dialog_cancel), null).show();
        }

        private void deleteFirebaseAccount(final String encodedEmail) {
            Toast.makeText(PrefsFragment.this.getActivity(),
                    getString(R.string.msg_account_deleted),
                    Toast.LENGTH_SHORT).show();

            ((BaseActivity) getActivity()).revokeAccess();
            Firebase refUserAccount = new Firebase(ConstantsFirebase.FIREBASE_URL_USERS).child(encodedEmail);
            refUserAccount.removeValue(new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    new Firebase(ConstantsFirebase.FIREBASE_URL_USER_FRIENDS)
                            .child(encodedEmail).removeValue();
                }
            });
        }
    }
}
