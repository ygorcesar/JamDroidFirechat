package com.ygorcesar.jamdroidfirechat.view.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ygorcesar.jamdroidfirechat.BuildConfig;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.model.MapLocation;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.Singleton;
import com.ygorcesar.jamdroidfirechat.utils.Utils;
import com.ygorcesar.jamdroidfirechat.view.fragment.ChatsFragment;

import java.util.List;

public class MainActivity extends BaseActivity {
    private static final int REQUEST_INVITE = 100;
    private Toolbar mToolbar;
    private BottomSheetLayout mBottomSheet;
    private MapLocation mMapLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        String shareArgs = "";
        String extra = "";

        if (BuildConfig.DEBUG) {
            FirebaseMessaging.getInstance().subscribeToTopic("DEBUG");
        }

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                shareArgs = intent.getStringExtra(Intent.EXTRA_TEXT);
                extra = Intent.EXTRA_TEXT;

                mMapLocation = getMapLocationFromIntent(shareArgs);
                if (mMapLocation != null) {
                    extra = Constants.EXTRA_LOCATION;
                }
            }
            if (type.startsWith("image/")) {
                shareArgs = intent.getParcelableExtra(Intent.EXTRA_STREAM).toString();
                extra = Intent.EXTRA_STREAM;
            }
        }

        Singleton.getInstance().clearMessagesAndNumber();

        if (savedInstanceState == null) {
            initializeScreen(shareArgs, extra);
        }
    }

    private void initializeScreen(String shareArgs, String EXTRA) {
        ChatsFragment chatsFragment = new ChatsFragment();
        if (!shareArgs.isEmpty()) {
            Bundle args = new Bundle();
            args.putString(EXTRA, shareArgs);
            args.putString(Constants.KEY_SHARED_CONTENT, EXTRA);
            if (EXTRA.equals(Constants.EXTRA_LOCATION) && mMapLocation != null) {
                args.putString(Constants.KEY_SHARED_LATITUDE, mMapLocation.getLatitude());
                args.putString(Constants.KEY_SHARED_LONGITUDE, mMapLocation.getLongitude());
                mMapLocation = null;
            }
            chatsFragment.setArguments(args);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment, chatsFragment).commit();
    }

    @Override public void onBackPressed() {
        if (mBottomSheet == null || mBottomSheet.getState() == BottomSheetLayout.State.HIDDEN) {
            mBottomSheet = null;
            super.onBackPressed();
        } else {
            switch (mBottomSheet.getState()) {
                case EXPANDED:
                    mBottomSheet.peekSheet();
                    break;
                case PEEKED:
                    mBottomSheet.dismissSheet();
                    break;
            }
        }
    }

    @Override protected void onStart() {
        super.onStart();
        setUserOnline(true);
    }

    @Override protected void onPause() {
        super.onPause();
        setUserOnline(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_attach).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, PrefsActivity.class));
                break;
            case R.id.action_invite:
                Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setCallToActionText(getString(R.string.invitation_call_action))
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setHomeButtonVisible(boolean visible) {
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(visible);
    }

    public void setToolbarVisibility(int VISIBILITY) {
        boolean visible = VISIBILITY == View.VISIBLE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (visible) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        mToolbar.setVisibility(VISIBILITY);
        if (visible) {
            Utils.animateScaleXY(mToolbar, 0, 600);
        }
    }

    public void setToolbarMenuClickListener(View.OnClickListener toolbarClickListener) {
        mToolbar.setNavigationOnClickListener(toolbarClickListener);
    }

    public void setBottomSheet(BottomSheetLayout bottomSheet) {
        mBottomSheet = bottomSheet;
    }

    private MapLocation getMapLocationFromIntent(String addrs) {
        MapLocation mapLocation = null;
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        try {
            address = coder.getFromLocationName(addrs.substring(0, addrs.indexOf("\n")), 5);
            if (address.size() > 0) {
                Address location = address.get(0);
                mapLocation = new MapLocation(String.valueOf(location.getLatitude())
                        , String.valueOf(location.getLongitude()));
                location.getLatitude();
                location.getLongitude();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapLocation;
    }

    private void setUserOnline(final boolean online) {
        final DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference(ConstantsFirebase.FIREBASE_LOCATION_USERS)
                .child(mEncodedEmail);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userRef.child(ConstantsFirebase.FIREBASE_PROPERTY_ONLINE)
                            .setValue(online);
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
