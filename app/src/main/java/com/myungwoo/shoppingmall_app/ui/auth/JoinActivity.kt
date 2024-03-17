package com.myungwoo.shoppingmall_app.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.myungwoo.shoppingmall_app.ui.MainActivity
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.joinBtn.setOnClickListener {
            var isGoToJoin = true
            val email = binding.emailArea.text.toString()
            val password1 = binding.passwordArea1.text.toString()
            val password2 = binding.passwordArea2.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, R.string.join_verify_email, Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }

            if (password1.isEmpty()) {
                Toast.makeText(this, R.string.join_verify_pw, Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }

            if (password2.isEmpty()) {
                Toast.makeText(this, R.string.join_verify_pw_confirm, Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }

            if (!password1.equals(password2)) {
                Toast.makeText(this, R.string.join_verify_pw_fail, Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }

            if (password1.length < 6) {
                Toast.makeText(this, R.string.join_verify_pw_pattern, Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }

            if (isGoToJoin) {
                auth.createUserWithEmailAndPassword(email, password1)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)

                        } else {
                            Toast.makeText(this, R.string.join_auth_fail, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    }
}