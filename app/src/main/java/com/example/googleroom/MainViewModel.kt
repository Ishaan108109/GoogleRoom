package com.example.googleroom
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository = UserRepository(application.applicationContext as Application)

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?>
        get() = _user

    fun onSignInClick(account: GoogleSignInAccount) {
        viewModelScope.launch {
            val user = userRepository.getUser(account.id.toString())
            if (user == null) {
                // If user doesn't exist, create a new user with the Google account info
                val newUser = User(
                    id = account.id.toString(),
                    name = account.displayName,
                    email = account.email,

                )
                userRepository.insert(newUser)
                _user.value = newUser
            } else {
                _user.value = user
            }
        }
    }

    fun onSignOutClick() {
        _user.value = null
    }
}
