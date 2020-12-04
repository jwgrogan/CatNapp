package edu.utap.catnapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import edu.utap.catnapp.ui.FavCats
import edu.utap.catnapp.ui.MainViewModel
import edu.utap.catnapp.ui.SelectCatsWrapper


class MainActivity : AppCompatActivity() {
    companion object {
        const val categoryKey = "categoryKey"
    }
    private var categoryMap = mapOf<String, String>("Hats" to "1", "Space" to "2", "Funny" to "3", "Sunglasses" to "4", "Boxes" to "5", "Caturday" to "6", "Ties" to "7", "Dream" to "9", "Sinks" to "14", "Clothes" to "15")
    private val RC_SIGN_IN = 123


    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0);
    }

    // https://stackoverflow.com/questions/24838155/set-onclick-listener-on-action-bar-title-in-android/29823008#29823008





    private fun initFavorites() {
        val initFavorites = findViewById<ImageView>(R.id.actionFavorite)
        initFavorites?.setOnClickListener{
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.main_frame, FavCats.newInstance())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
        }
    }

    private fun initSelectCats() {
        val content = findViewById<View>(R.id.content_main)
        val spinner = content.findViewById<Spinner>(R.id.categorySpinner)

        if (spinner.selectedItemPosition == 0) {
            val toast = Toast.makeText(this, "Choose a cat-egory!", Toast.LENGTH_LONG)
            toast.show()
        } else {
            // save category name for title
            MainViewModel.categoryName = spinner.getItemAtPosition(spinner.selectedItemPosition).toString()

            val getSelectCatsIntent = Intent(this, SelectCatsWrapper::class.java)
            val result = 1
            val myExtras = Bundle()
            myExtras.putString(categoryKey, categoryMap[spinner.getItemAtPosition(spinner.selectedItemPosition).toString()])
            getSelectCatsIntent.putExtras(myExtras)
            startActivityForResult(getSelectCatsIntent, result)
        }
    }

    private fun initActionBar(actionBar: ActionBar) {
        // Disable the default and enable the custom
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayShowCustomEnabled(true)
        val customView: View =
                layoutInflater.inflate(R.layout.action_bar, null)
        // Apply the custom view
        actionBar.customView = customView
    }

    private fun initToolbarUser() {
        val username = Firebase.auth.currentUser?.displayName
        val toolbarUsername = findViewById<TextView>(R.id.toolbarUsername)
        toolbarUsername?.text = "Hi, " + username.toString()
    }

    private fun initToolbarMenu() {
        // setup toolbar menu
        val toolbarMenu = findViewById<TextView>(R.id.actionMenu)
        toolbarMenu.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, toolbarMenu)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.actionSignIn ->
                        signIn()
                    R.id.actionSignOut ->
                        signOut()
                }
                true
            })
            popupMenu.show()
        }
    }

    private fun signIn() {
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
        )
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN
        )
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        MainViewModel.clearPhotos()
        val toolbarUsername = findViewById<TextView>(R.id.toolbarUsername)
        toolbarUsername?.text = "Please sign in"
    }

    // check if user has changed and for clearing top text
    override fun onResume() {
        super.onResume()
        initToolbarUser()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // sign in user with firebase if no user
        if (Firebase.auth.currentUser == null) {
            signIn()
        }

        // set up toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.let{
            initActionBar(it)
        }

        initToolbarUser()
        initToolbarMenu()
        initFavorites()

        // set up spinner
        val content = findViewById<View>(R.id.content_main)
        val spinner = content.findViewById<Spinner>(R.id.categorySpinner)
        val categoryTypeAdapter = ArrayAdapter.createFromResource(this,
            R.array.category_type,
            android.R.layout.simple_spinner_item)
        categoryTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = categoryTypeAdapter

        val button = content.findViewById<Button>(R.id.friendButton)
        button?.setOnClickListener {
            initSelectCats()
        }
    }



    // TODO: delete this?
    // NB: If you declare data: Intent, you get onActivityResult overrides nothing
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // XXX Write me
        data?.extras?.apply {
            // Not sure if this is needed at all
            // Maybe pass favourites back?
            // If we want the favourites heart in toolbar
            // On this page
        }
    }
}