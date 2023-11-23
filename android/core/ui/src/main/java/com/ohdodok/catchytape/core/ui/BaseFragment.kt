package com.ohdodok.catchytape.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewDataBinding>(
    @LayoutRes private val layoutId: Int
) : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun setupBackStack(toolbar: MaterialToolbar){
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    protected fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
        }
    }

    protected fun showMessage(@StringRes messageId: Int) {
        Snackbar.make(this.requireView(), messageId, Snackbar.LENGTH_LONG).show()
    }

    protected fun fetchCtErrorMessageId(ctErrorType: CtErrorType): Int {
        return when (ctErrorType) {
            CtErrorType.DUPLICATED_NICKNAME -> R.string.error_message_duplicated_nickname
            CtErrorType.WRONG_TOKEN -> R.string.error_message_wrong_token
            CtErrorType.SERVER -> R.string.error_message_server
            CtErrorType.CONNECTION -> R.string.error_message_connection
            CtErrorType.IO -> R.string.error_message_io
            CtErrorType.SSL_HAND_SHAKE -> R.string.error_message_ssl_hand_shake
            CtErrorType.UN_KNOWN -> R.string.error_message_un_known
            CtErrorType.NOT_EXIST_PLAYLIST_ON_USER -> R.string.error_message_not_exist_playlist_on_user
            CtErrorType.NOT_EXIST_MUSIC -> R.string.error_message_not_exist_music
            CtErrorType.ALREADY_ADDED -> R.string.error_message_already_added
            CtErrorType.INVALID_INPUT_VALUE -> R.string.error_message_invalid_input_value
            CtErrorType.NOT_EXIST_USER -> R.string.error_message_not_exist_user
            CtErrorType.ALREADY_EXIST_EMAIL -> R.string.error_message_already_exist_email
            CtErrorType.NOT_EXIST_GENRE -> R.string.error_message_not_exist_genre
            CtErrorType.EXPIRED_TOKEN -> R.string.error_message_expired_token
            CtErrorType.SERVICE -> R.string.error_message_service
        }
    }

}