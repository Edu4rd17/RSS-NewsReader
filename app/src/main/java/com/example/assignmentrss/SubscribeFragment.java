package com.example.assignmentrss;

/**
 * @author Eduard Iacob
 */

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

public class SubscribeFragment extends Fragment {

    // Initialize buttons
    Button subscribeButton;
    Button logoutButton;
    TextView userName;
    TextView userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_subscribe, container, false);
        //get the reference of Button
        subscribeButton = rootView.findViewById(R.id.button_subscribe);
        logoutButton = rootView.findViewById(R.id.button_logout);
        userName = rootView.findViewById(R.id.userName);
        userEmail = rootView.findViewById(R.id.userEmail);

        //get the details of the signed in user
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            String personName = acct.getDisplayName();
//            String personGivenName = acct.getGivenName();
//            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            // set the text fields to the details of the user
            userName.setText(personName);
            userEmail.setText(personEmail);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set on click listener
        subscribeButton.setOnClickListener(buttonView -> {
            // open input text view to insert the URL
            openDialogWithInputText();
        });

        // Set on click listener
        logoutButton.setOnClickListener(logoutButton -> {
            // Logout user
            logout();
        });
    }

    // This method is used to log out the user when the button is pressed
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        // Redirect to main activity
        Intent intent = new Intent(this.getContext(), MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // This method is used to open dialog with input text
    private void openDialogWithInputText() {
        final EditText txtUrl = new EditText(this.getContext());

        // Set the default text to a link of google
        txtUrl.setHint("http://www.google.com");

        // Initialize the alert dialog
        new AlertDialog.Builder(this.getContext())
                .setTitle("Subscribe here")
                .setMessage("Please paste in the xml link of a news feed")
                .setView(txtUrl)
                .setPositiveButton("Download", (dialog, whichButton) -> {
                    // Get the text from the input text
                    String url = txtUrl.getText().toString();
                    // Check if the url is empty
                    if (url.isEmpty() || !Patterns.WEB_URL.matcher(url).matches()) {
                        Toast.makeText(getContext(), "Please enter a valid URL link!", Toast.LENGTH_SHORT).show();
                    } else {
                        Bundle bundle = new Bundle();
                        // Put the url in the bundle
                        bundle.putString("URL", url);
                        // create a new fragment and set the bundle as arguments
                        NewsListFragment newsListFragment = new NewsListFragment();
                        newsListFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, newsListFragment, "findThisFragment")
                                .addToBackStack(null)
                                .commit();
                    }
                })
                .setNegativeButton("Cancel", (dialog, whichButton) -> {
                })
                .show();
    }
}