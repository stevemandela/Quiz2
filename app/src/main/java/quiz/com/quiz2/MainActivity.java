package quiz.com.quiz2;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import quiz.com.quiz2.Model.User;

public class MainActivity extends AppCompatActivity {

    MaterialEditText edtNewUser, edtNewPassword, edtNewEmail;
    MaterialEditText edtUser, edtPassword;

    Button btnSignUp, btnSignIn;

    FirebaseDatabase database;
    DatabaseReference users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        edtUser = (MaterialEditText)findViewById(R.id.edtUser);
        edtPassword = (MaterialEditText)findViewById(R.id.edtPassword);

        btnSignIn = (Button)findViewById(R.id.btn_sign_in);
        btnSignUp = (Button)findViewById(R.id.btn_sign_up);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(edtUser.getText().toString(), edtPassword.getText().toString());
            }



        });

    }



    private void showSignUpDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Sign Up");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View sign_up_layout = inflater.inflate(R.layout.sign_up_layout,null);

        edtNewUser = (MaterialEditText)sign_up_layout.findViewById(R.id.edtNewUserName);
        edtNewEmail = (MaterialEditText)sign_up_layout.findViewById(R.id.edtNewEmail);
        edtNewPassword= (MaterialEditText)sign_up_layout.findViewById(R.id.edtNewPassword);

        alertDialog.setView(sign_up_layout);
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);

        alertDialog.setNegativeButton("NO", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        alertDialog.setPositiveButton("YES", (dialogInterface, which) -> {

            User user = new User(edtNewUser.getText().toString(),
                    edtNewPassword.getText().toString(),
                    edtNewEmail.getText().toString());


            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(user.getUserName()).exists())
                        Toast.makeText(MainActivity.this, "User already exists", Toast.LENGTH_SHORT).show();

                    else{

                        users.child(user.getUserName()).setValue(user);
                        Toast.makeText(MainActivity.this, "User registration success", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            dialogInterface.dismiss();

        });
        alertDialog.show();
    }

    private void signIn(String user, String pwd) {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user).exists())
                {
                    if (!user.isEmpty())
                    {
                        User login = dataSnapshot.child(user).getValue(User.class);
                        if (login.getPassword().equals(pwd))
                            Toast.makeText(MainActivity.this, "Login ok", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, "wrong password", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Please enter your user name", Toast.LENGTH_SHORT).show();

                    }

                }
                else
                {
                    Toast.makeText(MainActivity.this, "user is ot exists!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
