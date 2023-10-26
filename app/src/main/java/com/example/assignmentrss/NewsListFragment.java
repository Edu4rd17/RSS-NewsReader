package com.example.assignmentrss;

/**
 * @author Eduard Iacob
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NewsListFragment extends Fragment {

    // Initialize variable
    private static final String CHANNEL_ID = "RSS-NEWS";
    private static final int NOTIFICATION_ID = 1234;

    //declare a recycler view object
    RecyclerView recyclerView = null;
    // creating a variable for our Firebase Database.
    FirebaseDatabase firebaseDatabase;
    // creating a variable for our Database Reference for Firebase.
    DatabaseReference databaseReference;
    //private string of the url just for testing
    private String URL = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_news_list, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        //call the method for initializing the database
        initDatabase();
        //call the createNotificationChannel method
        createNotificationChannel();
        //url string variable that takes in the passed url from the subscribe fragment
        URL = getArguments().getString("URL");
        //call the method for loading the data from the database which checks every minute for new data
        setRepeatingAsyncTask();
    }

    //method which loads the data from the database
    private void loadPage() {
        // Create a new AsyncTask that downloads an XML feed from the web
        new DownloadXmlTask().execute(URL);
    }

    private void initDatabase() {
        // below line is used to get the instance of our Firebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();
        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference();
    }

    //method for saving the items from the network into the database
    private void saveItemsToDatabase(List<Item> items) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            items.forEach(item -> databaseReference.child("ItemsTable").child(item.guid).setValue(item));
        }
    }

    //method that checks for updates it takes in as an argument a list of items
    private void checkForUpdates(List<Item> itemsFromNetwork) {
        List<Item> databaseItems = new ArrayList<>();
        ;
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // add items into the list
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot item : singleSnapshot.getChildren()) {
                        Item myItem = item.getValue(Item.class);
                        databaseItems.add(myItem);
                    }
                }
                //check if the list that contains the items from the network (xml) is bigger than the list that contains the items from the database
                if (itemsFromNetwork.size() > databaseItems.size()) {
                    //if it does then we call the createNotification method
                    createNotification();
                }
                // For testing purpose save only 2 items in database so we always have more items on network and we can check if we receive any notification
//                saveItemsToDatabase(itemsFromNetwork.subList(0, 2));
                saveItemsToDatabase(itemsFromNetwork);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.getRef().addValueEventListener(postListener);
    }

    //method for displaying the items by using the newsListAdapter class
    private void displayItems(List<Item> items) {
        NewsListAdapter adapter = new NewsListAdapter(items);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);
    }

    //method for creating the notification
    private void createNotification() {
        // Create an explicit intent for the profile activity
        Intent intent = new Intent(this.getContext(), ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("RSS News")
                .setContentText("You have a new item to check")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.getContext());

        // notificationId is a unique int for each notification that must be defined
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = this.getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //method for setting the repeating async task
    private void setRepeatingAsyncTask() {
        // Create the Handler object
        final Handler handler = new Handler();
        // Create the Timer object
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Create the Runnable object
                handler.post(() -> {
                    try {
                        // Call the method for loading the data from the database
                        loadPage();
                    } catch (Exception e) {
                        // error, do something
                    }
                });
            }
        };
        // Schedule the TimerTask to run every 1 minute
        timer.schedule(task, 0, 60 * 1000);  // interval of one minute

    }

    // Uses AsyncTask to download the XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String, Void, List<Item>> {
        @Override
        protected List<Item> doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_LONG).show();
            } catch (XmlPullParserException e) {
                Toast.makeText(getContext(), "XML error", Toast.LENGTH_LONG).show();
            }
            return Collections.emptyList();
        }

        @Override
        protected void onPostExecute(List<Item> itemsFromNetwork) {
            displayItems(itemsFromNetwork);
            checkForUpdates(itemsFromNetwork);
            Log.d("TAG", "onPostExecute: " + itemsFromNetwork.toString());
        }

        // Uploads XML from stackoverflow.com, parses it, and combines it with HTML markup. Returns HTML string.
        private List<Item> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
            InputStream stream = null;
            // Instantiate the parser
            RssXmlParser rssXmlParser = new RssXmlParser();
            List<Item> entries = null;
            try {
                stream = downloadUrl(urlString);
                entries = rssXmlParser.parse(stream);
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return entries;
        }

        // Given a string representation of a URL, sets up a connection and gets an input stream.
        private InputStream downloadUrl(String urlString) throws IOException {
            java.net.URL url = new java.net.URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            return conn.getInputStream();
        }
    }
}

