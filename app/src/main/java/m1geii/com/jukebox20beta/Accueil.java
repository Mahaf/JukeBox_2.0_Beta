package m1geii.com.jukebox20beta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Accueil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_accueil);

        Button boutonDiffuseur=(Button)findViewById(R.id.boutonDiffuseur);
        Button boutonParticipant=(Button)findViewById(R.id.boutonParticipant);

        boutonDiffuseur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a=new Intent(Accueil.this,Mode_Diffuseur.class);
                startActivity(a);
            }
        });

        boutonParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b=new Intent(Accueil.this,Mode_Participant.class);
                startActivity(b);
            }
        });
    }
}
