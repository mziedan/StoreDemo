package com.beekoding.storedemo;

/**
 * Created by moham on 2017-05-15.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.beekoding.storedemo.Adapter.RecyclerViewAdapter;
import com.beekoding.storedemo.Model.ProductImage;
import com.beekoding.storedemo.Model.ProductItem;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.beekoding.storedemo.Utils.ObjectToFileUtil.objectFromFile;
import static com.beekoding.storedemo.Utils.ObjectToFileUtil.objectToFile;

/**
 * Created by SONU on 25/09/15.
 */
public class RecyclerViewActivity extends AppCompatActivity {

    private static final String TAG = RecyclerViewActivity.class.getSimpleName();
    private static int currentPage = 1;
    private static RecyclerView recyclerView;
    private List<ProductItem> sList;
    private int pastVisiblesItems;
    private int visibleItemCount;
    private int totalItemCount;
    private SharedPreferences preferences;
    private RecyclerViewAdapter adapter;
    private final String FEED_URL = "http://grapesnberries.getsandbox.com/products?count=10&from=";
    private ProgressBar mProgressBar;
    private boolean loading = true;
    private boolean connected = false;

    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        initViews();
        sList = new ArrayList<>();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        adapter = new RecyclerViewAdapter(RecyclerViewActivity.this, sList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        viewProductDetails(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        if (!connected) {
            try {
                String filePath = preferences.getString("DATAPATH", "");
                String cachedResponse = (String) objectFromFile(filePath);
                parseResult(cachedResponse);
                populatRecyclerView();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                StaggeredGridLayoutManager manager =
                        (StaggeredGridLayoutManager) recyclerView.getLayoutManager();

                int[] firstVisibleItems = null;

                visibleItemCount = manager.getChildCount();
                totalItemCount = manager.getItemCount();
                firstVisibleItems = manager.findFirstVisibleItemPositions(firstVisibleItems);
                if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                    pastVisiblesItems = firstVisibleItems[0];
                }

                if (loading) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = false;
                        if (connected) {
                            new AsyncHttpTask().execute(FEED_URL + currentPage);
                        }
                    }
                }
            }
        });


        new AsyncHttpTask().execute(FEED_URL + currentPage);
        mProgressBar.setVisibility(View.VISIBLE);
        populatRecyclerView();
    }

    private String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }

    /**
     * Parsing the feed results and get the list
     *
     * @param result
     */
    private void parseResult(String result) {
        try {
            JSONArray products = new JSONArray(result);
            ProductItem item;
            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.optJSONObject(i);

                String description = product.optString("productDescription");
                double price = product.optDouble("price");
                int id = product.optInt("id");
                JSONObject image = product.optJSONObject("image");

                ProductImage pImage = new ProductImage();
                pImage.setUrl(image.optString("url"));
                pImage.setHeight(image.optInt("height"));
                pImage.setWidth(image.optInt("width"));

                item = new ProductItem();
                item.setId(id);
                item.setPrice(price);
                item.setProductDescription(description);
                item.setImage(pImage);

                sList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Initialize the view
    private void initViews() {
        recyclerView = (RecyclerView)
                findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        getSupportActionBar().setTitle("Store Demo");
        recyclerView
                .setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }


    // populate the list view by adding data to arraylist
    private void populatRecyclerView() {
        adapter.notifyDataSetChanged();// Notify the adapter

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void viewProductDetails(int position) {
        Intent intent = new Intent(RecyclerViewActivity.this, DetailsActivity.class);

        //Pass the image title and url to DetailsActivity
        intent.putExtra("width", sList.get(position).getImage().getWidth()).
                putExtra("height", sList.get(position).getImage().getHeight()).
                putExtra("productDescription", sList.get(position).getProductDescription()).
                putExtra("image", sList.get(position).getImage().getUrl()).
                putExtra("price", sList.get(position).getPrice());
        //Start details activity
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
            System.exit(0);
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3000);
        }
    }

    //Downloading data asynchronously
    private class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    try {
                        String dataPath = objectToFile(response);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("DATAPATH", dataPath);
                        editor.apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    parseResult(response);
                    result = 1; // Successful
                    currentPage = currentPage + 10;
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Lets update UI

            if (result == 1) {
                loading = true;
                populatRecyclerView();
            } else {
                Toast.makeText(RecyclerViewActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

            //Hide progressbar
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
