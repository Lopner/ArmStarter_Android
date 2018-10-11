package armstarter.w_arm.ru.armstarter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


//import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.GraphViewSeries;
//import com.jjoe64.graphview.GraphView.GraphViewData;
//import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    List ListLayout;
    TextView NubersOfRepeat;
    SeekBar sk;



    void InitListLayoit(){
        ListLayout = new ArrayList();
        ListLayout.add(R.id.main_container);
        ListLayout.add(R.id.main_setting);
        ListLayout.add(R.id.main_statistic);
        ListLayout.add(R.id.main_about);
    }

    void SetOnlyOneLayoutVisible(int v){
        if (ListLayout.contains(v)){
            for (int i = 0; i < ListLayout.size(); i++) {
                findViewById((int)ListLayout.get(i)).setVisibility(View.GONE);
            }

            for (int i = 0; i < ListLayout.size(); i++) {
                if (v == (int)ListLayout.get(i)) {
                    findViewById((int) ListLayout.get(i)).setVisibility(View.VISIBLE);
                }
            }
        }
    }


    String[] counrty_data = {"Rus", "Ukr", "UK", "Can", "Bra"};
    String[] Sport_title_data = {"ЗМС", "МСМК", "МС", "КМС", "1 разряд", "б/р"};


    void DrawTop100(JSONObject jobj){
        //бновить список участников
    }

    class MyDownloadTask extends AsyncTask<String,String,String>
    {

        @Override
        protected void onPreExecute() {
            //display progress dialog.

        }
        @Override
        protected String doInBackground(String... params) {
            String resultString = "";

            try {

                String myURL="http://armstarter.w-arm.ru/getdatafromw-arm.php";
                String parammetrs = "email=forder-rv@mail.ru&param2=XXX";
                byte[] data = null;
                InputStream is = null;
                try {
                    URL url = new URL(myURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    conn.setRequestProperty("Content-Length", "" + Integer.toString(parammetrs.getBytes().length));
                    OutputStream os = conn.getOutputStream();
                    data = parammetrs.getBytes("UTF-8");
                    os.write(data);
                    data = null;

                    conn.connect();
                    int responseCode= conn.getResponseCode();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    if (responseCode == 200) {
                        is = conn.getInputStream();

                        byte[] buffer = new byte[8192]; // Такого вот размера буфер
                        // Далее, например, вот так читаем ответ
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }
                        data = baos.toByteArray();
                        resultString = new String(data, "UTF-8");

                        try {
                            JSONObject obj = new JSONObject(resultString);
                            DrawTop100(obj);
                            Log.d("My App", obj.toString());
                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + resultString + "\"");
                        }





                    } else {
                    }



                } catch (MalformedURLException e) {

                    resultString = "MalformedURLException:" + e.getMessage();
                } catch (IOException e) {

                    resultString = "IOException:" + e.getMessage();
                } catch (Exception e) {
                    resultString = "Exception:" + e.getMessage();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultString;
        }


        @Override
        protected void onPostExecute(String result) {
            // dismiss progress dialog and update ui
        }
    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
    private int getmarginDP(Context contecxt, int dpValue /*margin in dips*/){
        float d = contecxt.getResources().getDisplayMetrics().density;
        return (int)(dpValue * d); // margin in pixels
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new MyDownloadTask().execute();






        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NubersOfRepeat = (TextView) findViewById(R.id.textView_RepeatNumbers);
        InitListLayoit();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        int i = 1;
        // Линейный график
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 125),
                new DataPoint(1, 115),
                new DataPoint(2, 90),
                new DataPoint(3, 228),
                new DataPoint(4, 150)
        });
        series.setTitle("t");
        graph.addSeries(series);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 500),
                new DataPoint(1, 10),
                new DataPoint(2, 0),
                new DataPoint(3, 115),
                new DataPoint(4, 200)
        });
        series2.setTitle("Мощность");
        int RGB_series2 = android.graphics.Color.rgb(200, 100, 90);
        series2.setColor(RGB_series2);
        graph.addSeries(series2);

        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 300),
                new DataPoint(1, 250),
                new DataPoint(2, 100),
                new DataPoint(3, 120),
                new DataPoint(4, 200)
        });

        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("repeats");
       // gridLabel.setVerticalAxisTitle("msec");
        int RGB_series3 = android.graphics.Color.rgb(250, 140, 90);
        series3.setColor(RGB_series3);
        series3.setTitle("Реакция");
        graph.addSeries(series3);


        /////// рейтинг


        TableLayout tb = (TableLayout)findViewById(R.id.ratingtable);

        TableRow.LayoutParams TableRow_LP = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
       // View HorizontalDiveder =  (View)findViewById(R.id.rowdevider);


        LinearLayout.LayoutParams paramsDiveder = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        float indention = convertDpToPixel(20, this);
        paramsDiveder.setMargins((int)indention, 0, (int)indention, 0);





        for (int i1 = 0; i1 < 37; i1++) {



            View diveder = (View)getLayoutInflater().inflate(R.layout.diveder, null);
           // diveder.setLayoutParams(paramsDiveder);
           // setMargins(diveder, 20,0,20,0);
            TableRow row= (TableRow)getLayoutInflater().inflate(R.layout.tableow_statd, null);
           // row.setLayoutParams(TableRow_LP);
            LinearLayout vertLinearLay = new LinearLayout(this);
            vertLinearLay.setOrientation(LinearLayout.VERTICAL);

            vertLinearLay.addView(diveder);



          //  LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           // params.setMargins(5,5,5,5);


            TextView txtsample1 = (TextView) getLayoutInflater().inflate(R.layout.tableow_tx1, null);
            txtsample1.setText(String.valueOf(i1)+".");
           // txtsample1.setLayoutParams(params);

            TextView txtsample2 = new TextView(this);
            txtsample2.setLayoutParams(TableRow_LP);
            txtsample2.setText("Mister I. A.");
          //  txtsample2.setLayoutParams(params);


            TextView txtsample3 = new TextView(this);
            double newDouble = new BigDecimal(0.065*i1).setScale(3, RoundingMode.UP).doubleValue();
            txtsample3.setLayoutParams(TableRow_LP);
            txtsample3.setText(String.valueOf(newDouble) + " ms");
           // txtsample3.setLayoutParams(params);


            TextView txtsample4 = new TextView(this);
            txtsample4.setLayoutParams(TableRow_LP);
            txtsample4.setText("МС");
            //txtsample4.setLayoutParams(params);

            row.addView(txtsample1);
            row.addView(txtsample2);
            row.addView(txtsample3);
            row.addView(txtsample4);

            tb.addView(row);
            tb.addView(vertLinearLay);
            vertLinearLay.setLayoutParams(paramsDiveder);
        }

        /////////







//выпадающий список

        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, counrty_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Title");
        // выделяем элемент
        spinner.setSelection(2);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });



        // адаптер
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Sport_title_data);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner2 = (Spinner) findViewById(R.id.sporttitle_spinner);
        spinner2.setAdapter(adapter2);
        // заголовок
        spinner2.setPrompt("Title");
        // выделяем элемент
        spinner2.setSelection(2);
        // устанавливаем обработчик нажатия
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });





        sk=(SeekBar) findViewById(R.id.seekBar);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                int my_progress = sk.getProgress();
                NubersOfRepeat.setText(Integer.toString(my_progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub

               // Toast.makeText(getApplicationContext(), String.valueOf(progress),Toast.LENGTH_LONG).show();

            }
        });




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.




        int id = item.getItemId();

        if (id == R.id.nav_training) {
            SetOnlyOneLayoutVisible(R.id.main_container);
        } else if (id == R.id.nav_statistic) {
            SetOnlyOneLayoutVisible(R.id.main_statistic);
        } else if (id == R.id.nav_settings) {
            // Handle the camera action
            SetOnlyOneLayoutVisible(R.id.main_setting);
        } else if (id == R.id.nav_about) {
            SetOnlyOneLayoutVisible(R.id.main_about);
        } else if (id == R.id.nav_exit) {
            goExit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void goExit(){

    }
}
