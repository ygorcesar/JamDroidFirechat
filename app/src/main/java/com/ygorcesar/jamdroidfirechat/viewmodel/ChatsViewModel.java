package com.ygorcesar.jamdroidfirechat.viewmodel;

import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.Utils;

public class ChatsViewModel extends BaseChatViewModel {

    private ChatsViewModelContract mContract;
    private static final String TAG = "ChatsViewModel";

    public ChatsViewModel(User user, String loggedUserEmail, ChatsViewModelContract contract) {
        super(user, loggedUserEmail);
        mContract = contract;
    }

    public void onItemClick(View view) {
        getChatKey(mUser);
    }

    /**
     * Busca key do chat, caso não encontre gera uma nova key e seta para ambos usuários da conversa
     *
     * @param user
     */
    public void getChatKey(final User user) {
        if (user.getEmail().equals(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL)) {
            mContract.moveToMessagesFragment(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL,
                    ConstantsFirebase.CHAT_GLOBAL_HELPER, ConstantsFirebase.FIREBASE_TOPIC_CHAT_GLOBAL_TO);
        } else {
            FirebaseDatabase.getInstance()
                    .getReference(ConstantsFirebase.FIREBASE_LOCATION_USER_FRIENDS)
                    .child(mLoggedUserEmail)
                    .child(user.getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                String chatKey = dataSnapshot.getValue().toString();
                                mContract.moveToMessagesFragment(chatKey, user.getName(), user.getFcmUserDeviceId());
                            } else {
                                mContract.moveToMessagesFragment(Utils.createChat(mLoggedUserEmail, user.getEmail()),
                                        user.getName(), user.getFcmUserDeviceId());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                        }
                    });
        }
    }
}
