package vnzla.jonasleon8.myapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leon on 14/01/2018.
 */

public class QueyEnd extends AppCompatActivity {


    ArrayList<Equipo> listaEquipos;
    ImageButton buttonConsulta, buttonApagar, buttonBorrar;
    TextView tvTmp, tvVol, tvEdo, tvIdEq, tvPc;
    String dp_email, dp_id_equipo,pc;


    private static String URL = "https://remote-admin.000webhostapp.com/query_end.php";
    private Snackbar snackbar;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queryend);

        pd = new ProgressDialog(QueyEnd.this);

            String idEquipoConsulta= getIntent().getStringExtra("idEquipoConsulta");
            String pcAconsultar= getIntent().getStringExtra("pcAconsultar");
            String correo= getIntent().getStringExtra("correo");

        dp_email = correo;
        dp_id_equipo = idEquipoConsulta;
        pc = pcAconsultar;

        buttonConsulta = (ImageButton) findViewById(R.id.buttonConsulta);
        buttonApagar = (ImageButton) findViewById(R.id.buttonApagar);
        buttonBorrar = (ImageButton) findViewById(R.id.buttonBorrar);

        tvTmp = (TextView) findViewById(R.id.valTmp);
        tvVol = (TextView) findViewById(R.id.valVol);
        tvEdo = (TextView) findViewById(R.id.valEdo);
        tvIdEq = (TextView) findViewById(R.id.id_equipo);
        tvPc = (TextView) findViewById(R.id.pc);

        buttonConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 consulta();
            }
        });

       buttonApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               apagar();
            }
        });

        buttonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                borrar();
            }
        });

    }



   private void consulta()
        {
            pd.setMessage("Consultado datos del equipo...");
            pd.show();

            RequestQueue queue = Volley.newRequestQueue(QueyEnd.this);
            String response = null;

            final String finalResponse = response;

            StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {

                            pd.hide();

                            try {

                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                String msg = jsonObject.getString("msg");
                                String st, stAux;

                                if ( status == 1 ) {

                                    JSONArray losEquipos = jsonObject.getJSONArray("data");
                                    st = losEquipos.getJSONObject(0).getString("flag_apagado");
                                    if ( st.equals("0") ) {
                                        stAux = "Estado: Encendido";
                                    } else {
                                        stAux = "Estado: Apagado";
                                    }

                                    tvIdEq.setText("Equipo: " + losEquipos.getJSONObject(0).getString("id_equipo") );
                                    tvPc.setText("Maquina: " + losEquipos.getJSONObject(0).getString("pc_usuario") );
                                    tvTmp.setText("Temperatura: " + losEquipos.getJSONObject(0).getString("temperatura") );
                                    tvVol.setText("Voltaje: " + losEquipos.getJSONObject(0).getString("voltaje") );
                                    tvEdo.setText(stAux);

                                } else {

                                    pd.hide();
                                    Toast.makeText(QueyEnd.this, "No se encontraron equipos", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            pd.hide();
                            Log.d("ErrorResponse", error.toString());

                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("correo",  dp_email );
                    params.put("equipo", dp_id_equipo);
                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(postRequest);

        }

    private void apagar()
    {
        final String URL2, user;

        URL2 = "https://remote-admin.000webhostapp.com/bandera.php";

        pd.setMessage("Apagando equipo...");
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(QueyEnd.this);
        String response = null;

        final String finalResponse = response;

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL2,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        pd.hide();
                        showSnackbar("Se ha apagado equipo!");
                        consulta();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.hide();
                        Log.d("ErrorResponse", error.toString());

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("user",   dp_email );
                params.put("pc", pc);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

    private void borrar(){

        final String URL3, user;
        pd.setMessage("Eliminando datos del equipo...");
        pd.show();

        URL3 = "https://remote-admin.000webhostapp.com/Delete.php";

        RequestQueue queue = Volley.newRequestQueue(QueyEnd.this);
        String response = null;

        final String finalResponse = response;

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL3,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        pd.hide();

                        showSnackbar("Equipo Eliminado!");

                        tvIdEq.setText(" ");
                        tvPc.setText(" ");
                        tvTmp.setText(" ");
                        tvVol.setText(" ");
                        tvEdo.setText(" ");
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        pd.hide();
                        Log.d("ErrorResponse", error.toString());

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("correo",  dp_email );
                params.put("equipo", dp_id_equipo);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

    public void showSnackbar(String stringSnackbar){
        snackbar.make(findViewById(android.R.id.content), stringSnackbar.toString(), Snackbar.LENGTH_LONG)
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                .show();


    }
}

