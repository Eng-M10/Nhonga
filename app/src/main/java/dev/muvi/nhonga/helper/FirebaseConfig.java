package dev.muvi.nhonga.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class FirebaseConfig {



    private static DatabaseReference referenceFirebase;
    private static FirebaseAuth referenceAuth;
    private static StorageReference referenceStorage;



    public static DatabaseReference getReferenceFirebase() {
        if(referenceFirebase == null){
            referenceFirebase = FirebaseDatabase.getInstance().getReference();
        }

        return referenceFirebase;
    }

    public static FirebaseAuth getReferenceAuth() {
        if(referenceAuth == null){
            referenceAuth = FirebaseAuth.getInstance();
        }
        return referenceAuth;
    }

    public static StorageReference getReferenceStorage() {
        if (referenceStorage == null){
            referenceStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenceStorage;
    }



}
