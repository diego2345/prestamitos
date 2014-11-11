package diego.example.com.prestamitos;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class ActivityLista extends ListActivity {

    protected ListView lv;
    protected ArrayList<String> array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        lv = (ListView)findViewById(android.R.id.list);

        rellenarListView();

        Button btnBack = (Button)findViewById(R.id.btnBackList);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLista.this.finish();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent B = new Intent(ActivityLista.this, CreateEditActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("activity", "list");
                bundle1.putString("file", array.get(i).toString());
                B.putExtras(bundle1);
                /*B.putExtra("activity", "list");
                B.putExtra("file", asd.getText().toString());*/
                startActivity(B);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int i, long l) {
                new AlertDialog.Builder(ActivityLista.this)
                        .setTitle("DELETE")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                deleteLoan(array.get(i).toString());
                                rellenarListView();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });
    }

    public void deleteLoan(String note) {
        File file = new File(getFilesDir(),note);
        file.delete();
    }

    @Override
    protected void onResume () {
        super.onResume();
        rellenarListView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        rellenarListView();
    }

    protected void rellenarListView() {
        File dir = getFilesDir();
        array = new ArrayList<String>();
        for (String item : dir.list()) {
            array.add(item);
        }

        lv.setAdapter(new PrestamosAdapter(this, array, dir));
    }

}
