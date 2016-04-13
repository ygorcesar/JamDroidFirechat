package com.ygorcesar.jamdroidfirechat.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.adapters.ChatsItemAdapter;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.OnRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsFragment extends Fragment implements OnRecyclerItemClickListener {
    private RecyclerView mRvChats;
    private List<User> mUsers;
    private ValueEventListener mValueUserListener;
    private String mEncodedMail;
    private String mChatKey;
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);
        mRvChats = (RecyclerView) rootView.findViewById(R.id.rv_chats);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEncodedMail = prefs.getString(Constants.KEY_ENCODED_EMAIL, "");

        initializeScreen();
        initializeFirebase();
        return rootView;
    }

    private void initializeScreen() {
        mUsers = new ArrayList<>();
        User userGeral = new User(getString(R.string.chat_global),
                ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL,
                ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL, null);
        mUsers.add(userGeral);

        ChatsItemAdapter adapter = new ChatsItemAdapter(getActivity(), mUsers);
        adapter.setOnRecyclerItemClickListener(this);
        mRvChats.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvChats.setAdapter(adapter);
        mRvChats.setHasFixedSize(true);
    }

    private void initializeFirebase() {
        if (mValueUserListener == null) {
            mValueUserListener = createUserValueListener();
        }
        Firebase mRefUsers = new Firebase(ConstantsFirebase.FIREBASE_URL_USERS);
        mRefUsers.addValueEventListener(mValueUserListener);
    }

    private ValueEventListener createUserValueListener() {
        return new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getValue(User.class).getEmail().equals(mEncodedMail)) {
                        mUsers.add(snapshot.getValue(User.class));
                        mRvChats.getAdapter().notifyItemInserted(mUsers.size() - 1);
                    }
                }
            }

            @Override public void onCancelled(FirebaseError firebaseError) {

            }
        };
    }

    @Override public void onRecycleItemClick(int view_id, int position) {
        getChatKey(position);
    }

    /**
     * Move para o fragment do chat para realizar troca de mensagens
     *
     * @param chatKey
     * @param friendName
     */
    private void moveToChatFragment(String chatKey, String friendName) {
        Bundle args = new Bundle();
        args.putString(Constants.KEY_CHAT_CHILD, chatKey);
        args.putString(Constants.KEY_USER_DISPLAY_NAME, friendName);
        MessagesFragment fragment = new MessagesFragment();
        fragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment).addToBackStack(null).commit();
    }

    /**
     * Busca key do chat, caso não encontre gera uma nova key e seta para ambos usuários da conversa
     *
     * @param position
     */
    private void getChatKey(int position) {
        if (mUsers.get(position).getEmail().equals(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL)) {
            moveToChatFragment(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL, getString(R.string.chat_global));
        } else {
            final String friendEmail = mUsers.get(position).getEmail();
            final String friendName = mUsers.get(position).getName();
            Firebase ref = new Firebase(ConstantsFirebase.FIREBASE_URL_USER_FRIENDS);
            ref.child(mEncodedMail).child(friendEmail)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                mChatKey = dataSnapshot.getValue().toString();
                                moveToChatFragment(mChatKey, friendName);
                            } else {
                                moveToChatFragment(createChat(friendEmail), friendName);
                            }
                        }

                        @Override public void onCancelled(FirebaseError firebaseError) {
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

        makeFriends(mEncodedMail, userEmail, ref.getKey());
        makeFriends(userEmail, mEncodedMail, ref.getKey());
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
