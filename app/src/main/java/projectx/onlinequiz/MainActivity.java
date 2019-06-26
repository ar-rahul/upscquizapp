package projectx.onlinequiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
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

import org.apache.commons.validator.routines.EmailValidator;

import projectx.onlinequiz.models.User;


public class MainActivity extends AppCompatActivity {

    MaterialEditText enterNewUser, enterNewPassword, enterNewEmail; // for signup
    MaterialEditText enterUser,enterPassword; // for signin


    Button btnSignup, btnSignIn;

    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        enterUser = (MaterialEditText)findViewById(R.id.enterUserName);
        enterPassword = (MaterialEditText)findViewById(R.id.enterPassword);


        btnSignIn = (Button)findViewById(R.id.btn_sign_in);
        btnSignup = (Button)findViewById(R.id.btn_sign_up);


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignUpDialog();



            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            signIn(enterUser.getText().toString(),enterPassword.getText().toString());

            }
        });

    }

    private void signIn(final String user, final String pwd) {

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user).exists()){

                    if(!user.isEmpty()){

                        String hashedPassword = (String) HashPassword.main(pwd);


                        User login = dataSnapshot.child(user).getValue(User.class);
                        if(login.getPassword().equals(hashedPassword)) {

                            Intent homeActivity = new Intent(MainActivity.this,Home.class);
                            startActivity(homeActivity);
                            finish();


                        }else
                            Toast.makeText(MainActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();

                    }else{

                        Toast.makeText(MainActivity.this, "Please enter your username", Toast.LENGTH_SHORT).show();


                    }
                }else {

                    Toast.makeText(MainActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void showSignUpDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Sign Up");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View sign_up_layout = inflater.inflate(R.layout.sign_up_layout,null);

        enterNewUser = (MaterialEditText)sign_up_layout.findViewById(R.id.enterNewUserName);
        enterNewPassword = (MaterialEditText)sign_up_layout.findViewById(R.id.enterNewPassword);
        enterNewEmail = (MaterialEditText)sign_up_layout.findViewById(R.id.enterNewEmail);

        alertDialog.setView(sign_up_layout);
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });

        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

               boolean evalid = EmailValidator.getInstance().isValid(enterNewEmail.getText().toString());

               if (
                        !(enterNewUser.getText().toString().isEmpty()) &&
                                !(enterNewPassword.getText().toString().isEmpty()) &&
                                (evalid == true)){

                   String hashedPassword = (String) HashPassword.main(enterNewPassword.getText().toString());

                    final User user = new User(enterNewUser.getText().toString(),
                            hashedPassword,
                            enterNewEmail.getText().toString());


                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(user.getUserName()).exists()) {

                            Toast.makeText(MainActivity.this, "User already exist!", Toast.LENGTH_SHORT).show();

                        } else {
                            users.child(user.getUserName())
                                    .setValue(user);
                            Toast.makeText(MainActivity.this, "Usert registration success!!", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                dialogInterface.dismiss();

                } else {

                   if (
                           (enterNewUser.getText().toString().isEmpty()) ||
                           (enterNewPassword.getText().toString().isEmpty()) ||
                           (enterNewEmail.getText().toString().isEmpty()))
                    Toast.makeText(MainActivity.this, "Please enter full information!", Toast.LENGTH_SHORT).show();
                   else
                    Toast.makeText(MainActivity.this, "Invalid Email ID!", Toast.LENGTH_SHORT).show();


                   showSignUpDialog();
                }
            }
        });

        alertDialog.show();
    }

}
