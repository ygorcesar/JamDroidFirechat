package com.ygorcesar.jamdroidfirechat.viewmodel;

import android.util.Log;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;

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
                    ConstantsFirebase.CHAT_GLOBAL_HELPER, "");
        } else {
            Firebase ref = new Firebase(ConstantsFirebase.FIREBASE_URL_USER_FRIENDS);
            ref.child(mLoggedUserEmail).child(user.getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                String chatKey = dataSnapshot.getValue().toString();
                                mContract.moveToMessagesFragment(chatKey, user.getName(), user.getOneSignalUserId());
                            } else {
                                mContract.moveToMessagesFragment(createChat(user.getEmail()),
                                        user.getName(), user.getOneSignalUserId());
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Log.d(TAG, "onCancelled: " + firebaseError.getMessage());
                        }
                    });
        }
    }

    /**
     * Cria key exclusiva para chat
     *
     * @param userEmail
     * @return
     */
    private String createChat(String userEmail) {
        Firebase ref = new Firebase(ConstantsFirebase.FIREBASE_URL_CHAT);
        ref = ref.push();

        makeFriends(mLoggedUserEmail, userEmail, ref.getKey());
        makeFriends(userEmail, mLoggedUserEmail, ref.getKey());
        return ref.getKey();
    }

    /**
     * Cria relação entre usuário atual e selecionado para chat, setando uma key exclusiva
     * de chat para ambos
     *
     * @param userEmail
     * @param userFriend
     * @param key
     */
    private void makeFriends(String userEmail, String userFriend, String key) {
        Firebase ref = new Firebase(ConstantsFirebase.FIREBASE_URL_USER_FRIENDS);
        ref.child(userEmail).child(userFriend).setValue(key);
    }
}
