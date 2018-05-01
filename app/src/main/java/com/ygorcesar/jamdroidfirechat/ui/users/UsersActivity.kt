package com.ygorcesar.jamdroidfirechat.ui.users

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.util.Pair
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.appinvite.AppInviteInvitation
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.R.id.rv_users
import com.ygorcesar.jamdroidfirechat.R.id.toolbar
import com.ygorcesar.jamdroidfirechat.data.entity.ChatReference
import com.ygorcesar.jamdroidfirechat.data.entity.MapLocation
import com.ygorcesar.jamdroidfirechat.data.repository.local.AppDatabase
import com.ygorcesar.jamdroidfirechat.databinding.UsersActivityBinding
import com.ygorcesar.jamdroidfirechat.extensions._setLinearLayoutManager
import com.ygorcesar.jamdroidfirechat.extensions.provideViewModel
import com.ygorcesar.jamdroidfirechat.extensions.startActivityWithTransition
import com.ygorcesar.jamdroidfirechat.ui.BaseActivity
import com.ygorcesar.jamdroidfirechat.ui.messages.MessagesActivity
import com.ygorcesar.jamdroidfirechat.ui.preferences.PrefsActivity
import com.ygorcesar.jamdroidfirechat.utils.Constants
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.users_activity.*
import kotlinx.android.synthetic.main.users_item.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.error
import org.jetbrains.anko.startActivity


class UsersActivity : BaseActivity() {

    private val binding: UsersActivityBinding by lazy { UsersActivityBinding.inflate(layoutInflater) }
    private val viewModel: UsersViewModel by lazy { provideViewModel<UsersViewModel>() }
    private var usersEventDisposable: Disposable? = null
    private var sharedLocation: MapLocation? = null
    private var extraType = ""
    private var sharedArgs = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        doAsync { AppDatabase.getInstance(applicationContext).notificationDao().deleteUnreadNotifications() }

        intent?.apply {
            if (Intent.ACTION_SEND == action && !type.isNullOrEmpty()) {
                supportActionBar?.setTitle(R.string.title_select_an_user)
                if (type == Constants.IntentType.TEXT_PLAIN) {
                    sharedArgs = getStringExtra(Intent.EXTRA_TEXT)
                    extraType = Intent.EXTRA_TEXT
                }
                if (type.startsWith(Constants.IntentType.IMAGE)) {
                    sharedArgs = getParcelableExtra<Uri>(Intent.EXTRA_STREAM).toString()
                    extraType = Intent.EXTRA_STREAM
                }
            }

            extras?.apply {
                val userEmail = getString(Constants.KEY_USER_EMAIL)
                val chatKey = getString(Constants.KEY_CHAT_KEY)
                goToChat(userEmail, chatKey)
            }
        }

        setContentView(binding.root)

        setSupportActionBar(toolbar)
        setupRecyclerView()
        binding.viewmodel = viewModel
    }

    override fun onResume() {
        super.onResume()
        if (extraType.isEmpty()) supportActionBar?.setTitle(R.string.app_name)

        userEmail?.let {
            usersEventDisposable = viewModel.fetchUsers(it).subscribe({ viewModel.setUsers(it) }, { it.printStackTrace() })
        }
    }

    override fun onStop() {
        super.onStop()
        usersEventDisposable?.dispose()
    }

    private fun setupRecyclerView() {
        rv_users.apply {
            _setLinearLayoutManager(withDivider = true)
            adapter = UsersAdapter({ friendUser, view ->
                userEmail?.let {
                    viewModel.fetchReference(it, friendUser.email, { chatRef -> goToChat(chatRef, view) },
                            { error("Error on fetch chat key reference", it) })
                }
            })
        }
    }

    private fun goToChat(chatRef: ChatReference, itemView: View) {
        val img = Pair<View, String>(itemView.iv_user_photo, getString(R.string.transition_name_user_photo))
        val title = Pair<View, String>(itemView.tv_user_name, getString(R.string.transition_name_user_name))
        val intent = intentForMessageActivity(chatRef)

        startActivityWithTransition(intent, img, title)
    }

    private fun goToChat(userEmail: String?, chatKey: String?) {
        if (!userEmail.isNullOrEmpty() && !chatKey.isNullOrEmpty()) {
            val intent = intentForMessageActivity(ChatReference(userEmail!!, chatKey!!))
            startActivity(intent)
        }
    }

    private fun intentForMessageActivity(chatRef: ChatReference) = Intent(this, MessagesActivity::class.java).apply {
        putExtra(Constants.KEY_CHAT_EMAIL, chatRef.userEmail)
        putExtra(Constants.KEY_CHAT_KEY, chatRef.chatKey)
        if (extraType.isNotEmpty()) {
            putExtra(extraType, sharedArgs)
            putExtra(Constants.IntentType.KEY_EXTRA, extraType)
            sharedLocation?.let {
                putExtra(Constants.KEY_SHARED_LATITUDE, it.latitude)
                putExtra(Constants.KEY_SHARED_LONGITUDE, it.longitude)
            }
            clearSharedArgs()
        }
    }

    private fun clearSharedArgs() {
        extraType = ""
        sharedArgs = ""
        sharedLocation = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (extraType.isNotEmpty()) {
            menuInflater.inflate(R.menu.menu_shared_args, menu)
        } else {
            menuInflater.inflate(R.menu.menu_main, menu)
        }

        (menu.findItem(R.id.action_search).actionView as SearchView?)?.setOnQueryTextListener(viewModel.getOnQueryUser())
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity<PrefsActivity>()
            R.id.action_invite -> onInviteClicked()
            R.id.action_search -> return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onInviteClicked() {
        val intent = AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .build()
        startActivityForResult(intent, REQUEST_INVITE)
    }

    companion object {
        const val REQUEST_INVITE = 1
    }
}