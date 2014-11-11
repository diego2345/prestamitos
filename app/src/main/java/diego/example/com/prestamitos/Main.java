package diego.example.com.prestamitos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;


public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnList = (Button)findViewById(R.id.btnList);
        Button btnAdd = (Button)findViewById(R.id.btnAdd);
        Button btnExit = (Button)findViewById(R.id.btnExit);

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentList = new Intent(Main.this, ActivityLista.class);
                //intentList.putExtra("activity", "list");
                startActivity(intentList);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAdd = new Intent(Main.this, CreateEditActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("activity", "main");
                intentAdd.putExtras(bundle1);
                //intentAdd.putExtra("activity", "main");
                startActivity(intentAdd);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }



}
