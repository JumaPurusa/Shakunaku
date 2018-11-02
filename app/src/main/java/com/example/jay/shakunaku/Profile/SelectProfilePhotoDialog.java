package com.example.jay.shakunaku.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;

import com.example.jay.shakunaku.Listeners.OnRecyclerviewItemClickListener;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Timeline_Posting.GalleryActivity;

public class SelectProfilePhotoDialog extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private ProfilePhotoSelectorAdapter adapter;

    public SelectProfilePhotoDialog(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_photo_selector_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);

        adapter = new ProfilePhotoSelectorAdapter(getContext(), Action.actions);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        adapter.setOnRecyclerviewItemClickListener(
                new OnRecyclerviewItemClickListener() {
                    @Override
                    public void itemClick(View v, int position) {
                        Action action = Action.actions[position];
                        if(action.getPosition() == 0){
                            Intent galleryIntent = new Intent(getActivity(), GalleryActivity.class);
                            galleryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                            getActivity().startActivity(galleryIntent);
                        }

                        if(action.getPosition() == 1){
                            Intent cameraIntent = new Intent(getActivity(), ImageCropperActivity.class);
                            cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(cameraIntent);
                        }

                        getDialog().dismiss();
                    }
                }
        );
    }
}
