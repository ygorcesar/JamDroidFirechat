package com.ygorcesar.jamdroidfirechat.ui.users

import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.data.entity.ChatReference
import com.ygorcesar.jamdroidfirechat.databinding.UsersActivityBinding
import com.ygorcesar.jamdroidfirechat.ui.BaseActivity
import com.ygorcesar.jamdroidfirechat.ui.preferences.PrefsActivity
import com.ygorcesar.jamdroidfirechat.utils._setLinearLayoutManager
import com.ygorcesar.jamdroidfirechat.utils.provideViewModel
import kotlinx.android.synthetic.main.users_activity.*
import org.jetbrains.anko.error
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class UsersActivity : BaseActivity() {

    private val binding: UsersActivityBinding by lazy { UsersActivityBinding.inflate(layoutInflater) }
    private val viewModel: UsersViewModel by lazy { provideViewModel<UsersViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(toolbar)
        setupRecyclerView()
        binding.viewmodel = viewModel

        userEmail?.let {
            viewModel.fetchUsers(it).subscribe({ viewModel.setUsers(it) }, { it.printStackTrace() })
        }
    }

    private fun setupRecyclerView() {
        rv_users.apply {
            _setLinearLayoutManager(withDivider = true)
            adapter = UsersAdapter({ friendUser ->
                val chatReference = viewModel.shouldFetchChatReference(friendUser.email)
                if (chatReference == null) {
                    userEmail?.let { loggedEmail ->
                        viewModel.fetchChatReference(loggedEmail, friendUser.email)
                                .subscribe({ chatKey -> goToChat(viewModel.saveChatReference(friendUser.email, chatKey)) },
                                        { err ->
                                            if (err is NullPointerException) {
                                                goToChat(viewModel.createChatReference(loggedEmail, friendUser.email))
                                            } else {
                                                error("Error on get chat key!", err)
                                            }
                                        })
                    }
                } else {
                    goToChat(chatReference)
                }
            })
        }
    }

    private fun goToChat(chatRef: ChatReference) {
        toast("Friend Email: ${chatRef.userEmail}, KEY: ${chatRef.chatKey}")
        //TODO
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.action_attach)?.isVisible = false

        val searchView = (menu.findItem(R.id.action_search).actionView as SearchView)
        searchView.setOnQueryTextListener(viewModel.getOnQueryUser())
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity<PrefsActivity>()
            R.id.action_search -> return true
        }
        return super.onOptionsItemSelected(item)
    }
}