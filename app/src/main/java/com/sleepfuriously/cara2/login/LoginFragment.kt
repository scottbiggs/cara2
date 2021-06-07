package com.sleepfuriously.cara2.login

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.sleepfuriously.cara2.CameraViewModel
import com.sleepfuriously.cara2.MainActivity
import com.sleepfuriously.cara2.databinding.FragmentLoginLayoutBinding

import com.sleepfuriously.cara2.R

class LoginFragment : Fragment() {

    //---------------------
    //  constants
    //---------------------

    companion object {
        private const val TAG = "LoginFragment"
    }


    //---------------------
    //  data
    //---------------------

    private lateinit var loginViewModel: LoginViewModel
    private var _binding: FragmentLoginLayoutBinding? = null

    /** name of the user as they typed it in */
    private lateinit var mUserName : String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    //---------------------
    //  overridden functions
    //---------------------

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v(TAG, "onViewCreated()")

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        requireActivity().title = getString(R.string.login_frag_title)

        val usernameEditText = binding.usernameEt
        val passwordEditText = binding.password
        val loginButton = binding.login
        val loadingProgressBar = binding.loading

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let {
                    showLoginFailed(it)
                }

                loginResult.success?.let {
                    // Successful login, yay!
                    updateUiWithUser(it)
                    saveDataToViewModel()
                    findNavController().navigate(R.id.action_loginFragment_to_cameraFragment)
                }
            })

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            mUserName = usernameEditText.text.toString()
            loginViewModel.login(
                mUserName,
                passwordEditText.text.toString()
            )
        }

        (requireActivity() as MainActivity).setupCameraPermissions()
    } // onViewCreated()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //---------------------
    //  private functions
    //---------------------

    /**
     * Does any UI changes once a successful login is completed.  In this case it's
     * simply display a toast.
     */
    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.displayName
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }


    /**
     * Saves important info to the CameraViewModel.
     */
    private fun saveDataToViewModel() {
        val cameraViewModel = ViewModelProvider(this).get(CameraViewModel::class.java)
        cameraViewModel.mNameLiveData.value = mUserName
    }
}