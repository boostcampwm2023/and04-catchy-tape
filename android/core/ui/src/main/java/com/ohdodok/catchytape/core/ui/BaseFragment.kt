package com.ohdodok.catchytape.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
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

    protected fun showMessage(messageId: Int) {
        Snackbar.make(this.requireView(), messageId, Snackbar.LENGTH_LONG).show()
    }

    protected fun fetchCtErrorMessageId(ctErrorType: CtErrorType): Int {
        return when (ctErrorType) {
            CtErrorType.DUPLICATED_NICKNAME -> R.string.error_message_duplicated_nickname
            CtErrorType.WRONG_TOKEN -> R.string.error_message_wrong_token
            CtErrorType.BAD_REQUEST -> R.string.error_message_bad_request
            CtErrorType.SERVER -> R.string.error_message_server
            CtErrorType.CONNECTION -> R.string.error_message_connection
            CtErrorType.IO -> R.string.error_message_io
            CtErrorType.SSL_HAND_SHAKE -> R.string.error_message_ssl_hand_shake
        }
    }

}
