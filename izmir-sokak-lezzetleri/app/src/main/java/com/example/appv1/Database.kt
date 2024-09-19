package com.example.appv1

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

fun addUser(email :String,password :String,country : String,point : Int){
    val database = FirebaseDatabase.getInstance("https://appv1-e33f4-default-rtdb.europe-west1.firebasedatabase.app")
    val userId = database.reference.child("users").push().key?:return
    val user = User(email,password,country,point)

    database.reference.child("users").child(userId).setValue(user)
        .addOnSuccessListener {
            println("users add")
        }
        .addOnFailureListener {
            println("users not add")
        }

}

fun signInUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
    val db = FirebaseDatabase.getInstance("https://appv1-e33f4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users")
    db.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                for (userSnapshot in snapshot.children) {
                    val storedPassword = userSnapshot.child("password").getValue(String::class.java)
                    if (storedPassword == password) {
                        onComplete(true, null) // Giriş başarılı
                        return
                    }
                }
                onComplete(false, "Kullanıcı adı veya şifre hatalı.")
            } else {
                onComplete(false, "Kullanıcı bulunamadı.")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            onComplete(false, error.message)
        }
    })
}


class UserViewModel : ViewModel() {
    private val _userPoints = MutableLiveData<Int>(0)
    val userPoints: LiveData<Int> get() = _userPoints

    fun fetchUserInfo(userEmail: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.reference.child("users").orderByChild("email").equalTo(userEmail)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.children.first().getValue(User::class.java)

                    _userPoints.value = user?.point ?: 0
                } else {

                    _userPoints.value = 0
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Hata durumu

                _userPoints.value = 0
            }
        })
    }
}


fun updateUserPointsByEmail(email: String?, points: Int, onComplete: (Boolean, String?) -> Unit) {
    if (email != null && email.isNotEmpty()) {
        // E-posta adresini kullanıcı kaydını bulmak için arama yap
        val usersRef = FirebaseDatabase.getInstance("https://appv1-e33f4-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("users")

        usersRef.orderByChild("email").equalTo(email).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val userKey = snapshot.children.first().key

                userKey?.let {
                    val userRef = usersRef.child(it)
                    userRef.child("point").get().addOnSuccessListener { pointSnapshot ->
                        val currentPoints = pointSnapshot.getValue(Int::class.java) ?: 0
                        userRef.child("point").setValue(currentPoints + points)
                            .addOnSuccessListener { onComplete(true, null) }
                            .addOnFailureListener { onComplete(false, it.message) }
                    }.addOnFailureListener { onComplete(false, it.message) }
                } ?: onComplete(false, "Kullanıcı bulunamadı")
            } else {
                onComplete(false, "Kullanıcı bulunamadı")
            }
        }.addOnFailureListener { onComplete(false, it.message) }
    } else {
        onComplete(false, "Email is null or empty")

    }
}

fun uploadPhotoToFirebase(bitmap: Bitmap, onSuccess: (String) -> Unit) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")

    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data = baos.toByteArray()

    // Yükleme işlemi
    val uploadTask = storageRef.putBytes(data)

    uploadTask.addOnSuccessListener {
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            onSuccess(uri.toString()) // URL'yi geri döndür
        }
    }.addOnFailureListener {
          println("yükleme hataası ")
    }
}












