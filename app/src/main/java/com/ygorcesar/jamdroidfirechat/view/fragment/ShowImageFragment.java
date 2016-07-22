package com.ygorcesar.jamdroidfirechat.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.greysonparrelli.permiso.Permiso;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.databinding.FragmentShowImageBinding;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.view.activity.MainActivity;

public class ShowImageFragment extends Fragment {

    private String url = "";

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentShowImageBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_show_image, container, false);

        ((MainActivity)getActivity()).setToolbarVisibility(View.GONE);

        binding.flProgressBar.setVisibility(View.VISIBLE);
        Bundle args = getArguments();
        if (args != null) {
            url = args.getString(Constants.KEY_IMAGE_URL);
        }
        if (url != null && !url.isEmpty()) {
            Glide.with(getActivity())
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            binding.flProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(binding.ivImage);
        }

        binding.fabShare.setImageResource(R.drawable.ic_share);
        binding.fabShare.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                            @Override
                            public void onPermissionResult(Permiso.ResultSet resultSet) {
                                if (resultSet.areAllPermissionsGranted()) {
                                    try {
                                        binding.flProgressBar.setVisibility(View.VISIBLE);
                                        Glide.with(getActivity())
                                                .load(url)
                                                .asBitmap()
                                                .into(new SimpleTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(final Bitmap bmp, GlideAnimation glideAnimation) {
                                                        initializeShareIntent(bmp, binding.flProgressBar);
                                                    }
                                                });
                                    } catch (Exception e) {
                                        binding.flProgressBar.setVisibility(View.GONE);
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(ShowImageFragment.this.getActivity(),
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
                }
        );

        return binding.getRoot();
    }

    private void initializeShareIntent(Bitmap bmp, FrameLayout flProgress) {
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                bmp, "title", "description");
        Intent shareImageIntent = new Intent();
        shareImageIntent.setAction(Intent.ACTION_SEND);
        shareImageIntent.setType("image/*");
        shareImageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        flProgress.setVisibility(View.GONE);
        startActivity(Intent.createChooser(shareImageIntent, "Compartilhe a imagem"));
    }
}
