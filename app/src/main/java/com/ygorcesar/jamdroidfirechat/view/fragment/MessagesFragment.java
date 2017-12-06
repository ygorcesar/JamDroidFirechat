package com.ygorcesar.jamdroidfirechat.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.flipboard.bottomsheet.commons.ImagePickerSheetView;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.greysonparrelli.permiso.Permiso;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.databinding.AdapterItemMessageBinding;
import com.ygorcesar.jamdroidfirechat.databinding.FragmentMessagesBinding;
import com.ygorcesar.jamdroidfirechat.databinding.FragmentShowImageBinding;
import com.ygorcesar.jamdroidfirechat.model.MapLocation;
import com.ygorcesar.jamdroidfirechat.model.Message;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.Utils;
import com.ygorcesar.jamdroidfirechat.view.activity.MainActivity;
import com.ygorcesar.jamdroidfirechat.viewmodel.MessageAdapterViewModel;
import com.ygorcesar.jamdroidfirechat.viewmodel.MessageAdapterViewModelContract;
import com.ygorcesar.jamdroidfirechat.viewmodel.MessageFragmViewModel;
import com.ygorcesar.jamdroidfirechat.viewmodel.MessageFragmViewModelContract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;

public class MessagesFragment extends Fragment implements MessageFragmViewModelContract,
        MessageAdapterViewModelContract {

    private static final int DIALOG_SEND_IMAGE = 100;
    private List<User> mUsers;
    private List<String> mUsersEmails;
    private DatabaseReference mRefUsers;
    private ValueEventListener valueUserListener;
    private String mChildChatKey;
    private String mFcmUserDeviceId;
    private FragmentMessagesBinding mFragmentMessagesBinding;
    private FirebaseRecyclerAdapter<Message, MessageItemHolder> mAdapter;
    private static final String TAG = "MessagesFragment";
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_IMAGE = 2;
    private static final int REQUEST_SELECT_LOCATION = 3;
    private ProgressDialog mProgressUpload;
    private String imageUri;
    private static String encodedMail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentMessagesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_messages, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        encodedMail = prefs.getString(Constants.KEY_ENCODED_EMAIL, "");

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mChildChatKey = getArguments().getString(Constants.KEY_CHAT_CHILD, "");
            mFcmUserDeviceId = getArguments().getString(Constants.KEY_USER_FCM_DEVICE_ID, "");

            initializeScreen();

            String CONTENT_TYPE = getArguments().getString(Constants.KEY_SHARED_CONTENT, "");
            getArguments().putString(Constants.KEY_SHARED_CONTENT, "");
            if (!CONTENT_TYPE.isEmpty()) {
                String[] args = Utils.getSharedType(CONTENT_TYPE, getArguments());
                String sharedArgs = args[0];

                switch (args[Constants.ARGS_POS_TYPE]) {
                    case Intent.EXTRA_TEXT:
                        mFragmentMessagesBinding.edtMessageContent.setText(sharedArgs);
                        break;
                    case Intent.EXTRA_STREAM:
                        showSendImageFragment(sharedArgs);
                        break;
                    case Constants.EXTRA_LOCATION:
                        mFragmentMessagesBinding.getMessageViewModel()
                                .sendLocationMessage(args[Constants.ARGS_POS_LATITUDE]
                                        , args[Constants.ARGS_POS_LONGITUDE]);
                        break;
                }
            }

            ((MainActivity) getActivity()).setToolbarVisibility(View.VISIBLE);
            if (mFcmUserDeviceId.equals(ConstantsFirebase.FIREBASE_TOPIC_CHAT_GLOBAL_TO)) {
                getActivity().setTitle(ConstantsFirebase.CHAT_GLOBAL_HELPER);
            } else {
                getActivity().setTitle(getArguments()
                        .getString(Constants.KEY_USER_DISPLAY_NAME, getString(R.string.app_name)));
            }
        }
        return mFragmentMessagesBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeFirebase();

        ((MainActivity) getActivity()).setBottomSheet(mFragmentMessagesBinding.bottomsheet);

        Utils.animateScaleXY(mFragmentMessagesBinding.edtMessageContent, 300, 400);
        Utils.animateScaleXY(mFragmentMessagesBinding.btnSendMessage, 500, 400);

        mFragmentMessagesBinding.flContainerToHide.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View view, MotionEvent motionEvent) {
                hideMediaMenu();
                return false;
            }
        });
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_attach).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_attach:
                showHideMediaMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHideMediaMenu() {
        if (mFragmentMessagesBinding.lnMenuItems.getVisibility() == View.VISIBLE) {
            hideMediaMenu();
        } else {
            showMediaMenu();
        }
    }

    private void hideMediaMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utils.exitCircularReveal(mFragmentMessagesBinding.lnMenuItems);
        } else {
            Utils.animateFadeOut(mFragmentMessagesBinding.lnMenuItems, 0, 500);
        }
    }

    private void showMediaMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utils.enterCircularReveal(mFragmentMessagesBinding.lnMenuItems);
        } else {
            Utils.animateFadeIn(mFragmentMessagesBinding.lnMenuItems, 0, 500);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        removeFirebaseListeners();
        clearLists();
        mAdapter.cleanup();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    if (!imageUri.isEmpty()) {
                        showSendImageFragment(imageUri);
                        imageUri = "";
                    }
                    break;
                case REQUEST_SELECT_IMAGE:
                    showSendImageFragment(data.getData().toString());
                    break;
                case REQUEST_SELECT_LOCATION:
                    Place place = PlacePicker.getPlace(data, getActivity());
                    mFragmentMessagesBinding.getMessageViewModel()
                            .sendLocationMessage(String.valueOf(place.getLatLng().latitude),
                                    String.valueOf(place.getLatLng().longitude));
                    break;
                case DIALOG_SEND_IMAGE:
                    byte[] bytes = data.getExtras().getByteArray(Constants.KEY_IMAGE_BYTES);
                    if (bytes != null) {
                        mProgressUpload.show();
                        mFragmentMessagesBinding.getMessageViewModel().uploadImageToFirebase(bytes);
                    }
                    break;
            }
        }
    }

    /**
     * Inicializando Adapters, RecyclerView e Listeners...
     */
    private void initializeScreen() {
        setupToolbar();

        mProgressUpload = new ProgressDialog(getActivity());
        mProgressUpload.setMessage(getString(R.string.progress_dialog_uploading));

        mUsers = new ArrayList<>();
        mUsersEmails = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String loggedUserName = preferences.getString(Constants.KEY_USER_DISPLAY_NAME, "");

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setStackFromEnd(true);
        mFragmentMessagesBinding.rvMessage.setHasFixedSize(true);
        mFragmentMessagesBinding.rvMessage.setLayoutManager(llm);

        mFragmentMessagesBinding.setMessageViewModel(new MessageFragmViewModel(this, encodedMail,
                mChildChatKey, mFcmUserDeviceId, loggedUserName));

        EmojIconActions emojIcon = new EmojIconActions(getActivity(), mFragmentMessagesBinding.getRoot(),
                mFragmentMessagesBinding.edtMessageContent, mFragmentMessagesBinding.ivEmoji);
        emojIcon.ShowEmojIcon();
    }

    private void setupToolbar() {
        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setHomeButtonVisible(true);
            activity.setToolbarMenuClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().popBackStack();
                }
            });
        }
    }

    /**
     * Inicializando Firebase e Listeners do Firebase
     */
    private void initializeFirebase() {
        if (valueUserListener == null) {
            valueUserListener = createFirebaseUsersListeners();
        }
        mRefUsers = FirebaseDatabase.getInstance().getReference(ConstantsFirebase.FIREBASE_LOCATION_USERS);
        mRefUsers.keepSynced(true);
        mRefUsers.addValueEventListener(valueUserListener);

        Query messagesRef = FirebaseDatabase.getInstance()
                .getReference(ConstantsFirebase.FIREBASE_LOCATION_CHAT)
                .child(mChildChatKey)
                .orderByKey().limitToLast(50);
        messagesRef.keepSynced(true);
        attachMessagesToRecyclerView(messagesRef);
    }

    private void attachMessagesToRecyclerView(Query messagesReference) {
        mAdapter = new FirebaseRecyclerAdapter<Message, MessageItemHolder>(Message.class,
                R.layout.adapter_item_message, MessageItemHolder.class, messagesReference) {
            @Override
            public MessageItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                AdapterItemMessageBinding adapterItemMessageBinding = DataBindingUtil.inflate(LayoutInflater
                        .from(parent.getContext()), viewType, parent, false);
                return new MessageItemHolder(adapterItemMessageBinding);
            }

            @Override
            protected void populateViewHolder(MessageItemHolder viewHolder, Message message, int position) {
                int index = mUsersEmails.indexOf(message.getEmail());
                if (index != -1) {
                    viewHolder.bindMessage(mUsers.get(index), message, MessagesFragment.this);
                }
            }
        };

        mFragmentMessagesBinding.rvMessage.setAdapter(mAdapter);
        mFragmentMessagesBinding.rvMessage.getAdapter().registerAdapterDataObserver(
                new RecyclerView.AdapterDataObserver() {
                    @Override public void onItemRangeInserted(int position, int itemCount) {
                        super.onItemRangeInserted(position, itemCount);
                        mFragmentMessagesBinding.rvMessage.scrollToPosition(position);
                    }
                });
    }

    private ValueEventListener createFirebaseUsersListeners() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mUsers.add(snapshot.getValue(User.class));
                        mUsersEmails.add(snapshot.getValue(User.class).getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    /**
     * Remove listeners dos objetos Firebase
     */
    private void removeFirebaseListeners() {
        if (valueUserListener != null) {
            mRefUsers.removeEventListener(valueUserListener);
        }
    }

    private void clearLists() {
        mUsers.clear();
        mUsersEmails.clear();
    }

    /**
     * Resgata click do adapter por meio da interface implementada para exibir usuário da mensagem
     *
     * @param user
     */
    @Override
    public void onMessageItemClick(User user, int viewId) {
        FragmentManager fragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_CHAT_KEY, mChildChatKey);
        args.putString(Constants.KEY_USER_FCM_DEVICE_ID, user.getFcmUserDeviceId());
        args.putString(Constants.KEY_USER_DISPLAY_NAME, user.getName());
        args.putString(Constants.KEY_ENCODED_EMAIL, Utils.decodeEmail(user.getEmail()));
        args.putString(Constants.KEY_USER_PROVIDER_PHOTO_URL, user.getPhotoUrl());
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        fragment.show(fragmentManager, "dialog_fragment_user");
    }

    @Override public void onMessageItemClick(String uri, View view) {
        Bundle args = new Bundle();
        args.putString(Constants.KEY_IMAGE_URL, uri);

        ShowImageFragment fragment = new ShowImageFragment();
        fragment.setArguments(args);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition slideTop = TransitionInflater.from(getActivity()).
                    inflateTransition(android.R.transition.slide_top);
            Transition slideBottom = TransitionInflater.from(getActivity()).
                    inflateTransition(android.R.transition.slide_bottom);

            setSharedElementReturnTransition(slideTop);
            setExitTransition(slideBottom);

            fragment.setSharedElementEnterTransition(slideTop);
            fragment.setEnterTransition(slideBottom);

            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .addToBackStack("transaction")
                    .addSharedElement(view, "shared")
                    .commit();
        } else {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override public void onMessageLocationClick(MapLocation mapLocation) {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(String.format(/*"google.navigation:q=%s,%s"*/
                        getString(R.string.map_location_intent)
                        , mapLocation.getLatitude()
                        , mapLocation.getLongitude()
                        , mapLocation.getLatitude()
                        , mapLocation.getLongitude())));
        startActivity(mapIntent);
    }

    @Override
    public void setEditTextMessage(String msg) {
        mFragmentMessagesBinding.edtMessageContent.setText(msg);
    }

    @Override
    public void showToastMessage(int string_res) {
        Toast.makeText(getActivity(), getString(string_res), Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getEditTextMessage() {
        return mFragmentMessagesBinding.edtMessageContent.getText().toString();
    }

    @Override public void uploadTask(boolean success) {
        mProgressUpload.dismiss();
        String msg = success ? getString(R.string.msg_info_send_success) : getString(R.string.msg_info_send_failed);
        Toast.makeText(MessagesFragment.this.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override public void actionMenuItemCamera() {
        hideMediaMenu();
        initializeCameraIntent();
    }

    @Override public void actionMenuItemGallery() {
        hideMediaMenu();
        initializeGalleryPickerIntent();
    }

    @Override public void actionMenuItemLocation() {
        hideMediaMenu();
        initializeMapIntent();
    }

    @Override public void showImagePicker() {
        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {
                    ImagePickerSheetView imagePickerSheetView = new ImagePickerSheetView.Builder(getActivity())
                            .setMaxItems(30)
                            .setShowCameraOption(true)
                            .setShowPickerOption(true)
                            .setTitle(getString(R.string.dialog_title_select_image))
                            .setOnTileSelectedListener(new ImagePickerSheetView.OnTileSelectedListener() {
                                @Override
                                public void onTileSelected(ImagePickerSheetView.ImagePickerTile selectedTile) {
                                    if (selectedTile.isCameraTile()) {
                                        initializeCameraIntent();
                                    } else if (selectedTile.isPickerTile()) {
                                        initializeGalleryPickerIntent();
                                    } else if (selectedTile.isImageTile()) {
                                        if (selectedTile.getImageUri() != null) {
                                            showSendImageFragment(selectedTile.getImageUri().toString());
                                        }
                                    }
                                    mFragmentMessagesBinding.bottomsheet.dismissSheet();
                                }
                            })
                            .setImageProvider(new ImagePickerSheetView.ImageProvider() {
                                @Override
                                public void onProvideImage(final ImageView imageView, final Uri imageUri, int size) {
                                    Glide.with(getActivity())
                                            .load(imageUri)
                                            /*.centerCrop()
                                            .crossFade()*/
                                            .into(imageView);
                                }
                            })
                            .create();
                    mFragmentMessagesBinding.bottomsheet.showWithSheetView(imagePickerSheetView);
                } else {
                    Toast.makeText(MessagesFragment.this.getActivity(),
                            getString(R.string.msg_permission_required), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                Permiso.getInstance().showRationaleInDialog(null,
                        getString(R.string.msg_permission_required),
                        null, callback);
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void initializeGalleryPickerIntent() {

        Intent selectImageIntent = new Intent();
        selectImageIntent.setType("image/*");
        selectImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(selectImageIntent, REQUEST_SELECT_IMAGE);
    }

    private void initializeCameraIntent() {
        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override
            public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Log.e(TAG, "onTileSelected: ", ex);
                    }
                    if (photoFile != null) {
                        Uri uri = Uri.fromFile(photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        imageUri = uri.toString();
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } else {
                    Toast.makeText(MessagesFragment.this.getActivity(),
                            getString(R.string.msg_permission_required), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                Permiso.getInstance().showRationaleInDialog(null,
                        getString(R.string.msg_permission_required),
                        null, callback);
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void initializeMapIntent() {
        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(getActivity()), REQUEST_SELECT_LOCATION);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MessagesFragment.this.getActivity(),
                            getString(R.string.msg_permission_required), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                Permiso.getInstance().showRationaleInDialog(null,
                        getString(R.string.msg_permission_required),
                        null, callback);
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    protected File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void showSendImageFragment(String uri) {
        FragmentManager fragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_IMAGE_URL, uri);

        SendImageFragment fragment = new SendImageFragment();
        fragment.setArguments(args);
        fragment.setArguments(args);
        fragment.setTargetFragment(this, DIALOG_SEND_IMAGE);
        fragment.show(fragmentManager, "dialog_send_image");
    }

    public static class SendImageFragment extends DialogFragment {
        private String url;

        @Nullable @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            final FragmentShowImageBinding binding = DataBindingUtil.inflate(inflater,
                    R.layout.fragment_show_image, container, false);

            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Bundle args = getArguments();
            if (args != null) {
                url = args.getString(Constants.KEY_IMAGE_URL);
                if (url != null) {
                  /*  Glide.with(getActivity())
                            .load(url)
                            .asBitmap()
                            .fitCenter()
                            .into(new SimpleTarget<Bitmap>(1360, 1360) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    binding.ivImage.setImageBitmap(resource);
                                }
                            });*/
                }
            }

            binding.fabShare.setImageResource(R.drawable.ic_send);
            binding.fabShare.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    try {
                        getActivity().getIntent().putExtra(Constants.KEY_IMAGE_BYTES,
                                getBytesFromImageView(url));

                        getTargetFragment().onActivityResult(getTargetRequestCode(),
                                Activity.RESULT_OK, getActivity().getIntent());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dismiss();
                }
            });

            return binding.getRoot();
        }

        private byte[] getBytesFromImageView(String uri) throws IOException {
            int quality = 60;
            Bitmap bitmap = Utils.getBitmapFromUri(getActivity(), uri);
            if (bitmap.getWidth() > 1280 || bitmap.getHeight() > 1280) {
                bitmap = Utils.getScaledBitmap(bitmap, 1280);
                quality = 45;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            return baos.toByteArray();
        }
    }

    public static class MessageItemHolder extends RecyclerView.ViewHolder {
        private AdapterItemMessageBinding mAdapterItemMessageBinding;

        public MessageItemHolder(AdapterItemMessageBinding adapterItemMessageBinding) {
            super(adapterItemMessageBinding.rvContainer);
            mAdapterItemMessageBinding = adapterItemMessageBinding;

        }

        public void bindMessage(User user, Message message, MessageAdapterViewModelContract contract) {
            if (mAdapterItemMessageBinding.getMessageViewModel() == null) {
                mAdapterItemMessageBinding.setMessageViewModel(new MessageAdapterViewModel(user,
                        encodedMail, message, contract));
            } else {
                mAdapterItemMessageBinding.getMessageViewModel().setUser(user);
                mAdapterItemMessageBinding.getMessageViewModel().setMessage(message);
            }
        }
    }
}
