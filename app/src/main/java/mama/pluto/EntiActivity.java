package mama.pluto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import mama.pluto.view.EntiMainView;

public class EntiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new EntiMainView(this));
    }
}
