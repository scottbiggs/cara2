package com.sleepfuriously.cara2

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.sleepfuriously.cara2.databinding.FragmentLoginLayoutBinding

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

//    private lateinit var loginViewModel: LoginViewModel
    private var _binding: FragmentLoginLayoutBinding? = null

    /** name of the user as they typed it in */
    private lateinit var mUserName : String

    /** password as typed in by user */
    private lateinit var mPassword : String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mCameraViewModel: CameraViewModel

    //---------------------
    //  widgets
    //---------------------

    private lateinit var mUserNameEt : EditText
    private lateinit var mPassEt : EditText

    private lateinit var mLoginButt : Button

    private lateinit var mProgressBar: ProgressBar

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


    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)
        Log.v(TAG, "onViewCreated()")

//        loginViewModel = ViewModelProvider(requireActivity(), LoginViewModelFactory())
//            .get(LoginViewModel::class.java)
//
        mCameraViewModel = ViewModelProvider(requireActivity()).get((CameraViewModel::class.java))

        requireActivity().title = getString(R.string.login_frag_title)

        (requireActivity() as MainActivity).setupCameraPermissions()

        // bind the widgets
        mUserNameEt = binding.usernameEt
        mPassEt = binding.passwordEt
        mLoginButt = binding.login
        mProgressBar = binding.loading

        //
        //  listeners
        //

        mLoginButt.setOnClickListener {
            if (goodLoginEntries()) {
                Log.d(TAG, "goodLoginEntries")
                mProgressBar.visibility = View.VISIBLE

                if (loginSuccessful()) {
                    Log.d(TAG, "loginSuccessful")
                    getDataFromUi()
                    updateUiWithUser()
                    saveDataToViewModel()
                    // continue to CameraFragment
                    findNavController().navigate(R.id.action_loginFragment_to_cameraFragment)
                }
                else {
                    Log.d(TAG, "login NOT successful")
                    // login problems
                    userAlert(R.string.login_frag_unable_to_login)
                    mProgressBar.visibility = View.GONE
                }
            }
            else {
                // problems with forms
                Log.d(TAG, "NOT good login entries")
                userAlert(R.string.login_frag_fill_in_forms)
            }
        }

//        loginViewModel.loginFormState.observe(viewLifecycleOwner,
//            Observer { loginFormState ->
//                if (loginFormState == null) {
//                    return@Observer
//                }
//                loginButton.isEnabled = loginFormState.isDataValid
//                loginFormState.usernameError?.let {
//                    usernameEditText.error = getString(it)
//                }
//                loginFormState.passwordError?.let {
//                    passwordEditText.error = getString(it)
//                }
//            })
//
//        loginViewModel.loginResult.observe(viewLifecycleOwner,
//            Observer { loginResult ->
//                loginResult ?: return@Observer
//                loadingProgressBar.visibility = View.GONE
//                loginResult.error?.let {
//                    showLoginFailed(it)
//                }
//
//                loginResult.success?.let {
//                    // Successful login, yay!
//                    updateUiWithUser(it)
//                    saveDataToViewModel()
//
//                    Log.d(TAG, "about to go to Camera Fragment. view model username = ${mCameraViewModel.mNameLiveData.value.toString()}")
//
//                    // add some data to the navigation system
//                    findNavController().navigate(R.id.action_loginFragment_to_cameraFragment)
//                }
//            })

//        loginButton.setOnClickListener {
//            mUserName = usernameEditText.text.toString()
//            loginViewModel.login(
//                mUserName,
//                passwordEditText.text.toString()
//            )
//        }

    } // onViewCreated()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //---------------------
    //  private functions
    //---------------------

    /**
     * Returns TRUE iff all the login forms have at least something to check.
     */
    private fun goodLoginEntries() : Boolean {
        if (mUserNameEt.text.isNotEmpty() && mPassEt.text.isNotEmpty()) {
            return true
        }
        return false
    }


    /**
     * Currently this is just a stub.
     * todo: actually do some checks for valid account, etc.
     */
    private fun loginSuccessful() : Boolean {
        return true
    }

    /**
     * Moves data from ui elements into our class variables.
     *
     * side effects:
     *  mUserName
     *  mPassword
     *
     */
    private fun getDataFromUi() {
        mUserName = mUserNameEt.text.toString()
        mPassword = mPassEt.text.toString()
    }

    /**
     * Does any UI changes once a successful login is completed.  In this case it's
     * simply display a toast.
     */
    private fun updateUiWithUser() {
        val welcome = getString(R.string.welcome) + " " + mUserName + "!"
        Toast.makeText(requireContext(), welcome, Toast.LENGTH_LONG).show()
    }

    /**
     * Displays the given string
     *
     * @param   strId   id of the string to display
     */
    private fun userAlert(strId : Int) {
        userAlert(getString(strId))
    }

    /**
     * Displays this string as an alert.
     * Right now this is just a toast
     */
    private fun userAlert(str : String) {
        Toast.makeText(requireContext(), str, Toast.LENGTH_LONG).show()
    }

//    private fun showLoginFailed(@StringRes errorString: Int) {
//        val appContext = context?.applicationContext ?: return
//        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
//    }


    /**
     * Saves important info to the CameraViewModel.
     */
    private fun saveDataToViewModel() {
        mCameraViewModel.mNameLiveData.value = mUserName
    }

}